package ge.studio101.service.delivery.trackings;

import lombok.Data;

@Data
public class TrackingsResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
