package org.diagnosis.app.exceptions.sflplus;

public class SFLPlusEvaluateException extends Exception {
    // Default constructor
    public SFLPlusEvaluateException() {
        super();
    }

    // Constructor that accepts a message
    public SFLPlusEvaluateException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a Throwable cause
    public SFLPlusEvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a Throwable cause
    public SFLPlusEvaluateException(Throwable cause) {
        super(cause);
    }
}
