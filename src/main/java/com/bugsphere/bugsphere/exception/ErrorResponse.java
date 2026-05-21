package com.bugsphere.bugsphere.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

// A consistent JSON shape for ALL error responses from BugSphere.
// Every error will look like:
// {
//   "timestamp": "2024-01-15T10:30:00",
//   "status": 404,
//   "error": "Not Found",
//   "message": "Bug not found with id: 5",
//   "path": "/api/bugs/5"
// }
// This is much easier for the frontend to handle than inconsistent formats.
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp; // when the error happened
    private int status;              // HTTP status code number (404, 403, 500...)
    private String error;            // short human-readable status name
    private String message;          // detailed message about what went wrong
    private String path;             // which URL triggered this error
}