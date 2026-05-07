package org.example.projects.repository;

import java.util.Optional;

import org.example.projects.entity.AcademicEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicEvaluationRepository extends JpaRepository<AcademicEvaluation, Long> {

    Optional<AcademicEvaluation> findBySessionId(Long sessionId);
}

