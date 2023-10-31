//package ecom.Ecom.controller;
//
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import ecom.Ecom.entity.Cart;
//import ecom.Ecom.entity.Product;
//import ecom.Ecom.entity.UserInfo;
//import ecom.Ecom.service.CartService;
//import ecom.Ecom.service.ProductService;
//import ecom.Ecom.service.UserInfoService;
//
//@Controller
//public class CartRestController {
//	
//	@Autowired
//	CartService cartService;
//	
//	@Autowired
//	UserInfoService userInfoService;
//	
//	public String getCurrentUsername() {
//		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
//		return authentication.getName();
//	}

//    @PostMapping("/addcart")
//    public String addToCart(@RequestParam("quantity") int quantity,
//    		@RequestParam("productUuid") UUID productUuid,Model model) {
//        String currentUsername = String.valueOf(getCurrentUsername());
//        System.out.println("inside addcart");
//
//        if (quantity < 0 || quantity > 100) {
//            return "invalid quantity, accepted values are 0-100";
//        }
//
//        if (cartService.addToCart(productUuid,quantity)) {
//        	return "redirect:/productDetail?productUuid=" + productUuid;
//        } else {
//            return "not add";
//        }
//    }
    
//    @PostMapping("/delete")
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//    public String deleteFromCart(@RequestParam("productUuid") UUID productUuid) {
//        // Get the current user
//        String currentUsername = String.valueOf(getCurrentUsername());
//        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
//
//        // Get the user's cart items
//        List<Cart> carts = cartService.findByUser(userInfo);
//        String deletedStatus = "not deleted";
//
//        // Iterate through the user's cart items
//        for (Cart cart : carts) {
//            if (cart.getProductId().getUuid().equals(productUuid)) {
//                // If the productUuid matches, delete the cart item
//                cartService.delete(cart);
//                deletedStatus = "deleted";
//                break;
//            }
//        }
//        return deletedStatus;
//    }
    
//    @PostMapping("/update")
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//    public String updateCart(@RequestParam("cartItemId") List<UUID> cartItemIds,
//                             @RequestParam("quantity") List<Integer> quantities) {
//        // Assuming the order of cartItemIds and quantities corresponds to the cart items in the table
//
//        for (int i = 0; i < cartItemIds.size(); i++) {
//            UUID cartItemId = cartItemIds.get(i);
//            int quantity = quantities.get(i);
//
//            if (quantity == 0) {
//                cartService.deleteCartById(cartItemId);
//                System.out.println(cartItemId + " deleted");
//            } else {
//                Cart existingCartItem = cartService.findCartByUuid(cartItemId);
//                if (existingCartItem.getQuantity() != quantity) {
//                    System.out.println(cartItemId + " updated");
//                    existingCartItem.setQuantity(quantity);
//                    cartService.save(existingCartItem);
//                }
//            }
//        }
//
//        return "redirect:/viewCart"; // Redirect back to the same update page after saving changes
//    }


    
    
//}
