package com.bugsphere.bugsphere.dto;

import com.bugsphere.bugsphere.entity.BugStatus;
import com.bugsphere.bugsphere.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// This is the JSON body the frontend sends when CREATING or UPDATING a bug.
// We use a DTO (not the entity) so the frontend can't accidentally set fields
// like createdAt, createdBy, or id — those are set by the backend only.
@Data
public class BugRequest {

    @NotBlank(message = "Bug title is required")
    private String title;           // short summary, e.g. "Login button doesn't work on mobile"

    private String description;     // detailed explanation — optional

    private Priority priority;      // LOW / MEDIUM / HIGH / CRITICAL — optional, defaults to MEDIUM

    private BugStatus status;       // OPEN / IN_PROGRESS / RESOLVED / CLOSED — optional for updates

    @NotNull(message = "Project ID is required")
    private Long projectId;         // which project this bug belongs to

    private Long assignedToId;      // which user to assign this bug to — optional (can be null)
}