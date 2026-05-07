package org.example.projects.repository;

import java.util.List;

import org.example.projects.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findAllByOrderByNameAsc();
}

