package org.diagnosis.app.exceptions.reconstructed;

public class ReconstructedFailedException extends Exception {
    // Default constructor
    public ReconstructedFailedException() {
        super();
    }

    // Constructor that accepts a message
    public ReconstructedFailedException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public ReconstructedFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public ReconstructedFailedException(Throwable cause) {
        super(cause);
    }
}
