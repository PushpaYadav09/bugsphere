package com.bugsphere.bugsphere.controller;

import com.bugsphere.bugsphere.dto.ProjectRequest;
import com.bugsphere.bugsphere.dto.ProjectResponse;
import com.bugsphere.bugsphere.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All project endpoints live under /api/projects
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // POST /api/projects — create a new project
    // Only admins can create projects
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // throws 403 if the logged-in user is not ROLE_ADMIN
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/projects — get all projects
    // All authenticated users can see projects
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // GET /api/projects/{id} — get one project by id
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        // @PathVariable extracts the {id} from the URL
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // PUT /api/projects/{id} — update a project
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    // DELETE /api/projects/{id} — delete a project (and all its bugs)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content — success, nothing to return
    }

    // GET /api/projects/search?name=auth — search projects by name
    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam String name) {
        // @RequestParam reads the ?name=... query parameter from the URL
        return ResponseEntity.ok(projectService.searchByName(name));
    }
}