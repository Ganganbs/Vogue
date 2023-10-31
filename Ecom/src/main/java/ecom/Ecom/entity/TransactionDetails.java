package ecom.Ecom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class TransactionDetails {

	private String orderId;
	private String currency;
	private Integer amount;
	private String key;
}
