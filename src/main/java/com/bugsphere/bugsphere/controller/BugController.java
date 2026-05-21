package com.bugsphere.bugsphere.controller;

import com.bugsphere.bugsphere.dto.BugRequest;
import com.bugsphere.bugsphere.dto.BugResponse;
import com.bugsphere.bugsphere.entity.BugStatus;
import com.bugsphere.bugsphere.service.BugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;

    // POST /api/bugs — any logged-in user can create a bug
    @PostMapping
    public ResponseEntity<BugResponse> createBug(
            @Valid @RequestBody BugRequest request) {
        BugResponse response = bugService.createBug(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/bugs — get all bugs (admin sees all, user sees all too for now)
    @GetMapping
    public ResponseEntity<List<BugResponse>> getAllBugs() {
        return ResponseEntity.ok(bugService.getAllBugs());
    }

    // GET /api/bugs/my — get bugs assigned to ME (the logged-in user)
    @GetMapping("/my")
    public ResponseEntity<List<BugResponse>> getMyBugs() {
        return ResponseEntity.ok(bugService.getMyAssignedBugs());
    }

    // GET /api/bugs/stats — dashboard summary counts
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(bugService.getBugStats());
    }

    // GET /api/bugs/{id} — get one bug
    @GetMapping("/{id}")
    public ResponseEntity<BugResponse> getBugById(@PathVariable Long id) {
        return ResponseEntity.ok(bugService.getBugById(id));
    }

    // GET /api/bugs/project/{projectId} — get all bugs in a project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<BugResponse>> getBugsByProject(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(bugService.getBugsByProject(projectId));
    }

    // PUT /api/bugs/{id} — full update of a bug
    @PutMapping("/{id}")
    public ResponseEntity<BugResponse> updateBug(
            @PathVariable Long id,
            @Valid @RequestBody BugRequest request) {
        return ResponseEntity.ok(bugService.updateBug(id, request));
    }

    // PATCH /api/bugs/{id}/status — update status only (for Kanban drag-and-drop)
    // PATCH = partial update (only one field), vs PUT = full update
    @PatchMapping("/{id}/status")
    public ResponseEntity<BugResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam BugStatus status) {
        // Called like: PATCH /api/bugs/3/status?status=IN_PROGRESS
        return ResponseEntity.ok(bugService.updateBugStatus(id, status));
    }

    // PATCH /api/bugs/{id}/assign — assign a bug to a user
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')") // only admins can reassign bugs
    public ResponseEntity<BugResponse> assignBug(
            @PathVariable Long id,
            @RequestParam Long userId) {
        // Called like: PATCH /api/bugs/3/assign?userId=5
        return ResponseEntity.ok(bugService.assignBug(id, userId));
    }

    // DELETE /api/bugs/{id} — delete a bug
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // only admins can delete bugs
    public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
        return ResponseEntity.noContent().build();
    }
}