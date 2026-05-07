package org.example.projects.controller;

import org.example.projects.entity.BorrowingRecord;
import org.example.projects.exception.BusinessException;
import org.example.projects.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public String pendingIssue(Model model) {
        model.addAttribute("records", inventoryService.getPendingIssueRecords());
        return "admin/inventory-list";
    }

    @PostMapping("/{recordId}/confirm")
    public String confirm(@PathVariable Long recordId, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.confirmIssue(recordId);
            redirectAttributes.addFlashAttribute("success", "Đã xác nhận xuất kho");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/inventory";
    }
}

