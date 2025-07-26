package org.diagnosis.app.exceptions.reconstructed;

public class ReconstructedEvaluateException extends Exception {
    // Default constructor
    public ReconstructedEvaluateException() {
        super();
    }

    // Constructor that accepts a message
    public ReconstructedEvaluateException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public ReconstructedEvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public ReconstructedEvaluateException(Throwable cause) {
        super(cause);
    }
}
