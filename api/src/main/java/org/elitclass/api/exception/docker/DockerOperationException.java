package org.elitclass.api.exception.docker;

public class DockerOperationException extends RuntimeException {
    public DockerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
