package ge.studio101.service.dto.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAddressRequest {
    @NotBlank
    @Size(max = 255)
    private String address;

    @NotNull
    private AddressType type;

    @NotNull
    private Integer cityId;
}
