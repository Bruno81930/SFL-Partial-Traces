package org.diagnosis.algorithms.reconstruction;

public class ReconstructionException extends Exception {
    // Default constructor
    public ReconstructionException() {
        super();
    }

    // Constructor that accepts a message
    public ReconstructionException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public ReconstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public ReconstructionException(Throwable cause) {
        super(cause);
    }
}
