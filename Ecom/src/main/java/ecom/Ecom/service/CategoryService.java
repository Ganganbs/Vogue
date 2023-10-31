package ecom.Ecom.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.Category;
import ecom.Ecom.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public void delete(UUID uuid) {
        categoryRepository.deleteById(uuid);
    }

    public void updateCategory(Category category) {
        categoryRepository.save(category);
    }


    public void save(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> findAll() {
       return categoryRepository.findAll();
    }

    public Category getCategory(UUID uuid) {
        return categoryRepository.findById(uuid).orElse(null);
    }
    
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    public void deleteCategory(UUID uuid) {
        // Fetch the category by its UUID
        Category category = categoryRepository.findById(uuid)
                .orElseThrow();

        // Perform the deletion
        categoryRepository.delete(category);
    }
    
    public Long countAllCategory() {
        return categoryRepository.count();
    }
    
}
