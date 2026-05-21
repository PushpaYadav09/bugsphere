package com.bugsphere.bugsphere.repository;

import com.bugsphere.bugsphere.entity.Bug;
import com.bugsphere.bugsphere.entity.BugStatus;
import com.bugsphere.bugsphere.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    // Get all bugs that belong to a specific project
    // SELECT * FROM bugs WHERE project_id = ?
    List<Bug> findByProjectId(Long projectId);

    // Get all bugs assigned to a specific user (their personal task list)
    // SELECT * FROM bugs WHERE assigned_to_id = ?
    List<Bug> findByAssignedToId(Long userId);

    // Get all bugs created by a specific user
    // SELECT * FROM bugs WHERE created_by_id = ?
    List<Bug> findByCreatedById(Long userId);

    // Get all bugs in a project filtered by status (e.g., all OPEN bugs in Project #3)
    // SELECT * FROM bugs WHERE project_id = ? AND status = ?
    List<Bug> findByProjectIdAndStatus(Long projectId, BugStatus status);

    // Get all bugs in a project filtered by priority
    // SELECT * FROM bugs WHERE project_id = ? AND priority = ?
    List<Bug> findByProjectIdAndPriority(Long projectId, Priority priority);

    // Count how many bugs exist in a project (useful for dashboard stats)
    // SELECT COUNT(*) FROM bugs WHERE project_id = ?
    long countByProjectId(Long projectId);

    // Count bugs by status across all projects (e.g., "how many bugs are OPEN right now?")
    // SELECT COUNT(*) FROM bugs WHERE status = ?
    long countByStatus(BugStatus status);
}