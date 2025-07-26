package org.diagnosis.evaluation.ground_truth;

public class GroundTruthException extends Exception {
    // Default constructor
    public GroundTruthException() {
        super();
    }

    // Constructor that accepts a message
    public GroundTruthException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public GroundTruthException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public GroundTruthException(Throwable cause) {
        super(cause);
    }
}
