package org.example.projects.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.projects.dto.LoginForm;
import org.example.projects.dto.RegistrationForm;
import org.example.projects.exception.BusinessException;
import org.example.projects.service.LookupService;
import org.example.projects.service.UserService;
import org.example.projects.validation.RegistrationFormValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final UserService userService;
    private final LookupService lookupService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RegistrationFormValidator registrationFormValidator;

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginForm") LoginForm form,
                          BindingResult bindingResult,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            request.getSession(true)
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công.");
            return "redirect:" + getLandingPage(authentication);
        } catch (BadCredentialsException ex) {
            bindingResult.reject("loginError", "Sai tên đăng nhập hoặc mật khẩu");
            return "login";
        } catch (AuthenticationException ex) {
            bindingResult.reject("loginError", "Không thể đăng nhập lúc này");
            return "login";
        }
    }

    private String getLandingPage(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .filter(authority -> authority != null && authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .map(role -> switch (role) {
                    case "STUDENT" -> "/student/home";
                    case "LECTURER" -> "/lecturer/home";
                    case "ADMIN" -> "/admin/home";
                    default -> "/dashboard";
                })
                .orElse("/dashboard");
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        model.addAttribute("departments", lookupService.getDepartments());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("registrationForm") RegistrationForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        // Custom validation
        registrationFormValidator.validate(form, bindingResult);
        
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", lookupService.getDepartments());
            return "register";
        }

        try {
            userService.registerStudent(form);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công, vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (BusinessException ex) {
            model.addAttribute("departments", lookupService.getDepartments());
            String msg = ex.getMessage() == null ? "Lỗi đăng ký" : ex.getMessage();
            // If it's an email duplication, bind the error to the email field so user sees it next to the input
            if (msg.toLowerCase().contains("email")) {
                bindingResult.rejectValue("email", "duplicate", msg);
            } else {
                bindingResult.reject("registerError", msg);
            }
            return "register";
        }
    }
}
