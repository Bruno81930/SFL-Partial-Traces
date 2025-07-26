package org.diagnosis.app.exceptions.queries;

public class QueryException extends Exception {
    // Default constructor
    public QueryException() {
        super();
    }

    // Constructor that accepts a message
    public QueryException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public QueryException(Throwable cause) {
        super(cause);
    }
}
