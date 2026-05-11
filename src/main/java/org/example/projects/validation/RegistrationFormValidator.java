package org.example.projects.validation;

import org.example.projects.dto.RegistrationForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RegistrationFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationForm form = (RegistrationForm) target;
        
        String password = form.getPassword();

        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                errors.rejectValue("password", "password.too.short", "Mật khẩu phải có ít nhất 6 ký tự");
            } else if (password.length() > 100) {
                errors.rejectValue("password", "password.too.long", "Mật khẩu không được vượt quá 100 ký tự");
            }
        }

        String phone = form.getPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            String phoneTrim = phone.trim();
            if (!phoneTrim.matches("^0\\d{9}$")) {
                errors.rejectValue("phone", "phone.invalid", "Số điện thoại không hợp lệ (chỉ chứa số, 9-10 ký tự)");
            }
        }

        String username = form.getUsername();
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