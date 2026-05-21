package com.bugsphere.bugsphere.service;

import com.bugsphere.bugsphere.dto.BugRequest;
import com.bugsphere.bugsphere.dto.BugResponse;
import com.bugsphere.bugsphere.entity.*;
import com.bugsphere.bugsphere.exception.ResourceNotFoundException;
import com.bugsphere.bugsphere.repository.BugRepository;
import com.bugsphere.bugsphere.repository.ProjectRepository;
import com.bugsphere.bugsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
// ↑ lets us get the currently logged-in user inside the service
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ── Helper: get currently logged-in user ──────────────────────────────────
    // Spring Security stores the logged-in user in a thread-local context.
    // We use this in createBug() to auto-set the createdBy field.
    private User getCurrentUser() {
        // SecurityContextHolder holds auth info for the current request thread
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // returns the username from the JWT token
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Current user not found"
                ));
    }

    // ── Create bug ────────────────────────────────────────────────────────────

    @Transactional
    public BugResponse createBug(BugRequest request) {
        // Find the project this bug belongs to — 404 if not found
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + request.getProjectId()
                ));

        // Resolve the assigned user if one was provided
        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssignedToId()
                    ));
        }

        // Build the bug entity
        Bug bug = Bug.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                // Use request priority if provided, otherwise default to MEDIUM
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .status(BugStatus.OPEN)      // every new bug starts as OPEN
                .project(project)
                .createdBy(getCurrentUser()) // auto-set from JWT token — no need to send this in request
                .assignedTo(assignedTo)      // may be null
                .build();

        Bug saved = bugRepository.save(bug);
        return BugResponse.fromEntity(saved);
    }

    // ── Get all bugs (optionally filtered) ───────────────────────────────────

    @Transactional(readOnly = true)
    public List<BugResponse> getAllBugs() {
        return bugRepository.findAll()
                .stream()
                .map(BugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get bugs for a specific project ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BugResponse> getBugsByProject(Long projectId) {
        // Verify the project exists first
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        return bugRepository.findByProjectId(projectId)
                .stream()
                .map(BugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get bugs assigned to the logged-in user ───────────────────────────────

    @Transactional(readOnly = true)
    public List<BugResponse> getMyAssignedBugs() {
        User currentUser = getCurrentUser();
        return bugRepository.findByAssignedToId(currentUser.getId())
                .stream()
                .map(BugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get one bug by ID ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public BugResponse getBugById(Long id) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bug not found with id: " + id
                ));
        return BugResponse.fromEntity(bug);
    }

    // ── Update bug (full update) ───────────────────────────────────────────────

    @Transactional
    public BugResponse updateBug(Long id, BugRequest request) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bug not found with id: " + id
                ));

        // Update fields if they were provided in the request
        bug.setTitle(request.getTitle());
        bug.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            bug.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            bug.setStatus(request.getStatus());
        }

        // Re-assign to a different project if projectId changed
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project not found with id: " + request.getProjectId()
                    ));
            bug.setProject(project);
        }

        // Re-assign to a different user — or set to null to un-assign
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssignedToId()
                    ));
            bug.setAssignedTo(assignedTo);
        }

        // @PreUpdate in Bug.java auto-updates updatedAt timestamp
        Bug updated = bugRepository.save(bug);
        return BugResponse.fromEntity(updated);
    }

    // ── Update status only — a shortcut endpoint ──────────────────────────────
    // Useful for drag-and-drop Kanban boards — just change the status, nothing else

    @Transactional
    public BugResponse updateBugStatus(Long id, BugStatus newStatus) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bug not found with id: " + id
                ));
        bug.setStatus(newStatus); // only touch the status field
        Bug updated = bugRepository.save(bug);
        return BugResponse.fromEntity(updated);
    }

    // ── Assign bug to a user ───────────────────────────────────────────────────

    @Transactional
    public BugResponse assignBug(Long bugId, Long userId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bug not found with id: " + bugId
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        bug.setAssignedTo(user); // link the bug to the user
        Bug updated = bugRepository.save(bug);
        return BugResponse.fromEntity(updated);
    }

    // ── Delete bug ────────────────────────────────────────────────────────────

    @Transactional
    public void deleteBug(Long id) {
        if (!bugRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bug not found with id: " + id);
        }
        bugRepository.deleteById(id);
    }

    // ── Dashboard stats ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Long> getBugStats() {
        // Returns a summary: how many bugs in each status
        Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("total",       bugRepository.count());
        stats.put("open",        bugRepository.countByStatus(BugStatus.OPEN));
        stats.put("inProgress",  bugRepository.countByStatus(BugStatus.IN_PROGRESS));
        stats.put("resolved",    bugRepository.countByStatus(BugStatus.RESOLVED));
        stats.put("closed",      bugRepository.countByStatus(BugStatus.CLOSED));
        return stats;
    }
}