package org.example.projects.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.projects.dto.BookingForm;
import org.example.projects.dto.BorrowingDetailView;
import org.example.projects.dto.PendingSessionDto;
import org.example.projects.dto.SessionHistoryDto;
import org.example.projects.entity.AcademicEvaluation;
import org.example.projects.entity.BorrowingDetail;
import org.example.projects.entity.BorrowingRecord;
import org.example.projects.entity.BorrowingRecordStatus;
import org.example.projects.entity.Department;
import org.example.projects.entity.Equipment;
import org.example.projects.entity.MentoringSession;
import org.example.projects.entity.MentoringSessionStatus;
import org.example.projects.entity.UserAccount;
import org.example.projects.entity.UserRole;
import org.example.projects.entity.UserProfile;
import org.example.projects.exception.BusinessException;
import org.example.projects.repository.AcademicEvaluationRepository;
import org.example.projects.repository.BorrowingDetailRepository;
import org.example.projects.repository.BorrowingRecordRepository;
import org.example.projects.repository.EquipmentRepository;
import org.example.projects.repository.MentoringSessionRepository;
import org.example.projects.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentoringSessionService {

    private final MentoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AcademicEvaluationRepository evaluationRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public MentoringSession bookSession(Long studentId, BookingForm form) {
        UserAccount student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy sinh viên"));
        UserAccount lecturer = userRepository.findById(form.getLecturerId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy giảng viên"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new BusinessException("Chỉ sinh viên mới được đặt lịch");
        }
        if (lecturer.getRole() != UserRole.LECTURER) {
            throw new BusinessException("Người được chọn không phải giảng viên");
        }
        if (form.getDepartmentId() == null || lecturer.getDepartment() == null
                || !form.getDepartmentId().equals(lecturer.getDepartment().getId())) {
            throw new BusinessException("Giảng viên không thuộc khoa/ngành đã chọn");
        }

        if (form.getSessionDate() == null || form.getSessionTime() == null) {
            throw new BusinessException("Vui lòng chọn ngày và giờ");
        }

        LocalDateTime sessionTime = LocalDateTime.of(form.getSessionDate(), form.getSessionTime());
        if (sessionTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Không thể đặt lịch ở quá khứ");
        }
        if (sessionRepository.existsConflict(lecturer.getId(), sessionTime)) {
            throw new BusinessException("Giảng viên đã có lịch trong khung giờ này");
        }

        MentoringSession session = new MentoringSession();
        session.setStudent(student);
        session.setLecturer(lecturer);
        session.setSessionTime(sessionTime);
        session.setStatus(MentoringSessionStatus.PENDING);
        return sessionRepository.save(session);
    }

    @Transactional
    public void cancelSession(Long studentId, Long sessionId) {
        MentoringSession session = sessionRepository.findByIdAndStudentId(sessionId, studentId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lịch hẹn"));
        if (session.getStatus() == MentoringSessionStatus.CANCELED) {
            throw new BusinessException("Lịch đã được hủy trước đó");
        }
        if (session.getStatus() == MentoringSessionStatus.COMPLETED) {
            throw new BusinessException("Không thể hủy lịch đã hoàn thành");
        }
        if (session.getSessionTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BusinessException("Chỉ được hủy trước tối thiểu 24 giờ");
        }
        session.setStatus(MentoringSessionStatus.CANCELED);
        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<SessionHistoryDto> getStudentHistory(Long studentId) {
        List<MentoringSession> sessions = sessionRepository.findByStudentIdOrderBySessionTimeDesc(studentId);
        List<SessionHistoryDto> history = new ArrayList<>();
        for (MentoringSession session : sessions) {
            SessionHistoryDto dto = new SessionHistoryDto();
            dto.setSessionId(session.getId());
            dto.setSessionTime(session.getSessionTime());
            dto.setSessionStatus(session.getStatus());
            if (session.getLecturer() != null) {
                dto.setLecturerName(resolveFullName(session.getLecturer()));
                dto.setLecturerDepartment(session.getLecturer().getDepartment() != null
                        ? session.getLecturer().getDepartment().getName()
                        : null);
            }

            evaluationRepository.findBySessionId(session.getId())
                    .ifPresent(evaluation -> dto.setEvaluationText(evaluation.getEvaluationText()));

            borrowingRecordRepository.findBySessionId(session.getId()).ifPresent(record -> {
                dto.setBorrowingStatus(record.getStatus());
                List<BorrowingDetail> details = borrowingDetailRepository.findByRecordId(record.getId());
                List<BorrowingDetailView> borrowedItems = new ArrayList<>();
                for (BorrowingDetail detail : details) {
                    borrowedItems.add(new BorrowingDetailView(
                            detail.getEquipment() != null ? detail.getEquipment().getName() : "",
                            detail.getQuantity()));
                }
                dto.setBorrowedItems(borrowedItems);
            });

            history.add(dto);
        }
        return history;
    }

    @Transactional(readOnly = true)
    public List<PendingSessionDto> getPendingSessionsForLecturer(Long lecturerId) {
        List<MentoringSession> sessions = sessionRepository.findByLecturerIdAndStatusOrderBySessionTimeAsc(
                lecturerId, MentoringSessionStatus.PENDING);
        List<PendingSessionDto> result = new ArrayList<>();
        for (MentoringSession session : sessions) {
            UserAccount student = session.getStudent();
            result.add(new PendingSessionDto(
                    session.getId(),
                    resolveFullName(student),
                    student.getUsername(),
                    resolveFullName(session.getLecturer()),
                    session.getSessionTime(),
                    session.getStatus()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<PendingSessionDto> getConfirmedSessionsForLecturer(Long lecturerId) {
        List<MentoringSession> sessions = sessionRepository.findByLecturerIdAndStatusOrderBySessionTimeAsc(
                lecturerId, MentoringSessionStatus.CONFIRMED);
        List<PendingSessionDto> result = new ArrayList<>();
        for (MentoringSession session : sessions) {
            UserAccount student = session.getStudent();
            result.add(new PendingSessionDto(
                    session.getId(),
                    resolveFullName(student),
                    student.getUsername(),
                    resolveFullName(session.getLecturer()),
                    session.getSessionTime(),
                    session.getStatus()));
        }
        return result;
    }

    @Transactional
    public void confirmSession(Long lecturerId, Long sessionId) {
        MentoringSession session = sessionRepository.findByIdAndLecturerId(sessionId, lecturerId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lịch hẹn"));
        if (session.getStatus() != MentoringSessionStatus.PENDING) {
            throw new BusinessException("Chỉ có lịch đang chờ mới được duyệt");
        }
        session.setStatus(MentoringSessionStatus.CONFIRMED);
        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public MentoringSession getPendingSessionForLecturer(Long lecturerId, Long sessionId) {
        MentoringSession session = sessionRepository.findByIdAndLecturerId(sessionId, lecturerId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lịch hẹn"));
        return session;
    }

    private String resolveFullName(UserAccount user) {
        UserProfile profile = user.getProfile();
        if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
            return profile.getFullName();
        }
        return user.getUsername();
    }
}

