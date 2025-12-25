package ge.studio101.service.dto.delivery;

import ge.studio101.service.delivery.DeliveryProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    private Integer orderId;
    private String uuid;
    private String trackingCode;
    private String currentStatus;
    private String labelUrl;
    private Integer senderAddressId;
    private Integer receiverAddressId;
    private Integer senderPudoId;
    private Integer receiverPudoId;
    private DeliveryProvider provider;
}
