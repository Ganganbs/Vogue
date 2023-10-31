package ecom.Ecom.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    private UUID deletedBy;
    
    private int quantity;

    private float orderPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderHistory orderHistory;

    public String getItemName(){
        return this.getProductId().getName();
    }

    public Float getTotal(){
        return this.quantity * this.orderPrice;
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

}

	

   