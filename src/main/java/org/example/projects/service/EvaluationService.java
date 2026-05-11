package org.example.projects.service;

import java.util.ArrayList;
import java.util.List;

import org.example.projects.dto.EvaluationForm;
import org.example.projects.dto.EquipmentSelectionForm;
import org.example.projects.entity.AcademicEvaluation;
import org.example.projects.entity.BorrowingDetail;
import org.example.projects.entity.BorrowingRecord;
import org.example.projects.entity.BorrowingRecordStatus;
import org.example.projects.entity.Equipment;
import org.example.projects.entity.MentoringSession;
import org.example.projects.entity.MentoringSessionStatus;
import org.example.projects.exception.BusinessException;
import org.example.projects.repository.AcademicEvaluationRepository;
import org.example.projects.repository.BorrowingDetailRepository;
import org.example.projects.repository.BorrowingRecordRepository;
import org.example.projects.repository.EquipmentRepository;
import org.example.projects.repository.MentoringSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final MentoringSessionService mentoringSessionService;
    private final MentoringSessionRepository sessionRepository;
    private final AcademicEvaluationRepository evaluationRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public void evaluateSession(Long lecturerId, EvaluationForm form) {
        MentoringSession session = mentoringSessionService.getPendingSessionForLecturer(lecturerId, form.getSessionId());
        if (session.getStatus() != MentoringSessionStatus.CONFIRMED) {
            throw new BusinessException("Lịch hẹn chưa được duyệt, không thể đánh giá");
        }
        if (evaluationRepository.findBySessionId(session.getId()).isPresent()) {
            throw new BusinessException("Lịch hẹn đã được đánh giá trước đó");
        }

        List<EquipmentSelectionForm> selections = form.getSelections() == null ? List.of() : form.getSelections();
        List<EquipmentSelectionForm> validSelections = selections.stream()
                .filter(item -> item.getEquipmentId() != null && item.getQuantity() != null && item.getQuantity() > 0)
                .toList();
        if (validSelections.isEmpty()) {
            throw new BusinessException("Vui lòng chọn ít nhất một thiết bị cần cấp");
        }

        AcademicEvaluation evaluation = new AcademicEvaluation();
        evaluation.setSession(session);
        evaluation.setEvaluationText(form.getEvaluationText());
        evaluationRepository.save(evaluation);

        BorrowingRecord record = new BorrowingRecord();
        record.setSession(session);
        record.setStatus(BorrowingRecordStatus.PENDING_ISSUE);
        borrowingRecordRepository.save(record);

        List<BorrowingDetail> details = new ArrayList<>();
        for (EquipmentSelectionForm selection : validSelections) {
            Equipment equipment = equipmentRepository.findById(selection.getEquipmentId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy thiết bị"));
            BorrowingDetail detail = new BorrowingDetail();
            detail.setRecord(record);
            detail.setEquipment(equipment);
            detail.setQuantity(selection.getQuantity());
            details.add(detail);
        }
        borrowingDetailRepository.saveAll(details);

        session.setStatus(MentoringSessionStatus.COMPLETED);
        sessionRepository.save(session);
    }
}

