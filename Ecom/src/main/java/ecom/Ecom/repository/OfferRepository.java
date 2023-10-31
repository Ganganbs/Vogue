package ecom.Ecom.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.Offer;
import ecom.Ecom.entity.Product;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
    // You can add custom query methods if needed
	
	Offer findByProductId(UUID productId);
    boolean existsByProductId(UUID productId);
    
}
