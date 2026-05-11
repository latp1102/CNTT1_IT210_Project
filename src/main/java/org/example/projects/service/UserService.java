package org.example.projects.service;

import org.example.projects.dto.ProfileForm;
import org.example.projects.dto.RegistrationForm;
import org.example.projects.entity.Department;
import org.example.projects.entity.UserAccount;
import org.example.projects.entity.UserProfile;
import org.example.projects.entity.UserRole;
import org.example.projects.exception.BusinessException;
import org.example.projects.repository.DepartmentRepository;
import org.example.projects.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAccount registerStudent(RegistrationForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp");
        }
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        String normalizedEmail = form.getEmail() == null ? null : form.getEmail().trim().toLowerCase();
        if (normalizedEmail == null || normalizedEmail.isEmpty()) {
            throw new BusinessException("Email không hợp lệ");
        }
        if (userRepository.findByProfileEmail(normalizedEmail).isPresent()) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (form.getPhone() != null && !form.getPhone().trim().isEmpty() 
                && userRepository.findByProfilePhone(form.getPhone().trim()).isPresent()) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }
        Department department = departmentRepository.findById(form.getDepartmentId())
                .orElseThrow(() -> new BusinessException("Khoa/ngành không hợp lệ"));

        UserAccount user = new UserAccount();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(true);
        user.setDepartment(department);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(form.getFullName());
        profile.setEmail(normalizedEmail);
        profile.setPhone(form.getPhone());
        user.setProfile(profile);

        try {
            return userRepository.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new BusinessException("Dữ liệu trùng lặp hoặc không hợp lệ: " + ex.getMostSpecificCause().getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ProfileForm loadProfileForm(Long userId) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng"));

        ProfileForm form = new ProfileForm();
        if (user.getProfile() != null) {
            form.setFullName(user.getProfile().getFullName());
            form.setEmail(user.getProfile().getEmail());
            form.setPhone(user.getProfile().getPhone());
        }
        form.setDepartmentId(user.getDepartment() != null ? user.getDepartment().getId() : null);
        return form;
    }

    @Transactional
    public void updateProfile(Long userId, ProfileForm form) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng"));

        Department department = null;
        if (form.getDepartmentId() != null) {
            department = departmentRepository.findById(form.getDepartmentId())
                    .orElseThrow(() -> new BusinessException("Khoa/ngành không hợp lệ"));
        }

        user.setDepartment(department);
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }
        profile.setFullName(form.getFullName());
        profile.setEmail(form.getEmail());
        profile.setPhone(form.getPhone());
        userRepository.save(user);
    }
}

