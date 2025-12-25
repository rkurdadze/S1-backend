package ge.studio101.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class AdminDeliverySettingsDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceSetting {
        private String service;
        private boolean enabled;
        private String label;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private List<ServiceSetting> data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        private List<ServiceSetting> services;
    }
}