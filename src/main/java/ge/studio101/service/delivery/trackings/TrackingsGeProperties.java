package ge.studio101.service.delivery.trackings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "trackings.ge")
public class TrackingsGeProperties {
    private String apiUrl = "https://trackings.ge";
    private String token = "";
    private Duration timeout = Duration.ofSeconds(15);
    private int rateLimit = 60;
}
