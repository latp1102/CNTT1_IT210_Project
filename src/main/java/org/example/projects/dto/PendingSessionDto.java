package org.example.projects.dto;

import java.time.LocalDateTime;

import org.example.projects.entity.MentoringSessionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingSessionDto {

    private Long sessionId;
    private String studentName;
    private String studentUsername;
    private String lecturerName;
    private LocalDateTime sessionTime;
    private MentoringSessionStatus status;
}

