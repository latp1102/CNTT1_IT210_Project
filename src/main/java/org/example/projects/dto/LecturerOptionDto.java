package org.example.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturerOptionDto {

    private Long id;
    private String fullName;
    private String username;
    private String departmentName;
}

