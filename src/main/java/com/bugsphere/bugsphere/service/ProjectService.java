package com.bugsphere.bugsphere.service;

import com.bugsphere.bugsphere.dto.ProjectRequest;
import com.bugsphere.bugsphere.dto.ProjectResponse;
import com.bugsphere.bugsphere.entity.Project;
import com.bugsphere.bugsphere.exception.ResourceNotFoundException;
import com.bugsphere.bugsphere.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// @Transactional = wraps the method in a DB transaction.
// If anything fails mid-method, all DB changes are rolled back automatically.

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional // wrap in transaction — if save() fails, nothing is committed
    public ProjectResponse createProject(ProjectRequest request) {
        // Build a Project entity from the DTO
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        // Save to DB — Spring generates: INSERT INTO projects (name, description) VALUES (?, ?)
        Project saved = projectRepository.save(project);

        // Convert to DTO and return — never return the raw entity from a controller
        return ProjectResponse.fromEntity(saved);
    }

    // ── Read all ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true) // readOnly = true is a hint to DB to optimize — no writes here
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()                            // convert List to a stream for processing
                .map(ProjectResponse::fromEntity)    // convert each Project entity → ProjectResponse DTO
                .collect(Collectors.toList());       // collect back into a List
    }

    // ── Read one ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                // If no project exists with this id, throw 404
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + id
                ));
        return ProjectResponse.fromEntity(project);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        // First find the project — throw 404 if not found
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + id
                ));

        // Update only the fields that were sent
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        // save() on an existing entity = UPDATE query (not INSERT)
        // JPA knows it's an update because the entity already has an id
        Project updated = projectRepository.save(project);
        return ProjectResponse.fromEntity(updated);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteProject(Long id) {
        // Verify it exists before trying to delete
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        // cascade = ALL on Project.bugs means all bugs in this project are also deleted
        projectRepository.deleteById(id);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ProjectResponse> searchByName(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());
    }
}