package org.example.projects.repository;

import java.util.List;
import java.util.Optional;

import org.example.projects.entity.BorrowingRecord;
import org.example.projects.entity.BorrowingRecordStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    Optional<BorrowingRecord> findBySessionId(Long sessionId);

    @EntityGraph(attributePaths = {"session", "details", "details.equipment"})
    List<BorrowingRecord> findByStatus(BorrowingRecordStatus status);
}

