package org.example.projects.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentForm {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(0)
    private Integer quantity;

    @Size(max = 2000)
    private String description;
}

