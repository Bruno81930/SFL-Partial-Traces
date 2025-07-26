package org.diagnosis.app.exceptions.reconstructed;

public class ReconstructedSaveException extends Exception {
    // Default constructor
    public ReconstructedSaveException() {
        super();
    }

    // Constructor that accepts a message
    public ReconstructedSaveException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public ReconstructedSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public ReconstructedSaveException(Throwable cause) {
        super(cause);
    }
}
