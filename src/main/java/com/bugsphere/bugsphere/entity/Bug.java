package com.bugsphere.bugsphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bugs")
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bug title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")  // allow long descriptions
    private String description;

    // Store enum values as their string names in DB, e.g. "OPEN", "HIGH"
    // This makes the DB readable without needing to look up numbers.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default  // default value when creating a bug without specifying status
    private BugStatus status = BugStatus.OPEN;  // every new bug starts as OPEN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;  // default priority is MEDIUM

    // Many bugs belong to one project.
    // @ManyToOne — many bugs → one project
    // @JoinColumn — this table (bugs) will have a column "project_id" as a foreign key
    // nullable = false — every bug MUST belong to a project
    @ManyToOne(fetch = FetchType.LAZY)   // LAZY = don't load project from DB until we actually access it (saves memory)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // The user who CREATED this bug (e.g., a tester who found the bug)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // The developer this bug is ASSIGNED to (can be null if not yet assigned)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")  // no nullable=false here — assignment is optional
    private User assignedTo;

    @Column(updatable = false)  // creation time never changes
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;  // updated every time the bug is modified

    @PrePersist  // runs before INSERT
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate  // runs before every UPDATE — automatically tracks last modification time
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}