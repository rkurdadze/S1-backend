package ge.studio101.service.controllers;

import ge.studio101.service.dto.OrderPayload;
import ge.studio101.service.dto.OrderResponseDTO;
import ge.studio101.service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderPayload orderPayload) {
        return ResponseEntity.ok(orderService.createOrder(orderPayload));
    }
}
