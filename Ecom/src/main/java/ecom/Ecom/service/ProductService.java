package ecom.Ecom.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import ecom.Ecom.entity.Image;
import ecom.Ecom.entity.Product;
import ecom.Ecom.repository.ProductRepository;

@Service
public class ProductService  {
	
    @Autowired
    ImageService imageService;
    
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    CategoryService categoryService;

    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAllByEnabledTrue(pageable);
    }

    public Product getProduct(UUID uuid) {
        return productRepository.findById(uuid).orElse(null);
    }
    
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    //cannot delete a product because it has dependencies;
    public void delete(UUID uuid) {
        List<Image> images = imageService.findImagesByProductId(uuid);
        if(!images.isEmpty()){
            for (Image image : images) {
                imageService.delete(image.getUuid());
            }
        }
        productRepository.deleteById(uuid);
    }
    
    public List<Product> findByCategory(String filter) {
        return productRepository.findAllByEnabledTrue();
    }

    public List<Product> findByName(String keyword) {
        return productRepository.findByNameLike("%" + keyword + "%");
    }
    
    public Page<Product> findByCategory(String filter, Pageable pageable) {
        try {
            UUID uuid = UUID.fromString(filter); // Check if the string is a valid UUID
            return productRepository.findByCategory(categoryService.getCategory(uuid), pageable);
        } catch (IllegalArgumentException e) {
   
            return Page.empty();
        }
    }
    public Page<Product> findByNameLikePaged(String keyword, Pageable pageable) {
        return productRepository.findByNameLike("%"+keyword+"%", pageable);
    }
    public Page<Product> findAllPaged(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    public Product findById(UUID productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        return productOptional.orElse(null); 
    }

	public boolean existsByNameAndcategoryUuid(String name, UUID categoryUuid) {
		// TODO Auto-generated method stub
		return productRepository.existsByNameAndCategory_Uuid(name, categoryUuid);
	}

	public Long countAllProducts() {
        return productRepository.count();
    }
	
	
    
	/*
	 * public Page<Product> findByCategory(String filter, Pageable pageable) {
	 * return productRepository.findAllByEnabledTrue(pageable); }
	 * 
	 * public Page<Product> findByNameLikePaged(String keyword, Pageable pageable) {
	 * return productRepository.findByNameLike("%"+keyword+"%", pageable); }
	 * 
	 * public Page<Product> findAllPaged(Pageable pageable) { return
	 * productRepository.findAll(pageable); }
	 */
	
}