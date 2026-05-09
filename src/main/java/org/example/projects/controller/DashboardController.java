package org.example.projects.controller;

import org.example.projects.entity.UserAccount;
import org.example.projects.repository.UserRepository;
import org.example.projects.security.CurrentUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;

    @GetMapping("/")
    public String homeRedirect(@AuthenticationPrincipal CurrentUserDetails principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return switch (userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"))
                .getRole()) {
            case STUDENT -> "redirect:/student/home";
            case LECTURER -> "redirect:/lecturer/home";
            case ADMIN -> "redirect:/admin/home";
        };
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CurrentUserDetails principal, Model model) {
        UserAccount user = userRepository.findWithDepartmentAndProfileByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        model.addAttribute("currentUser", user);
        model.addAttribute("roleName", user.getRole().name());
        return "dashboard";
    }
}

