package ecom.Ecom.dto;

import java.util.UUID;

import ecom.Ecom.dto.NewOrderDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class NewOrderDto {
    UUID addressId;
    String generatedOrderUuid;
    UUID productId;
    int quantity;  
    String paymentMethod;

}