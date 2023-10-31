package ecom.Ecom.controller.admin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.aspectj.weaver.ast.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecom.Ecom.entity.Image;
import com.fasterxml.jackson.core.JsonProcessingException;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Product;
import ecom.Ecom.handler.UsernameProvider;
import ecom.Ecom.repository.CategoryRepository;
import ecom.Ecom.repository.ProductRepository;
import ecom.Ecom.service.CategoryService;
import ecom.Ecom.service.ImageService;
import ecom.Ecom.service.ProductService;

@Controller
public class ProductController {

	@Autowired
	ProductService productService;

	@Autowired
	ImageService imageService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	UsernameProvider usernameProvider;

	@Autowired
	ProductRepository productRepository;
	
	
	  @GetMapping("/product")
	  
	  @PreAuthorize("hasRole('ROLE_ADMIN')") public String all 
	  
	  (@RequestParam(required = false,defaultValue = "") String filter,
	  
	  @RequestParam(required = false, defaultValue = "") String keyword,
	  
	  @RequestParam(required = false, defaultValue = "ASC") String sort,
	  
	  @RequestParam(required = false, defaultValue = "name") String field,
	  
	  @RequestParam(required = false, defaultValue = "0") int page,
	  
	  @RequestParam(required = false, defaultValue = "10") int size, Model model){
	  
	  Pageable pageable = PageRequest.of(page, size,
	  Sort.by(Sort.Direction.fromString(sort), field));
	  
	  Page<Product> products = Page.empty();
	  
	  if(!filter.equals("")){ 
		  products = productService.findByCategory(filter, pageable); 
	  } else if (!keyword.equals("")) { 
		  products = productService.findByNameLikePaged(keyword, pageable); 
	  } else{
		  products = productService.findAllPaged(pageable); }
	  
	  model.addAttribute("categories", categoryService.findAll());
	  model.addAttribute("products",products);
	  
	  //Pagination Values 
	  model.addAttribute("filter", filter);
	  model.addAttribute("keyword", keyword); 
	  model.addAttribute("currentPage",page); 
	  model.addAttribute("totalPages", products.getTotalPages());
	  model.addAttribute("field", field); 
	  model.addAttribute("sort", sort);
	  model.addAttribute("pageSize", size); int startPage = Math.max(0, page - 1);
	  int endPage = Math.min(page + 1, products.getTotalPages() - 1);
	  model.addAttribute("startPage", startPage); model.addAttribute("endPage",endPage);
	  
	  model.addAttribute("empty", products.getTotalElements() == 0); 
	  return "product/ProductList";
	  }
	  
	  
	 
//	@GetMapping("/product")
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	public String product(@RequestParam(required = false, defaultValue = "") String filter,
//	                  @RequestParam(required = false, defaultValue = "") String keyword,
//	                  @RequestParam(required = false, defaultValue = "ASC") String sort,
//	                  @RequestParam(required = false, defaultValue = "name") String field,
//	                  Model model) {
//
//	    List<Product> products;
//
//	    if (!filter.equals("")) {
//	        products = productService.findByCategory(filter);
//	    } else if (!keyword.equals("")) {
//	        products = productService.findByName(keyword);
//	    } else {
//	        products = productService.findAll();
//	    }
//
//	    model.addAttribute("categories", categoryService.findAll());
//	    model.addAttribute("products", products);
//
//	    // Other attributes you might want to add
//	    model.addAttribute("filter", filter);
//	    model.addAttribute("keyword", keyword);
//	    model.addAttribute("field", field);
//	    model.addAttribute("sort", sort);
//	    model.addAttribute("empty", products.isEmpty());
//
//	    return "product/ProductList";
//	}
	
	
	
	 @GetMapping("/product/{uuid}")
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	 public String viewProduct(@PathVariable UUID uuid, Model model){
		 Product product = productService.getProduct(uuid);
		 List<Category> categories = categoryService.findAll();
		 model.addAttribute("product", product);
		 model.addAttribute("categories", categories);
		 
		 return "product/ProductDetailView";
		 }	
	 
	 
	 
	 @GetMapping("/createproduct")
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	 public String createProduct(Model model) throws JsonProcessingException {
		 //get brands and cats and attach
		 
		 Product product = new Product();
		 List<Category> categories = categoryService.findAll();
		 model.addAttribute("categories", categories);
		 model.addAttribute("product", product);
		 
		 return "product/CreateProduct";
		 }
	 
	 
//	 @ModelAttribute Product products,
	 
	 @PostMapping("/save")
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	 public String saveProduct(
			 @RequestParam("images") List<MultipartFile> imageFiles,
			 @RequestParam("name") String name,
			 @RequestParam("categoryUuid") UUID categoryUuid,
			 @RequestParam("description") String description,
			 @RequestParam("price") float price,
			 @RequestParam("stock") int stock,
			 RedirectAttributes redirectAttributes) throws IOException {
		 
		 List<Product>product= productService.findAll();
		 
		 
		 
//		 if (productService.existsByNameAndcategoryUuid(product.getName(), categoryUuid)) {
//			 redirectAttributes.addFlashAttribute("message", "Product with this name already exists.");
//			 return "redirect:/createproduct";
//		    }
		 
		boolean flag=true;
		 
		 for (Product product1 : product) {
			    if (product1.getName().equals(name) && product1.getCategory().getUuid().equals(categoryUuid)) {
			       flag=false;
			       redirectAttributes.addFlashAttribute("message", "Product with this name already exists in the category.");
			        return "redirect:/createproduct";
			    }
			} 
		 if(flag) {
		 Product product1 = new Product();
		 product1.setName(name);
		 Category category=categoryService.getCategory(categoryUuid);
		 product1.setCategory(category);
		 product1.setDescription(description);
		 product1.setPrice(price);
		 product1.setStock(stock);
		 product1 = productService.save(product1);
		 
		 
		 List<Image> images = new ArrayList<>();
		 if(!imageFiles.get(0).getOriginalFilename().equals("")){
			 for (MultipartFile image : imageFiles) {
				 String fileLocation = handleFileUpload(image); // Save the image and get its file location
				 Image imageEntity = new Image(fileLocation,product1); // Create an Image entity with the file location
				 imageEntity = imageService.save(imageEntity);
				 images.add(imageEntity); // Add the Image entity to the Product's list of images
	            }
		 }
		 }
		    
		 return "redirect:/product";
//			 return product.getUuid();
	        }
	        
		 
		 private String handleFileUpload(MultipartFile file) throws IOException {
			    String uploadDir = "src/main/resources/static/uploads";
			    String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
			    String filePath = uploadDir + "/" + fileName;
			    Path path = Paths.get(filePath);
			    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			    return fileName;
			}
		 
		 
		 
	@PostMapping("/update")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String updateProduct(@RequestParam("productUuid") UUID productUuid,
			@RequestParam("name") String name,
			@RequestParam("categoryUuid") UUID categoryUuid,
			@RequestParam("description") String description,
			@RequestParam("price") float price,
			@RequestParam("stock") int stock,
			@RequestParam("newImages") List<MultipartFile> newImages,
			@RequestParam("imagesToRemove") String[] imagesToRemove
			) throws IOException {
		    	
		for(String image : imagesToRemove){
			if(!image.equals("")){
	      		Image deletedImage = imageService.findFileNameById(UUID.fromString(image));
	      		handleDelete(deletedImage.getFileName());
	      		imageService.delete(UUID.fromString(image));
			}
		}
		 Product updatedProduct = new Product();
	        updatedProduct.setUuid(productUuid);
	        updatedProduct.setName(name);
	        updatedProduct.setCategory(categoryService.getCategory(categoryUuid));
	        updatedProduct.setDescription(description);
	        updatedProduct.setPrice(price);
	        updatedProduct.setStock(stock);
	        productService.save(updatedProduct);
	        //save new images
	                for (MultipartFile image : newImages) {
	                    if(image.getOriginalFilename()!="")  {
	                        String fileLocation = handleFileUpload(image); // Save the image and get its file location
	                        Image imageEntity = new Image(fileLocation, productService.getProduct(productUuid)); // Create an Image entity with the file location
	                        imageService.save(imageEntity);
	                    }
	                }
	        return "redirect:/product";

	    }

		       	
		private void handleDelete(String fileName) throws IOException {
			String rootPath = System.getProperty("user.dir");
			String uploadDir = rootPath + "/src/main/resources/static/uploads";

			// Get the file path
			String filePath = uploadDir + "/" + fileName;
			
			// Create a file object for the file to be deleted
			File file = new File(filePath);
			
			// Check if the file exists
			if (file.exists()) {
				// Delete the file
				file.delete();
				System.out.println("File deleted successfully!");
			} else {
				System.out.println("File not found!");
			}
		}
		
		        
		@PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		@PreAuthorize("hasRole('ROLE_ADMIN')")
		public ResponseEntity<String> uploadImage(@RequestPart("image") MultipartFile imageFile,
				@RequestPart("Uuid") String Uuid) throws IOException {

			Product product = productService.getProduct(UUID.fromString(Uuid));
			Image image = new Image();
			image.setProduct_id(product);
			image.setFileName(handleFileUpload(imageFile));
			image = imageService.save(image);
			
			return ResponseEntity.ok("Image uploaded successfully");
		}

		//delete from dashboard
//		@GetMapping("/delete/{uuid}")
//		@PreAuthorize("hasRole('ROLE_ADMIN')")
//		public String delete(@PathVariable UUID uuid){
//			
//			Product product = productService.getProduct(uuid);
//			product.setDeleted(true);
//			product.setEnabled(false);
//			
//			System.out.println("SOft deleting product "+product.getName());
//			productService.save(product);
//			
//			return "redirect:/product";
//		        }
		
		@GetMapping("/delete/{uuid}")
		@PreAuthorize("hasRole('ROLE_ADMIN')")
		public String delete(@PathVariable UUID uuid){
		    Product product = productService.getProduct(uuid);

		    if (product != null) {
		        // Perform the hard delete by removing the product from the database
		        productService.deleteProduct(product);

		        System.out.println("Hard deleting product " + product.getName());
		    } else {
		        // Handle the case where the product with the given UUID is not found
		        System.out.println("Product not found with UUID: " + uuid);
		    }

		    return "redirect:/product";
		}


		
	    //Toggle for enable and disable
//	    @GetMapping("/toggleStatus")
//	    public String toggleStatus(@RequestParam("uuid") String uuid){
//	        Product product = productService.getProduct(UUID.fromString(uuid));
//	        System.out.println("inside toggle");
//	        product.setEnabled(!product.isEnabled());
//	        productService.save(product);
//	        return "redirect:/product";
//	    }
	    
	    @GetMapping("/productsearch")
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    public String searchProductsByName(
	        @RequestParam("searchTerm") String searchTerm,
	        Model model
	    ) {
	        List<Product> products = searchProducts(searchTerm);

	        if (products.isEmpty()) {
	            model.addAttribute("message", "No products found for the provided search term.");
	        } else {
	            model.addAttribute("products", products);
	        }

	        return "product/ProductList"; // Adjust the view name as needed
	    }

	    public List<Product> searchProducts(String searchTerm) {
	        String pattern = searchTerm + "%";
	        return productRepository.findByNameLike(pattern);
	    }
	   
	    
		    
	    @GetMapping("/productfilter")
	    public String filterProducts(
	        @RequestParam(value = "category", required = false) String category,
	        Model model
	    ) {
	        List<Product> filteredProducts;

	        if (category != null) {
	            filteredProducts = productRepository.findByCategory(category);
	        } else {
	            // Handle the case when no filter is applied
	            filteredProducts = productRepository.findAll();
	        }

	        model.addAttribute("products", filteredProducts);
	        return "product/ProductList"; // Return the name of your view
	    }

	    @PostMapping("/addCategory")
	    public String addCategory(@ModelAttribute("category") Category category) {
	        try {
	            categoryService.addCategory(category);
	            return "redirect:/product/addproduct";
	        } catch (Exception ex) {
	            return "redirect:/admin/product/addproduct?error=categoryExists";
	        }
	    }
	    
		 @PostMapping("/add1")
		 @PreAuthorize("hasRole('ROLE_ADMIN')")
		 public String add(@ModelAttribute Category category,RedirectAttributes redirectAttributes){
			 // Check if a category with the same name already exists
			    if (categoryService.existsByName(category.getName())) {
			        redirectAttributes.addFlashAttribute("message", "Category with this name already exists.");
			    } else {
			        categoryService.addCategory(category);
			        redirectAttributes.addFlashAttribute("message", "Category added successfully.");
			    }
			    
			    return "redirect:/createproduct";
		 }
		 

		 @GetMapping("/toggleProduct/{id}")
		    @PreAuthorize("hasRole('ROLE_ADMIN')")
		    public String toggleProductById(@PathVariable("id") UUID uuid) {
		        Product product = productService.getProduct(uuid);
		        
		        
		            product.setDeleted(!product.isDeleted());
		            
		            // Save the updated product
		            productService.save(product);
		            
		          
			    return "redirect:/product";
		    }

	       
}
