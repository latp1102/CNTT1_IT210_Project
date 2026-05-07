package org.example.projects.service;

import java.util.List;

import org.example.projects.entity.BorrowingDetail;
import org.example.projects.entity.BorrowingRecord;
import org.example.projects.entity.BorrowingRecordStatus;
import org.example.projects.entity.Equipment;
import org.example.projects.exception.BusinessException;
import org.example.projects.repository.BorrowingDetailRepository;
import org.example.projects.repository.BorrowingRecordRepository;
import org.example.projects.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public List<BorrowingRecord> getPendingIssueRecords() {
        return borrowingRecordRepository.findByStatus(BorrowingRecordStatus.PENDING_ISSUE);
    }

    @Transactional
    public void confirmIssue(Long recordId) {
        BorrowingRecord record = borrowingRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu mượn"));
        if (record.getStatus() == BorrowingRecordStatus.ISSUED) {
            throw new BusinessException("Phiếu mượn đã được xuất kho");
        }

        List<BorrowingDetail> details = borrowingDetailRepository.findByRecordId(recordId);
        for (BorrowingDetail detail : details) {
            Equipment equipment = equipmentRepository.findById(detail.getEquipment().getId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy thiết bị"));
            if (equipment.getQuantity() == null || equipment.getQuantity() < detail.getQuantity()) {
                throw new BusinessException("Không đủ tồn kho cho thiết bị: " + equipment.getName());
            }
        }

        for (BorrowingDetail detail : details) {
            Equipment equipment = equipmentRepository.findById(detail.getEquipment().getId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy thiết bị"));
            equipment.setQuantity(equipment.getQuantity() - detail.getQuantity());
            equipmentRepository.save(equipment);
        }

        record.setStatus(BorrowingRecordStatus.ISSUED);
        borrowingRecordRepository.save(record);
    }
}

