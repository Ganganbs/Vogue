package ecom.Ecom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import ecom.Ecom.dto.NewOrderDto;
import ecom.Ecom.entity.Address;
import ecom.Ecom.entity.Cart;
import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;
import ecom.Ecom.entity.OrderType;
import ecom.Ecom.entity.TransactionDetails;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.CartRepository;
import ecom.Ecom.repository.OrderHistoryRepository;
import ecom.Ecom.service.CartService;
import ecom.Ecom.service.OrderHistoryService;
import ecom.Ecom.service.UserInfoService;

@Controller
public class RazorpayController{
	
	@Autowired
	OrderHistoryService orderHistoryService;
	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	UserInfoService userInfoService;
	
	@Autowired
	OrderHistoryRepository orderHistoryRepository;
	
	public String getCurrentUsername() {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
//	@GetMapping("/createtransaction/{amount}")
//	public TransactionDetails transaction(@PathVariable (name="amount") double amount) {
//		return orderHistoryService.createtransaction(amount);
//	}
	
	
//	@GetMapping("/payment/success")
//    public String handlePaymentSuccess(@AuthenticationPrincipal(expression = "username") String username,
//                                       @RequestParam(name = "addressId") UUID addressId,
//                                       @RequestParam(name = "productId", required = false) UUID productId,
//                                       @RequestParam(name = "amount")Double amount) {
//		
//		 UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
//		 
//		 
//        System.out.println("success");
//        System.out.println(username+addressId);
////        List<Cart> cartItems = cartRepository.findByUserInfo(username);
////        PaymentMode paymentMode = PaymentMode.Prepaid;
//        
//        List<Cart> cartItems = cartService.findByUser(userInfo);
//        OrderType orderType = OrderType.ONLINE;
//        
//        if (productId!=null && !cartItems.isEmpty()){
//        	
//            List<Cart> filteredCartItemsByProductId = cartItems.stream()
//                    .filter(cart -> cart.getProduct().getUuid().equals(productId))
//                    .collect(Collectors.toList());
//            orderService.placeOrder(filteredCartItemsByProductId, username, addressId, orderType,amount);
//            cartRepository.deleteAll(filteredCartItemsByProductId);
//            
//        }else if (!cartItems.isEmpty()) {
//            orderService.placeOrder(cartItems, username, addressId, orderType,amount);
//            cartRepository.deleteAll(cartItems);
//        }
//        
//        return "redirect:/";
//    }
	
//	@GetMapping("/payment/success")
//    public String handlePaymentSuccess(@AuthenticationPrincipal(expression = "username") UserInfo username,
//                                       @RequestParam(name = "addressId") Address addressId,
//                                       @RequestParam(name = "productId", required = false) UUID productId,
//                                       @RequestParam(name = "amount") Float amount) {
//        
//        // Get user info
//        UserInfo userInfo = userInfoService.findByUsername(username);
//        
//        System.out.println("success");
//        System.out.println(username + addressId);
//        
//        // Get cart items for the current user
//        List<Cart> cartItems = cartService.findByUser(userInfo);
//        OrderType orderType = OrderType.ONLINE;
//        
//        if (productId != null && !cartItems.isEmpty()) {
//            // Process cart items with specific product ID
//            List<Cart> filteredCartItemsByProductId = cartItems.stream()
//                    .filter(cart -> cart.getProductId().getUuid().equals(productId))
//                    .collect(Collectors.toList());
//            
//            orderHistoryService.placeOrder(filteredCartItemsByProductId, username, addressId, orderType, amount);
//            
//            cartService.deleteAll(filteredCartItemsByProductId);
//        } else if (!cartItems.isEmpty()) {
//            // Process all cart items
//            orderService.placeOrder(cartItems, username, addressId, orderType, amount);
//            cartService.deleteAll(cartItems);
//        }
//        
//        return "redirect:/";
//    }
//	
//	@RequestParam(name = "productId", required = false) UUID productId,
	
	@GetMapping("/payment/success")
	public String handlePaymentSuccess(
	                                   @RequestParam(name = "amount") float amount) {
//	    System.out.println("success");
////	    System.out.println(username);
//	    UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
//	    List<Cart> cartItems = cartService.findByUser(userInfo);
//	    
////	    List<Cart> cartItems = cartRepository.findByUserInfo(username);
//	    OrderType orderType=OrderType.ONLINE;
//
//	    if (productId != null && !cartItems.isEmpty()) {
//	        List<Cart> filteredCartItemsByProductId = cartItems.stream()
//	                .filter(cart -> cart.getProductId().getUuid().equals(productId))
//	                .collect(Collectors.toList());
//	        orderHistoryService.placeOrder(filteredCartItemsByProductId, userInfo.getUsername(), orderType, amount);
//	        cartRepository.deleteAll(filteredCartItemsByProductId);
//	    } else if (!cartItems.isEmpty()) {
//	        orderHistoryService.placeOrder(cartItems, userInfo,getCurrentUsername(), orderType, amount);
//	        cartRepository.deleteAll(cartItems);
//	    }

	    return "redirect:/";
	}

//	public void placeOrder(List<Cart> cartItems, String username, OrderType orderType, float amount) {
//        // Create a new OrderHistory object and set its properties
//        OrderHistory orderHistory = new OrderHistory();
//        orderHistory.setOrderType(orderType);
//        orderHistory.setTotal(amount);
//        // Set other properties as needed
//        
//        // Process the cart items and create order items
//        List<OrderItems> orderItemsList = new ArrayList<>();
//        float total = 0;
//        for (Cart cartItem : cartItems) {
//            if (cartItem.getQuantity() > 0) {
//                OrderItems orderItem = new OrderItems();
//                orderItem.setQuantity(cartItem.getQuantity());
//                orderItem.setOrderPrice(cartItem.getProductId().getPrice());
//                // Set other order item properties as needed
//                
//                // Update total and other calculations
//                total += (cartItem.getProductId().getPrice() * cartItem.getQuantity());
//                
//                // Add the order item to the list
//                orderItemsList.add(orderItem);
//            }
//        }
//        
//        // Set the order items and total to the order history
//        orderHistory.setItems(orderItemsList);
//        orderHistory.setTotal(total);
//        
//        // Save the order history to the database using your repository
//        orderHistoryRepository.save(orderHistory);
//        
//        // Perform other necessary actions
//    }

	
}

