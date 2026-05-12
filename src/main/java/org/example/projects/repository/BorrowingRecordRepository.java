package org.example.projects.repository;

import java.util.List;
import java.util.Optional;

import org.example.projects.entity.BorrowingRecord;
import org.example.projects.entity.BorrowingRecordStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    Optional<BorrowingRecord> findBySessionId(Long sessionId);

    //    @EntityGraph(attributePaths = {"session", "details", "details.equipment"})
    @EntityGraph(attributePaths = {"session", "session.student", "session.student.profile", "details", "details.equipment"})
    List<BorrowingRecord> findByStatus(BorrowingRecordStatus status);

    @Query("SELECT COALESCE(SUM(bd.quantity), 0) FROM BorrowingDetail bd WHERE bd.record.status = :status")
    Long getTotalBorrowingQuantityByStatus(BorrowingRecordStatus status);

    @Query("SELECT COALESCE(SUM(bd.quantity), 0) FROM BorrowingDetail bd WHERE bd.record.status IN :statuses")
    Long getTotalBorrowingQuantityByStatuses(List<BorrowingRecordStatus> statuses);

    default Long getTotalBorrowingQuantity() {
        return getTotalBorrowingQuantityByStatuses(List.of(
            BorrowingRecordStatus.ISSUED
        ));
    }

    long countByStatus(BorrowingRecordStatus status);
}
