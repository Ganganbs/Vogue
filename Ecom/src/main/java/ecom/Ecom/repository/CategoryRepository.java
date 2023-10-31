package ecom.Ecom.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import ecom.Ecom.repository.CategoryRepository;
import ecom.Ecom.service.CategoryService;
import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.UserInfo;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>{

	    boolean existsByName(String name);
	    
	    List<Category> findByNameLike(String pattern);
	
}
