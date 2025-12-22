package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminOrderDTO {
    private Long id;
    private String orderNumber;
    private String customer;
    private String total;
    private String status;
    private String delivery;
    private String date;
    private String address;
    private String notes;
    private String window;
}
