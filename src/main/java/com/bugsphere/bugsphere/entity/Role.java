package com.bugsphere.bugsphere.entity;

// An enum is like a fixed list of allowed values.
// Instead of storing "admin" or "user" as raw text (which can have typos),
// we define the only valid options here. Java enforces them at compile time.
public enum Role {
    ROLE_ADMIN,   // has full access: create projects, delete bugs, manage users
    ROLE_USER     // can create bugs, update assigned bugs, view everything
}