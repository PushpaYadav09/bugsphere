package com.bugsphere.bugsphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList; // used to initialize the bugs list so it's never null
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")  // TEXT in PostgreSQL = unlimited length string (vs VARCHAR which has a limit)
    private String description;

    // One project can have many bugs.
    // mappedBy = "project" means: the "project" field inside Bug owns this relationship.
    // cascade = ALL means: if we delete a project, all its bugs are automatically deleted too.
    // orphanRemoval = true: if we remove a bug from this list, it gets deleted from DB.
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default  // tells Lombok's builder to use this value as default instead of null
    private List<Bug> bugs = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}