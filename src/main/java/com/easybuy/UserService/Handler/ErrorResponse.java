// java
package com.easybuy.UserService.Handler;

import java.time.Instant;

/**
 * Simple error response used by the global handler.
 */
public class ErrorResponse
{
    private final String error;
    private final String message;
    private final Instant timestamp;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getError() { return error; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
