package org.example.projects.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrowing_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", unique = true)
    private MentoringSession session;

    @Enumerated(EnumType.STRING)
    private BorrowingRecordStatus status;

    @OneToMany(mappedBy = "record", fetch = FetchType.LAZY)
    private List<BorrowingDetail> details = new ArrayList<>();
}

