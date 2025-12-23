package ge.studio101.service.models;

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
