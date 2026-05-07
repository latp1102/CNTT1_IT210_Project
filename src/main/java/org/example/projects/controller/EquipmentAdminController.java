package org.example.projects.controller;

import org.example.projects.dto.EquipmentForm;
import org.example.projects.entity.Equipment;
import org.example.projects.exception.BusinessException;
import org.example.projects.service.EquipmentService;
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
@RequestMapping("/admin/equipments")
public class EquipmentAdminController {

    private final EquipmentService equipmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("equipments", equipmentService.findAll());
        return "admin/equipment-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("equipmentForm", new EquipmentForm());
        model.addAttribute("formTitle", "Thêm thiết bị");
        model.addAttribute("actionUrl", "/admin/equipments/new");
        return "admin/equipment-form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("equipmentForm") EquipmentForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Thêm thiết bị");
            model.addAttribute("actionUrl", "/admin/equipments/new");
            return "admin/equipment-form";
        }
        try {
            equipmentService.create(form);
            redirectAttributes.addFlashAttribute("success", "Đã thêm thiết bị");
            return "redirect:/admin/equipments";
        } catch (BusinessException ex) {
            model.addAttribute("formTitle", "Thêm thiết bị");
            model.addAttribute("actionUrl", "/admin/equipments/new");
            bindingResult.reject("equipmentError", ex.getMessage());
            return "admin/equipment-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentService.findById(id);
        EquipmentForm form = new EquipmentForm();
        form.setName(equipment.getName());
        form.setQuantity(equipment.getQuantity());
        form.setDescription(equipment.getDescription());
        model.addAttribute("equipmentForm", form);
        model.addAttribute("formTitle", "Sửa thiết bị");
        model.addAttribute("actionUrl", "/admin/equipments/" + id + "/edit");
        model.addAttribute("equipmentId", id);
        return "admin/equipment-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("equipmentForm") EquipmentForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Sửa thiết bị");
            model.addAttribute("actionUrl", "/admin/equipments/" + id + "/edit");
            model.addAttribute("equipmentId", id);
            return "admin/equipment-form";
        }
        try {
            equipmentService.update(id, form);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật thiết bị");
            return "redirect:/admin/equipments";
        } catch (BusinessException ex) {
            model.addAttribute("formTitle", "Sửa thiết bị");
            model.addAttribute("actionUrl", "/admin/equipments/" + id + "/edit");
            model.addAttribute("equipmentId", id);
            bindingResult.reject("equipmentError", ex.getMessage());
            return "admin/equipment-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            equipmentService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa thiết bị");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/equipments";
    }
}

