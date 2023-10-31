package ecom.Ecom.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.Category;

import ecom.Ecom.entity.Product;
import ecom.Ecom.entity.UserInfo;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

	@SuppressWarnings("unchecked")
	Product save(Product product);

	List<Product> findAll();

	public Optional<Product> findById(UUID uuid);

	void deleteById(UUID uuid);
	
	List<Product> findByNameLike(String key);
	
	Page<Product> findByNameLike(String key, Pageable pageable);
	 
	Page<Product> findByNameContainingAndEnabledIsTrue(String key, Pageable pageable);

	Page<Product> findAllByEnabledTrue(Pageable pageable);

	List<Product> findAllByEnabledTrue();

	Page<Product> findByCategory(Category category, Pageable pageable);

//	List<Product> findByProductNameLike(String pattern);
	 List<Product> findByCategory(String category);
	 
	 boolean existsByName(String name);
	 
	 boolean existsByNameAndCategory_Uuid(String name, UUID categoryUuid);


	
	
}
