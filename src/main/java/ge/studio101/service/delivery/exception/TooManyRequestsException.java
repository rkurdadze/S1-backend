package ge.studio101.service.delivery.exception;

import org.springframework.http.HttpStatus;

import java.time.Duration;

public class TooManyRequestsException extends DeliveryClientException {
    private final Duration retryAfter;

    public TooManyRequestsException(Duration retryAfter, String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
        this.retryAfter = retryAfter;
    }

    public Duration getRetryAfter() {
        return retryAfter;
    }
}
