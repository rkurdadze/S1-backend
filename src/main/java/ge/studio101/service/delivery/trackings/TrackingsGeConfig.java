package ge.studio101.service.delivery.trackings;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Configuration
public class TrackingsGeConfig {

    @Bean
    public RestTemplate trackingsGeRestTemplate(RestTemplateBuilder builder, TrackingsGeProperties properties) {
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            if (properties.getToken() != null && !properties.getToken().isBlank()) {
                request.getHeaders().add("Authorization", "Bearer " + properties.getToken());
            }
            return execution.execute(request, body);
        };
        return builder
                .setConnectTimeout(properties.getTimeout())
                .setReadTimeout(properties.getTimeout())
                .additionalInterceptors(List.of(authInterceptor))
                .build();
    }
}
