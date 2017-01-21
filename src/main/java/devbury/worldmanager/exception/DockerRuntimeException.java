package devbury.worldmanager.exception;

public class DockerRuntimeException extends RuntimeException {
    public DockerRuntimeException(Exception cause) {
        super(cause);
    }
}
