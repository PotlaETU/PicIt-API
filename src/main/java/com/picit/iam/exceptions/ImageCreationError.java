package com.picit.iam.exceptions;

public class ImageCreationError extends RuntimeException {
    public ImageCreationError(String message, Throwable cause) {
        super(message, cause);
    }
}
