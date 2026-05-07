package org.example.projects.controller;

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
        java.util.List<PendingSessionDto> sessions = mentoringSessionService.getPendingSessionsForLecturer(principal.getId());
        model.addAttribute("sessions", sessions == null ? new ArrayList<>() : sessions);
        return "lecturer/sessions";
    }

    @GetMapping("/sessions/{sessionId}/evaluate")
    public String evaluateForm(@AuthenticationPrincipal CurrentUserDetails principal,
                               @PathVariable Long sessionId,
                               Model model) {
        MentoringSession session = mentoringSessionService.getPendingSessionForLecturer(principal.getId(), sessionId);
        EvaluationForm form = new EvaluationForm();
        form.setSessionId(session.getId());
        form.getSelections().add(new EquipmentSelectionForm());
        form.getSelections().add(new EquipmentSelectionForm());
        form.getSelections().add(new EquipmentSelectionForm());
        model.addAttribute("session", session);
        model.addAttribute("evaluationForm", form);
        model.addAttribute("equipments", equipmentService.findAll());
        return "lecturer/evaluate-form";
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
                model.addAttribute("session", mentoringSessionService.getPendingSessionForLecturer(principal.getId(), sessionId));
                model.addAttribute("equipments", equipmentService.findAll());
            } catch (BusinessException ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/lecturer/sessions";
            }
            return "lecturer/evaluate-form";
        }
        try {
            evaluationService.evaluateSession(principal.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Đánh giá và tạo phiếu mượn thành công");
            return "redirect:/lecturer/sessions";
        } catch (BusinessException ex) {
            try {
                model.addAttribute("session", mentoringSessionService.getPendingSessionForLecturer(principal.getId(), sessionId));
                model.addAttribute("equipments", equipmentService.findAll());
            } catch (BusinessException ignored) {
                // no-op
            }
            bindingResult.reject("evaluationError", ex.getMessage());
            return "lecturer/evaluate-form";
        }
    }
}

