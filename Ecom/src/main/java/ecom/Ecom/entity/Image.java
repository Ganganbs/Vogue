package ecom.Ecom.entity;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import ecom.Ecom.entity.Product;
import ecom.Ecom.repository.ImageRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
	private UUID uuid;
	
	private String fileName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product_id;
	
	public Image(String fileName, Product product) {
		this.fileName=fileName;
		this.product_id=product;
	}

//	public boolean isImagePresent(UUID uuid) {
//	    Optional<Image> imageOptional = ImageRepository.findById(uuid);
//	    return imageOptional.isPresent();
//	}

}
