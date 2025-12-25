package ge.studio101.service.delivery.exception;

import org.springframework.http.HttpStatus;

public class DeliveryClientException extends RuntimeException {
    private final HttpStatus status;

    public DeliveryClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
