package ge.studio101.service.models;

import ge.studio101.service.delivery.DeliveryProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Embedded
    private CustomerContact contact;

    @Column(name = "delivery_option", length = 120)
    private String deliveryOption;

    @Column(name = "delivery_window", length = 120)
    private String deliveryWindow;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_provider", length = 50)
    private DeliveryProvider deliveryProvider;

    @Column(name = "tracking_uuid", length = 36)
    private String trackingUuid;

    @Column(name = "tracking_code", length = 50)
    private String trackingCode;

    @Column(name = "delivery_status", length = 50)
    private String deliveryStatus;

    @Column(name = "delivery_label_url", columnDefinition = "TEXT")
    private String deliveryLabelUrl;

    @Column(name = "sender_delivery_external_id", length = 120)
    private String senderDeliveryExternalId;

    @Column(name = "receiver_delivery_external_id", length = 120)
    private String receiverDeliveryExternalId;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "payment_token", columnDefinition = "TEXT")
    private String paymentToken;

    @Column(name = "order_number", length = 50)
    private String orderNumber;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "email_notification")
    private boolean emailNotification;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
