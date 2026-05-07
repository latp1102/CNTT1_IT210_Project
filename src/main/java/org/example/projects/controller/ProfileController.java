package org.example.projects.controller;

import org.example.projects.dto.ProfileForm;
import org.example.projects.exception.BusinessException;
import org.example.projects.security.CurrentUserDetails;
import org.example.projects.service.LookupService;
import org.example.projects.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final LookupService lookupService;

    @GetMapping
    public String profile(@AuthenticationPrincipal CurrentUserDetails principal, Model model) {
        model.addAttribute("profileForm", userService.loadProfileForm(principal.getId()));
        model.addAttribute("departments", lookupService.getDepartments());
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal CurrentUserDetails principal,
                                @Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", lookupService.getDepartments());
            return "profile";
        }
        try {
            userService.updateProfile(principal.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
            return "redirect:/profile";
        } catch (BusinessException ex) {
            model.addAttribute("departments", lookupService.getDepartments());
            bindingResult.reject("profileError", ex.getMessage());
            return "profile";
        }
    }
}

