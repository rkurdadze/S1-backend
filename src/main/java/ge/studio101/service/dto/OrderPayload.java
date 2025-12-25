package ge.studio101.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderPayload {
    private List<CartItemDTO> items;
    private ContactInfoDTO contact;
    private String deliveryOption;
    private String deliveryProvider;
    private String notes;
    private BigDecimal total;
    private String paymentToken;
    private Long userId;
    private Boolean emailNotification;
}
