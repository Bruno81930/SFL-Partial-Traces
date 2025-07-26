package org.diagnosis.app.exceptions.partial;

public class PartialEvaluateException extends Exception {
    // Default constructor
    public PartialEvaluateException() {
        super();
    }

    // Constructor that accepts a message
    public PartialEvaluateException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public PartialEvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public PartialEvaluateException(Throwable cause) {
        super(cause);
    }
}
