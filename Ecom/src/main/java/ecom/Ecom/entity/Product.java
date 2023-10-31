package ecom.Ecom.entity;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Image;
import ecom.Ecom.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Data
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product  {

	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
	private UUID uuid;
	
	private String name;
	
	@Lob
	private String description;
	
	private float price;
	
	private int stock;
	
	private  boolean enabled= true;
	
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(mappedBy = "product_id")
	@ToString.Exclude
	private List<Image> images;
	
	private boolean deleted = false;
}