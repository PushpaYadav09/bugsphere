package com.bugsphere.bugsphere.entity;

// These are the only valid statuses a bug can have.
// Think of it like a Jira board — a bug moves through these stages.
public enum BugStatus {
    OPEN,         // bug was just reported, nobody is working on it yet
    IN_PROGRESS,  // a developer has picked it up and is working on it
    RESOLVED,     // developer says it's fixed, waiting for review/verification
    CLOSED        // confirmed fixed, no more action needed
}