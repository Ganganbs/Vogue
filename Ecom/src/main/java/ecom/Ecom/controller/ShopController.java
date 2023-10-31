package ecom.Ecom.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.twilio.twiml.messaging.Redirect;

import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;
import ecom.Ecom.entity.OrderStatus;
import ecom.Ecom.entity.Product;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.service.OrderHistoryService;
import ecom.Ecom.service.OrderItemsService;
import ecom.Ecom.service.ProductService;
import ecom.Ecom.service.UserInfoService;

@Controller
public class ShopController {

	@Autowired
	ProductService productService;
	
	@Autowired
	OrderHistoryService orderHistoryService;
	
	@Autowired
	UserInfoService userInfoService;
	
	@Autowired
	OrderItemsService orderItemService;
	
//	@GetMapping("/")
//    public String home(Model model) {
//          return "shop/index"; 
//    }
	
	public String getCurrentUsername() {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	@GetMapping("/productDetail")
	public String productView(@RequestParam(value = "productUuid", required = false) String productUuid,
			Model model){
		String currentUsername = String.valueOf(getCurrentUsername());
        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
        String firstName = userInfo.getFirstName();
        model.addAttribute("firstName", firstName);
        
		Product selectedProduct=new Product();
		selectedProduct= productService.getProduct(UUID.fromString(productUuid));
        model.addAttribute("product", selectedProduct);
        return "shop/single-product";
	}
	
	
	//-----Order-------
	
//	 @GetMapping("/orders")
//	    public String orderHistory(Model model) {
//	    	List<OrderHistory> orderList=orderHistoryService.findByUserInfo(userInfoService.findByUsername(getCurrentUsername()));
//	    	model.addAttribute("orderList",orderList);
//	    	return "shop/OrderHistory";
//	    }
	
	@GetMapping("/orders")
	public String orderHistory(Model model) {
	    UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
	    List<OrderHistory> orderList = orderHistoryService.findByUserInfo(userInfo);
	    model.addAttribute("orderList", orderList);
	    
	    String currentUsername = String.valueOf(getCurrentUsername());
        UserInfo userInfo1 = userInfoService.findByUsername(currentUsername);
        String firstName = userInfo1.getFirstName();
        model.addAttribute("firstName", firstName);
        
        
        
	    return "shop/OrderHistory";
	}
	 
//	 @PostMapping("/cancel-order")
//	    public String returnOrder(@RequestParam("uuid") UUID uuid) {
//	    	
//	        OrderHistory order = orderHistoryService.findById(uuid);
//	        
//	        
//	        	
//			 if (order != null && order.getOrderStatus() != OrderStatus.CANCELLED) {
//		         order.setOrderStatus(OrderStatus.CANCELLED);
//		         orderHistoryService.save(order);
//		          
//			 }
//	        return "redirect:/orders";
//	    }

	
	 @PostMapping("/cancel-order")
	 public String cancelOrder(@RequestParam("orderId") UUID orderId,
	                           RedirectAttributes redirectAttributes) {
	     try {

	         
	         OrderHistory order = orderHistoryService.findById(orderId);
	         if (order != null && order.getOrderStatus() != OrderStatus.CANCELLED) {
	             order.setOrderStatus(OrderStatus.CANCELLED);
	          
	             orderHistoryService.save(order);
	         }
	         List<OrderItems> orderItems=order.getItems();
	         
	         for (OrderItems orderItem : orderItems) {
	        	    Product product = orderItem.getProductId();
	        	    int newStock = product.getStock() + orderItem.getQuantity();
	        	    product.setStock(newStock);
	        	    productService.save(product);
	        	}


	         
	         return "redirect:/orders";
	     } catch (Exception e) {
	         return"redirect:/orders";
	     }
	 }
	
	 
	 
	 
//	 @RequestParam("productId") UUID productId,

//     @RequestParam("quantity") int quantity,
	 @PostMapping("/return-order")
	 public String returnOrder(@RequestParam("orderId") UUID orderId,
	                           RedirectAttributes redirectAttributes) {
	     try {
	         // Retrieve the product by ID
//	         Product product = productService.findById(productId);
	    	 

	         
//	         int newStock = product.getStock() + quantity;
//	         product.setStock(newStock);
//	         productService.save(product); // Save the updated product

	         
	         OrderHistory order = orderHistoryService.findById(orderId);
	         if (order != null && order.getOrderStatus() != OrderStatus.CANCELLED) {
	             order.setOrderStatus(OrderStatus.RETURN_REQUESTED);
	          
	             orderHistoryService.save(order);
	         }
	         List<OrderItems> orderItems=order.getItems();
//	         Product product =orderItems.get(0).getProductId();
//	         product.setStock((orderItems.get(0).getProductId().getStock())+(orderItems.get(0).getQuantity()));
//	         productService.save(product);
	         
	         for (OrderItems orderItem : orderItems) {
	        	    Product product = orderItem.getProductId();
	        	    int newStock = product.getStock() + orderItem.getQuantity();
	        	    product.setStock(newStock);
	        	    productService.save(product);
	        	}


	         
	         return "redirect:/orders";
	     } catch (Exception e) {
	         return"redirect:/orders";
	     }
	 }
	 
	 
	 
	 
	 
	    @GetMapping("/orderDetails")
	    public String orderDetails(@RequestParam(name = "orderId") UUID orderId,
	                               @RequestParam(name = "newOrderFlag", required = false, defaultValue = "false") boolean newOrderFlag,
	                               Model model) {

	        OrderHistory orderHistory = orderHistoryService.findById(orderId);
	        List<OrderItems> orderItems = orderItemService.findByOrder(orderHistory);
	        
	        String currentUsername = String.valueOf(getCurrentUsername());
	        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
	        String firstName = userInfo.getFirstName();
	        model.addAttribute("firstName", firstName);     
	        
//	        String progressBarClass;
//	        String badgeClass;
//
//	        int orderStatusPercentage = 0;
//	        
//			if (orderStatusPercentage >= 100) {
//	            progressBarClass = "progress-bar bg-danger";
//	            badgeClass = "badge bg-danger me-1";
//	        } else if (orderHistory.getOrderStatus() == OrderStatus.CANCELLED) {
//	            progressBarClass = "progress-bar bg-danger";
//	            badgeClass = "badge bg-danger me-1";
//	        } else if (orderHistory.getOrderStatus() == OrderStatus.RETURNED) {
//	            progressBarClass = "progress-bar bg-warning";
//	            badgeClass = "badge bg-warning me-1";
//	        } else if (orderStatusPercentage == 50) {
//	            progressBarClass = "progress-bar bg-warning";
//	            badgeClass = "badge bg-warning me-1";
//	        } else {
//	            progressBarClass = "progress-bar bg-success";
//	            badgeClass = "badge bg-success me-1";
//	        }
//
//	        model.addAttribute("progressBarClass", progressBarClass);
//	        model.addAttribute("badgeClass", badgeClass);

	        
	        model.addAttribute("orderDate", orderHistory.getCreatedAt());
	        model.addAttribute("order", orderHistory);
	        model.addAttribute("orderItems", orderItems);
	        model.addAttribute("orderSuccessfulAnimation", newOrderFlag);
	        
	        return "shop/orderDetails";
	    }
	    
	    String formatDate(Date date) {
	        //format date to readable format
	        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	        return dateTime.format(formatter);
	    }
	

	    @GetMapping("/about")
	    public String about(Model model) {
	    	String currentUsername = String.valueOf(getCurrentUsername());
	        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
	        String firstName = userInfo.getFirstName();
	        model.addAttribute("firstName", firstName);
	        return "shop/about";
	    }
	    
	    @GetMapping("/allproducts")
	    public String allproduct( Model model) {
	    	 List<Product> products= productService.findAll();

	 	    String currentUsername = String.valueOf(getCurrentUsername());
	        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
	        String firstName = userInfo.getFirstName();
	        model.addAttribute("firstName", firstName);
			model.addAttribute("products", products);
	        return "shop/products";
	    }
	    
	    @GetMapping("/contact")
	    public String contact(Model model) {
	    	String currentUsername = String.valueOf(getCurrentUsername());
	        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
	        String firstName = userInfo.getFirstName();
	        model.addAttribute("firstName", firstName);
	        return "shop/contact";
	    }
	    
//	    Invoice Generate----------------
	    
	    @GetMapping("/generate-invoice")
	    public ResponseEntity<byte[]> generateInvoice(@RequestParam("orderId") UUID orderId) throws Exception {
	        byte[] pdfBytes = orderHistoryService.generateInvoice(orderId);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDispositionFormData("attachment", "invoice.pdf");

	        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	    }
	    
	    
   
}
