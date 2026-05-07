package org.example.projects.validation;

import org.example.projects.dto.LoginForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LoginFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginForm loginForm = (LoginForm) target;
        
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            errors.rejectValue("username", "username.empty", "Tên đăng nhập không được để trống");
        } else if (username.trim().length() < 3) {
            errors.rejectValue("username", "username.too.short", "Tên đăng nhập phải có ít nhất 3 ký tự");
        } else if (username.trim().length() > 50) {
            errors.rejectValue("username", "username.too.long", "Tên đăng nhập không được vượt quá 50 ký tự");
        }
        
        // Validate password
        if (password == null || password.isEmpty()) {
            errors.rejectValue("password", "password.empty", "Mật khẩu không được để trống");
        } else if (password.length() < 6) {
            errors.rejectValue("password", "password.too.short", "Mật khẩu phải có ít nhất 6 ký tự");
        } else if (password.length() > 100) {
            errors.rejectValue("password", "password.too.long", "Mật khẩu không được vượt quá 100 ký tự");
        }
        
        // Check for SQL injection patterns
        if (username != null && (username.toLowerCase().contains("drop") || 
            username.toLowerCase().contains("delete") || 
            username.toLowerCase().contains("insert") || 
            username.toLowerCase().contains("'") || 
            username.toLowerCase().contains("\"") || 
            username.toLowerCase().contains(";"))) {
            errors.rejectValue("username", "username.invalid", "Tên đăng nhập chứa ký tự không hợp lệ");
        }
    }
}
