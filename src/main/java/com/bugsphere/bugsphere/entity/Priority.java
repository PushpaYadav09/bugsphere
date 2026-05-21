package com.bugsphere.bugsphere.entity;

// Priority tells the team how urgently a bug needs to be fixed.
public enum Priority {
    LOW,      // minor issue, fix when free
    MEDIUM,   // should be fixed soon but not blocking anything
    HIGH,     // blocking work or affecting many users, fix ASAP
    CRITICAL  // production is down or data is at risk, fix immediately
}