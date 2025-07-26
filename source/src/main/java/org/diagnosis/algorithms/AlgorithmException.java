package org.diagnosis.algorithms;

public class AlgorithmException extends Exception {
    // Default constructor
    public AlgorithmException() {
        super();
    }

    // Constructor that accepts a message
    public AlgorithmException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public AlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public AlgorithmException(Throwable cause) {
        super(cause);
    }
}
