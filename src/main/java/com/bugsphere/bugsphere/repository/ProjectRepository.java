package com.bugsphere.bugsphere.repository;

import com.bugsphere.bugsphere.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // SELECT * FROM projects WHERE LOWER(name) LIKE LOWER('%keyword%')
    // This lets us search projects by name, case-insensitively
    List<Project> findByNameContainingIgnoreCase(String name);
}