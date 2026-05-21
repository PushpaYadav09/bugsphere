package com.bugsphere.bugsphere.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus tells Spring: when this exception is thrown,
// automatically return HTTP 404 Not Found to the client.
// Much cleaner than writing if/else checks in every controller.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // RuntimeException = unchecked exception, no need to declare throws everywhere

    public ResourceNotFoundException(String message) {
        super(message); // passes the message to RuntimeException
        // Example usage: throw new ResourceNotFoundException("Bug not found with id: 5")
    }
}