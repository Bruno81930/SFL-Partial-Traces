package org.diagnosis.app;

public class EvaluateException extends Exception {
    // Default constructor
    public EvaluateException() {
        super();
    }

    // Constructor that accepts a message
    public EvaluateException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public EvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public EvaluateException(Throwable cause) {
        super(cause);
    }
}
