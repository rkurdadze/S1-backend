package ge.studio101.service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class GooglePayService {

    /**
     * Эмуляция обращения к платежному шлюзу для проверки paymentToken.
     */
    public String processPaymentToken(String paymentToken, BigDecimal amount) {
        if (paymentToken == null || paymentToken.isBlank()) {
            throw new IllegalArgumentException("Payment token is empty");
        }
        log.info("Отправка токена Google Pay в платежный шлюз на сумму {}", amount);
        // Здесь должен быть вызов реального шлюза. Для демо возвращаем идентификатор транзакции.
        return "gateway-" + UUID.randomUUID();
    }
}
