package org.example.projects.service;

import java.util.List;

import org.example.projects.dto.LecturerStatsDto;
import org.example.projects.entity.BorrowingRecordStatus;
import org.example.projects.repository.BorrowingRecordRepository;
import org.example.projects.repository.MentoringSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final BorrowingRecordRepository borrowingRecordRepository;
	private final MentoringSessionRepository mentoringSessionRepository;

	@Transactional(readOnly = true)
	public long getTotalBorrowingQuantity() {
		Long total = borrowingRecordRepository.getTotalBorrowingQuantity();
		return total == null ? 0L : total;
	}

	@Transactional(readOnly = true)
	public long countPendingIssueRecords() {
		return borrowingRecordRepository.countByStatus(BorrowingRecordStatus.PENDING_ISSUE);
	}

	@Transactional(readOnly = true)
	public long countIssuedRecords() {
		return borrowingRecordRepository.countByStatus(BorrowingRecordStatus.ISSUED);
	}

	@Transactional(readOnly = true)
	public long countReturnedRecords() {
		return borrowingRecordRepository.countByStatus(BorrowingRecordStatus.RETURNED);
	}

	@Transactional(readOnly = true)
	public List<LecturerStatsDto> topLecturers() {
		return mentoringSessionRepository.findLecturerStats(PageRequest.of(0, 5));
	}
}

