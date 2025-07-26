package org.diagnosis.algorithms.reconstruction;

public class SourceModelException extends Exception {
    // Default constructor
    public SourceModelException() {
        super();
    }

    // Constructor that accepts a message
    public SourceModelException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public SourceModelException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public SourceModelException(Throwable cause) {
        super(cause);
    }
}
