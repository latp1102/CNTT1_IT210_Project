package org.example.projects.controller;

import org.example.projects.entity.UserAccount;
import org.example.projects.repository.UserRepository;
import org.example.projects.security.CurrentUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminHomeController {

    private final UserRepository userRepository;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CurrentUserDetails principal, Model model) {
        UserAccount user = userRepository.findWithDepartmentAndProfileByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        model.addAttribute("currentUser", user);
        return "admin/home";
    }
}

