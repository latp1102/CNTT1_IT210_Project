package org.example.projects.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentSelectionForm {

    private Long equipmentId;

    @Min(0)
    private Integer quantity;
}

