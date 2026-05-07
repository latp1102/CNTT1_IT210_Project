package org.example.projects.service;

import java.util.List;

import org.example.projects.dto.LecturerOptionDto;
import org.example.projects.entity.Department;
import org.example.projects.entity.UserAccount;
import org.example.projects.entity.UserRole;
import org.example.projects.repository.DepartmentRepository;
import org.example.projects.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LookupService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public List<Department> getDepartments() {
        return departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoa/ngành"));
    }

    public List<LecturerOptionDto> getLecturers(Long departmentId) {
        List<UserAccount> lecturers = userRepository.findByRole(UserRole.LECTURER);
        return lecturers.stream()
                .filter(lecturer -> departmentId == null
                        || (lecturer.getDepartment() != null && departmentId.equals(lecturer.getDepartment().getId())))
                .map(this::toLecturerOption)
                .toList();
    }

    public LecturerOptionDto getLecturerOption(Long lecturerId) {
        UserAccount lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giảng viên"));
        return toLecturerOption(lecturer);
    }

    private LecturerOptionDto toLecturerOption(UserAccount lecturer) {
        String fullName = lecturer.getProfile() != null && lecturer.getProfile().getFullName() != null
                ? lecturer.getProfile().getFullName()
                : lecturer.getUsername();
        String departmentName = lecturer.getDepartment() != null ? lecturer.getDepartment().getName() : "";
        return new LecturerOptionDto(lecturer.getId(), fullName, lecturer.getUsername(), departmentName);
    }
}

