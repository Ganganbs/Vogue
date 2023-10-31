package ecom.Ecom.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

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
public class OrderHistory { //Order is a reserved keyword
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    
    private UUID deletedBy;
    

    private boolean deleted = false;

    private Float total=0F;

    private Float tax=0F;

    @Transient
    private Float gross;
    
    private Float offPrice=0F;

    private OrderStatus orderStatus = OrderStatus.UNKNOWN;

    private OrderType orderType=OrderType.UNKNOWN;

    //relationship

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserInfo userInfo;

//    @OneToMany
//    @JoinColumn(name = "oder_id")
//    @ToString.Exclude
//    private OrderItems orderItems;
    
    @OneToMany(mappedBy = "orderHistory", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItems> items = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name="address_id")
    @ToString.Exclude
    private Address userAddress;
    
    
// ----date----
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ... other fields, getters, and setters ...

//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Date createdAt) {
//        this.createdAt = createdAt;
//    }
    
//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product productId;
    
    //custom getters
    public Float getGross() {
     return total + tax;
    }

    public Long getGrossLong() {
        if (offPrice != null) {
            return (long) Math.round(total + tax);
        } else if (total == null) {
            return 0L;
        } else {
            return (long) Math.round(total + tax);
        }
    }

	public void setCreatedAt(LocalDateTime modifiedDate) {
		// TODO Auto-generated method stub
		this.createdAt = modifiedDate;
	}

	   @ManyToOne
	    @JoinColumn(name="coupon_id")
	    @ToString.Exclude
	    private Coupon coupon;



    public Float getOff(){
        if(offPrice == null){
            return 0F;
        }else{
            return offPrice;
        }
    }
    
    
    
}


    
    
  
  






