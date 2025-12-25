package ge.studio101.service.delivery.trackings;

import ge.studio101.service.delivery.DeliveryProvider;
import ge.studio101.service.dto.delivery.OrderInfoDTO;
import lombok.Data;

@Data
public class TrackingsOrderResponse {
    private boolean success;
    private Integer orderId;
    private String uuid;
    private String trackingCode;
    private String message;
    private String labelUrl;
    private Integer senderAddressId;
    private Integer receiverAddressId;
    private Integer senderPudoId;
    private Integer receiverPudoId;

    public OrderInfoDTO toOrderInfo() {
        return OrderInfoDTO.builder()
                .orderId(orderId)
                .uuid(uuid)
                .trackingCode(trackingCode)
                .currentStatus("CREATE")
                .labelUrl(labelUrl)
                .senderAddressId(senderAddressId)
                .receiverAddressId(receiverAddressId)
                .senderPudoId(senderPudoId)
                .receiverPudoId(receiverPudoId)
                .provider(DeliveryProvider.TRACKINGS_GE)
                .build();
    }
}
