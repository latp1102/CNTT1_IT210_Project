package org.example.projects.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.projects.entity.BorrowingRecordStatus;
import org.example.projects.entity.MentoringSessionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionHistoryDto {

    private Long sessionId;
    private LocalDateTime sessionTime;
    private String lecturerName;
    private String lecturerDepartment;
    private MentoringSessionStatus sessionStatus;
    private String evaluationText;
    private BorrowingRecordStatus borrowingStatus;
    private List<BorrowingDetailView> borrowedItems = new ArrayList<>();
}

