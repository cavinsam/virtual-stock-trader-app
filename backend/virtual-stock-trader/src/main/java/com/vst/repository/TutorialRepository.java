package com.vst.repository;

import com.vst.entity.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    // JpaRepository provides all standard CRUD methods (findAll, findById, save, deleteById)
}
