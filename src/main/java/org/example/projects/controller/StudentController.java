package org.example.projects.controller;

import java.util.ArrayList;

import org.example.projects.dto.BookingForm;
import org.example.projects.dto.SessionHistoryDto;
import org.example.projects.exception.BusinessException;
import org.example.projects.security.CurrentUserDetails;
import org.example.projects.service.LookupService;
import org.example.projects.service.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final MentoringSessionService mentoringSessionService;
    private final LookupService lookupService;

    @GetMapping("/booking")
    public String bookingForm(@AuthenticationPrincipal CurrentUserDetails principal,
                              @RequestParam(required = false) Long departmentId,
                              Model model) {
        model.addAttribute("bookingForm", new BookingForm());
        model.addAttribute("departments", lookupService.getDepartments());
        model.addAttribute("lecturers", lookupService.getLecturers(departmentId));
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("currentUserId", principal.getId());
        return "student/booking-form";
    }

    @PostMapping("/booking")
    public String book(@AuthenticationPrincipal CurrentUserDetails principal,
                       @Valid @ModelAttribute("bookingForm") BookingForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", lookupService.getDepartments());
            model.addAttribute("lecturers", lookupService.getLecturers(form.getDepartmentId()));
            return "student/booking-form";
        }

        try {
            mentoringSessionService.bookSession(principal.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Đặt lịch cố vấn thành công");
            return "redirect:/student/history";
        } catch (BusinessException ex) {
            model.addAttribute("departments", lookupService.getDepartments());
            model.addAttribute("lecturers", lookupService.getLecturers(form.getDepartmentId()));
            bindingResult.reject("bookingError", ex.getMessage());
            return "student/booking-form";
        }
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal CurrentUserDetails principal, Model model) {
        java.util.List<SessionHistoryDto> history = mentoringSessionService.getStudentHistory(principal.getId());
        model.addAttribute("history", history == null ? new ArrayList<>() : history);
        return "student/history";
    }

    @PostMapping("/sessions/{sessionId}/cancel")
    public String cancel(@AuthenticationPrincipal CurrentUserDetails principal,
                         @org.springframework.web.bind.annotation.PathVariable Long sessionId,
                         RedirectAttributes redirectAttributes) {
        try {
            mentoringSessionService.cancelSession(principal.getId(), sessionId);
            redirectAttributes.addFlashAttribute("success", "Đã hủy lịch thành công");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/student/history";
    }
}

