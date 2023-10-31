package ecom.Ecom.dto;

import java.util.UUID;

import ecom.Ecom.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private UUID uuid;

    private OrderStatus orderStatus;


}
