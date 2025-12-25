package ge.studio101.service.dto.delivery;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private String pickupMethod;
    private Integer senderAddressId;
    private Integer senderPudoCity;
    private Integer senderPudoId;
    private String deliveryMethod;
    private Integer receiverAddressId;
    private Integer receiverPudoCity;
    private Integer receiverPudoId;
    private Boolean isIndividualPerson;
    private String senderPhoneNumber;
    private String receiverPhoneNumber;
    private String receiverName;
    private BigDecimal weight;
    private Integer itemsQuantity;
    private Boolean cod;
    private BigDecimal codAmount;
    private String payer;
    private String paymentType;
    private Boolean fragile;
    private Boolean toHavePicture;
    private Boolean insured;
    private String description;
    private String govNumber;
    private String organizationName;
    private String returnDecision;
    private Integer returnPudoId;
}
