package org.diagnosis.app.exceptions.reconstructed;

public class ReconstructedLoadException extends Exception {
    // Default constructor
    public ReconstructedLoadException() {
        super();
    }

    // Constructor that accepts a message
    public ReconstructedLoadException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public ReconstructedLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public ReconstructedLoadException(Throwable cause) {
        super(cause);
    }
}
