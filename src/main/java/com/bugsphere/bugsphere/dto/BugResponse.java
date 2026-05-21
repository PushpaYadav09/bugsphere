package com.bugsphere.bugsphere.dto;

import com.bugsphere.bugsphere.entity.Bug;
import com.bugsphere.bugsphere.entity.BugStatus;
import com.bugsphere.bugsphere.entity.Priority;
import lombok.Data;

import java.time.LocalDateTime;

// This is what we send BACK to the frontend when returning bug data.
// We flatten the nested objects (project, assignedTo) into simple ID + name fields.
// This avoids sending the entire User/Project object with passwords and sensitive data.
@Data
public class BugResponse {

    private Long id;
    private String title;
    private String description;
    private BugStatus status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Instead of the full Project object, just send the ID and name
    private Long projectId;
    private String projectName;

    // Instead of the full User object, just send ID and username
    private Long createdById;
    private String createdByUsername;

    private Long assignedToId;
    private String assignedToUsername;  // null if not yet assigned

    // Static factory method — converts a Bug entity into a BugResponse DTO.
    // This keeps the conversion logic in one place.
    public static BugResponse fromEntity(Bug bug) {
        BugResponse response = new BugResponse();
        response.setId(bug.getId());
        response.setTitle(bug.getTitle());
        response.setDescription(bug.getDescription());
        response.setStatus(bug.getStatus());
        response.setPriority(bug.getPriority());
        response.setCreatedAt(bug.getCreatedAt());
        response.setUpdatedAt(bug.getUpdatedAt());

        // Safely map the project — it's a lazy-loaded object so we access only what we need
        if (bug.getProject() != null) {
            response.setProjectId(bug.getProject().getId());
            response.setProjectName(bug.getProject().getName());
        }

        // Safely map the creator
        if (bug.getCreatedBy() != null) {
            response.setCreatedById(bug.getCreatedBy().getId());
            response.setCreatedByUsername(bug.getCreatedBy().getUsername());
        }

        // Safely map the assignee — might be null if not yet assigned
        if (bug.getAssignedTo() != null) {
            response.setAssignedToId(bug.getAssignedTo().getId());
            response.setAssignedToUsername(bug.getAssignedTo().getUsername());
        }

        return response;
    }
}