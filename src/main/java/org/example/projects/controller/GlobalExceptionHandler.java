package org.example.projects.controller;

import org.example.projects.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessException(BusinessException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 400);
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(MethodArgumentNotValidException ex, Model model) {
        BindingResult bindingResult = ex.getBindingResult();
        model.addAttribute("message", bindingResult.getAllErrors().isEmpty() ? "Dữ liệu không hợp lệ" : bindingResult.getAllErrors().get(0).getDefaultMessage());
        model.addAttribute("status", 400);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage() == null ? "Lỗi hệ thống" : ex.getMessage());
        model.addAttribute("status", 500);
        return "error";
    }
}

