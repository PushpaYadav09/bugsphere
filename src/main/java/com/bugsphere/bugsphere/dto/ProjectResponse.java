package com.bugsphere.bugsphere.dto;

import com.bugsphere.bugsphere.entity.Project;
import lombok.Data;

import java.time.LocalDateTime;

// What we send back when returning project data
@Data
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private int bugCount;   // how many bugs this project has — useful for dashboard

    // Converts a Project entity into a ProjectResponse DTO
    public static ProjectResponse fromEntity(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        // project.getBugs() returns the list — .size() gives the count
        response.setBugCount(project.getBugs().size());
        return response;
    }
}