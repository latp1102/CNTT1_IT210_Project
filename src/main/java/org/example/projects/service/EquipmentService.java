package org.example.projects.service;

import java.util.List;

import org.example.projects.dto.EquipmentForm;
import org.example.projects.entity.Equipment;
import org.example.projects.exception.BusinessException;
import org.example.projects.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public List<Equipment> findAll() {
        return equipmentRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Equipment findById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thiết bị"));
    }

    @Transactional
    public Equipment create(EquipmentForm form) {
        Equipment equipment = new Equipment();
        applyForm(equipment, form);
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public Equipment update(Long id, EquipmentForm form) {
        Equipment equipment = findById(id);
        applyForm(equipment, form);
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void delete(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy thiết bị");
        }
        equipmentRepository.deleteById(id);
    }

    private void applyForm(Equipment equipment, EquipmentForm form) {
        equipment.setName(form.getName());
        equipment.setQuantity(form.getQuantity());
        equipment.setDescription(form.getDescription());
    }
}

