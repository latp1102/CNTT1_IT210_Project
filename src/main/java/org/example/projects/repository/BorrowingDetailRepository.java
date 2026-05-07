package org.example.projects.repository;

import java.util.List;

import org.example.projects.entity.BorrowingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingDetailRepository extends JpaRepository<BorrowingDetail, Long> {

    List<BorrowingDetail> findByRecordId(Long recordId);
}

