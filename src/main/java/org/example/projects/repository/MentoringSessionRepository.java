package org.example.projects.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.projects.entity.MentoringSession;
import org.example.projects.entity.MentoringSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    @EntityGraph(attributePaths = {"lecturer", "lecturer.department", "lecturer.profile", "student", "student.department", "student.profile"})
    List<MentoringSession> findByStudentIdOrderBySessionTimeDesc(Long studentId);

    @EntityGraph(attributePaths = {"lecturer", "lecturer.department", "lecturer.profile", "student", "student.department", "student.profile"})
    List<MentoringSession> findByLecturerIdAndStatusOrderBySessionTimeAsc(Long lecturerId, MentoringSessionStatus status);

    @EntityGraph(attributePaths = {"lecturer", "lecturer.department", "lecturer.profile", "student", "student.department", "student.profile"})
    Optional<MentoringSession> findByIdAndStudentId(Long id, Long studentId);

    @EntityGraph(attributePaths = {"lecturer", "lecturer.department", "lecturer.profile", "student", "student.department", "student.profile"})
    Optional<MentoringSession> findByIdAndLecturerId(Long id, Long lecturerId);

    @Query("""
            select case when count(ms) > 0 then true else false end
            from MentoringSession ms
            where ms.lecturer.id = :lecturerId
              and ms.sessionTime = :sessionTime
              and ms.status <> org.example.projects.entity.MentoringSessionStatus.CANCELED
            """)
    boolean existsConflict(@Param("lecturerId") Long lecturerId, @Param("sessionTime") LocalDateTime sessionTime);
}
