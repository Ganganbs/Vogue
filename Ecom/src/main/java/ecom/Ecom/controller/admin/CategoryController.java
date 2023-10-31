package ecom.Ecom.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.CategoryRepository;
import ecom.Ecom.service.CategoryService;

@Controller
public class CategoryController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	CategoryService categoryService;

	@GetMapping("/category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String all(Model model){
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories",categories);
        return "category/CategoryList";
    }
	
	@GetMapping("/createcategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createCategory(){
        return "category/CreateCategory";
    }
    

	@PostMapping("/add")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String add(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
	    // Check if a category with the same name already exists
	    if (categoryService.existsByName(category.getName())) {
	        redirectAttributes.addFlashAttribute("message", "Category with this name already exists.");
	    } else {
	        categoryService.addCategory(category);
	        redirectAttributes.addFlashAttribute("message", "Category added successfully.");
	    }
	    
	    return "redirect:/category";
	}
	 
	 @GetMapping("/category/{uuid}")
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	 public String getCategory(@PathVariable UUID uuid, Model model){
		 Category category = categoryService.getCategory(uuid);
		 
		 model.addAttribute("uuid",uuid);
		 model.addAttribute("name",category.getName());
		 model.addAttribute("description",category.getDescription());

		 return "category/CategoryDetailView";
	 }
	 
	 //Update category
	    @PostMapping("/updateCategory")
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    public String update(@ModelAttribute Category category ,RedirectAttributes redirectAttributes){
	    	if (categoryService.existsByName(category.getName())) {
		        redirectAttributes.addFlashAttribute("message", "Category with this name already exists.");
		    } else {
		    	categoryService.updateCategory(category);
		    	redirectAttributes.addFlashAttribute("message", "Category is updated.");
		    }
	        return "redirect:/category";

	    }
	    
	    
	    
	    @GetMapping("/deleteCategory/{id}")
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    public String deleteCategoryById(@PathVariable("id") UUID uuid) {
	        Category category = categoryService.getCategory(uuid);
	        
	        // Check if the category is not already deleted
	        if (!category.isDeleted()) {
	            // Perform a hard delete by removing the category from the database
	            categoryService.deleteCategory(uuid);
	            
	            // Optionally, you can log the deletion or perform any other necessary actions
	            
	            System.out.println("Hard deleted category");
	        }
	        
	        return "redirect:/category";
	    }
	    
	    
//	    Soft Delete

	    @GetMapping("/toggleCategory/{id}")
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    public String toggleCategoryById(@PathVariable("id") UUID uuid) {
	        Category category = categoryService.getCategory(uuid);
	        
	        // Toggle the deleted status
	        category.setDeleted(!category.isDeleted());
	        
	        // Save the updated category
	        categoryService.save(category);
	        
	        return "redirect:/category";
	    }
	    
	    
//	    search--------------------------------------------------------------
	    
	    @GetMapping("/categorysearch")
	    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	    public String searchCategoriesByName(@RequestParam("searchTerm") String searchTerm, Model model) {
	        List<Category> categories = searchCategories(searchTerm);
	        
	        if (categories.isEmpty()) {
	            model.addAttribute("message", "No categories found for the provided search term.");
	        } else {
	            model.addAttribute("categories", categories);
	        }
	        
	        return "category/CategoryList"; // Assuming there is a "CategoryList" template to display categories.
	    }

	    public List<Category> searchCategories(String searchTerm) {
	        String pattern = searchTerm + "%";
	        return categoryRepository.findByNameLike(pattern);
	    }


	 
}
