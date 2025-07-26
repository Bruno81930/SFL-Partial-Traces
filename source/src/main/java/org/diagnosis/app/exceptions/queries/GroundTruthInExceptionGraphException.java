package org.diagnosis.app.exceptions.queries;

public class GroundTruthInExceptionGraphException extends QueryException {
    // Default constructor
    public GroundTruthInExceptionGraphException() {
        super();
    }

    // Constructor that accepts a message
    public GroundTruthInExceptionGraphException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public GroundTruthInExceptionGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public GroundTruthInExceptionGraphException(Throwable cause) {
        super(cause);
    }
}
