package org.example.projects.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.example.projects.dto.EvaluationForm;
import org.example.projects.dto.EquipmentSelectionForm;
import org.example.projects.dto.PendingSessionDto;
import org.example.projects.entity.MentoringSession;
import org.example.projects.exception.BusinessException;
import org.example.projects.security.CurrentUserDetails;
import org.example.projects.service.EquipmentService;
import org.example.projects.service.EvaluationService;
import org.example.projects.service.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecturer")
public class LecturerController {

    private final MentoringSessionService mentoringSessionService;
    private final EvaluationService evaluationService;
    private final EquipmentService equipmentService;

    @GetMapping("/sessions")
    public String pendingSessions(@AuthenticationPrincipal CurrentUserDetails principal, Model model) {
        java.util.List<PendingSessionDto> sessions = mentoringSessionService
                .getPendingSessionsForLecturer(principal.getId());
        model.addAttribute("sessions", sessions == null ? new ArrayList<>() : sessions);
        return "lecturer/sessions";
    }

    @GetMapping("/sessions/confirmed")
    public String confirmedSessions(@AuthenticationPrincipal CurrentUserDetails principal, Model model) {
        java.util.List<PendingSessionDto> confirmed = mentoringSessionService.getConfirmedSessionsForLecturer(principal.getId());
        model.addAttribute("confirmedSessions", confirmed == null ? new ArrayList<>() : confirmed);
        return "lecturer/confirmed-sessions";
    }

    @GetMapping("/sessions/{sessionId}/evaluate")
    public String evaluateForm(@AuthenticationPrincipal CurrentUserDetails principal,
            @PathVariable("sessionId") Long sessionId,
            Model model) {
        try {
            MentoringSession session = mentoringSessionService.getPendingSessionForLecturer(principal.getId(),
                    sessionId);

            // only allow evaluation for sessions that have been confirmed by lecturer
            if (session.getStatus() != org.example.projects.entity.MentoringSessionStatus.CONFIRMED) {
                // redirect back with message
                model.addAttribute("error", "Phiên tư vấn chưa được duyệt, không thể đánh giá");
                return "redirect:/lecturer/sessions";
            }

            // Tính tên sinh viên để hiển thị
            String studentName = "Không xác định";
            if (session.getStudent() != null) {
                org.example.projects.entity.UserProfile profile = session.getStudent().getProfile();
                if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
                    studentName = profile.getFullName();
                } else {
                    studentName = session.getStudent().getUsername();
                }
            }

            // Format thời gian
            String sessionTimeDisplay = "Không có thời gian";
            if (session.getSessionTime() != null) {
                sessionTimeDisplay = session.getSessionTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            }

            EvaluationForm form = new EvaluationForm();
            form.setSessionId(session.getId());
            form.getSelections().add(new EquipmentSelectionForm());
            form.getSelections().add(new EquipmentSelectionForm());
            form.getSelections().add(new EquipmentSelectionForm());

            model.addAttribute("mentoringSession", session);
            model.addAttribute("sessionStudentName", studentName);
            model.addAttribute("sessionTimeDisplay", sessionTimeDisplay);
            model.addAttribute("evaluationForm", form);
            model.addAttribute("equipments", equipmentService.findAll());
            return "lecturer/evaluate-form";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải form đánh giá: " + e.getMessage());
            return "redirect:/lecturer/sessions";
        }
    }

    @PostMapping("/sessions/{sessionId}/confirm")
    public String confirmSession(@AuthenticationPrincipal CurrentUserDetails principal,
                                 @PathVariable("sessionId") Long sessionId,
                                 RedirectAttributes redirectAttributes) {
        try {
            mentoringSessionService.confirmSession(principal.getId(), sessionId);
            redirectAttributes.addFlashAttribute("success", "Đã duyệt lịch tư vấn");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/lecturer/sessions";
    }

    @PostMapping("/sessions/{sessionId}/evaluate")
    public String evaluate(@AuthenticationPrincipal CurrentUserDetails principal,
            @PathVariable Long sessionId,
            @Valid @ModelAttribute("evaluationForm") EvaluationForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        form.setSessionId(sessionId);
        if (bindingResult.hasErrors()) {
            try {
                MentoringSession session = mentoringSessionService.getPendingSessionForLecturer(principal.getId(),
                        sessionId);

                // Tính tên sinh viên để hiển thị
                String studentName = "Không xác định";
                if (session.getStudent() != null) {
                    org.example.projects.entity.UserProfile profile = session.getStudent().getProfile();
                    if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
                        studentName = profile.getFullName();
                    } else {
                        studentName = session.getStudent().getUsername();
                    }
                }

                // Format thời gian
                String sessionTimeDisplay = "Không có thời gian";
                if (session.getSessionTime() != null) {
                    sessionTimeDisplay = session.getSessionTime()
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                }

                model.addAttribute("mentoringSession", session);
                model.addAttribute("sessionStudentName", studentName);
                model.addAttribute("sessionTimeDisplay", sessionTimeDisplay);
                model.addAttribute("equipments", equipmentService.findAll());
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/lecturer/sessions";
            }
            return "lecturer/evaluate-form";
        }
        try {
            evaluationService.evaluateSession(principal.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Đánh giá và tạo phiếu mượn thành công");
            return "redirect:/lecturer/sessions";
        } catch (org.example.projects.exception.BusinessException ex) {
            try {
                MentoringSession session = mentoringSessionService.getPendingSessionForLecturer(principal.getId(),
                        sessionId);

                // Tính tên sinh viên để hiển thị
                String studentName = "Không xác định";
                if (session.getStudent() != null) {
                    org.example.projects.entity.UserProfile profile = session.getStudent().getProfile();
                    if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
                        studentName = profile.getFullName();
                    } else {
                        studentName = session.getStudent().getUsername();
                    }
                }

                // Format thời gian
                String sessionTimeDisplay = "Không có thời gian";
                if (session.getSessionTime() != null) {
                    sessionTimeDisplay = session.getSessionTime()
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                }

                model.addAttribute("mentoringSession", session);
                model.addAttribute("sessionStudentName", studentName);
                model.addAttribute("sessionTimeDisplay", sessionTimeDisplay);
                model.addAttribute("equipments", equipmentService.findAll());
            } catch (Exception ignored) {
                // no-op
            }
            bindingResult.reject("evaluationError", ex.getMessage());
            return "lecturer/evaluate-form";
        }
    }
}
