package org.example.projects.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationForm {

    @NotNull
    private Long sessionId;

    @NotBlank
    @Size(max = 4000)
    private String evaluationText;

    private List<EquipmentSelectionForm> selections = new ArrayList<>();
}

