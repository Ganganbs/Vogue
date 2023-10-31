package ecom.Ecom.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.attoparser.trace.MarkupTraceEvent.ProcessingInstructionTraceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecom.Ecom.dto.CouponValidityResponseDto;
import ecom.Ecom.dto.NewOrderDto;
import ecom.Ecom.entity.Address;
import ecom.Ecom.entity.Cart;
import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;
import ecom.Ecom.entity.OrderStatus;
import ecom.Ecom.entity.OrderType;
import ecom.Ecom.entity.Product;
import ecom.Ecom.entity.TransactionDetails;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.OrderHistoryRepository;
import ecom.Ecom.repository.ProductRepository;
import ecom.Ecom.service.AddressService;
import ecom.Ecom.service.CartService;
import ecom.Ecom.service.CouponService;
import ecom.Ecom.service.OrderHistoryService;
import ecom.Ecom.service.ProductService;
import ecom.Ecom.service.UserInfoService;

@Controller
public class CartController {
	
	@Autowired
	UserInfoService userInfoService;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	AddressService addressService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	OrderHistoryRepository orderHistoryRepository;
	
	@Autowired
	OrderHistoryService orderHistoryService;
	
	@Autowired
	CouponService couponService;
	
	
	public String getCurrentUsername() {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	@GetMapping("/viewCart")
	public String viewCart(Model model,
	                       @RequestParam(required = false) UUID addressUUID) {
	    UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
	    
	    String currentUsername = String.valueOf(getCurrentUsername());
        UserInfo userInfo1 = userInfoService.findByUsername(currentUsername);
        String firstName = userInfo1.getFirstName();
        model.addAttribute("firstName", firstName);

	    long count = userInfo.getSavedAddresses()
	            .stream()
	            .filter(a -> a.isEnabled())
	            .count();

	    if (count == 0) {
	        System.out.println("No addresses found for user, redirecting to profile");
	        return "redirect:/profile?addAddress=true";
	    }

	    List<Cart> cartItems = cartService.findByUser(userInfo);

	    // Remove items if they are out of stock or disabled
	    List<Address> addressList = addressService.findByUserInfoAndEnabled(userInfo, true);

	    model.addAttribute("addressList", addressList);

	    model.addAttribute("nameOfUser", userInfo.getFirstName() + " " + userInfo.getLastName());

	    if (cartItems.size() == 0) {
	        model.addAttribute("cartEmpty", true);
	    } else {
	        model.addAttribute("cartEmpty", false);
	    }

	    model.addAttribute("loggedIn", true);

	    model.addAttribute("cartItems", cartItems);

//	    float total = (float) cartItems.stream()
//	            .mapToDouble(cartItem -> cartItem.getProductId().getPrice() * cartItem.getQuantity())
//	            .sum();
	    
	    CouponValidityResponseDto couponValidityResponseDto=cartService.checkCouponValidity();
        double cartTotal=couponValidityResponseDto.getCartTotal();
        System.out.println(couponValidityResponseDto.getCartTotal()+"loading viecart");
        
        float total = (float) cartTotal;
//        float tax = total / 100 * 18; //18%
//        total -= tax;
//        float gross = total + tax;
        if(userInfo.getCoupon()!=null) {
        	model.addAttribute("appliedCouponCode",userInfo.getCoupon());
        	 model.addAttribute("couponApplied", true);
        	
        }

	    model.addAttribute("cartTotal", Math.round(total));

	    List<Address> addresses = addressService.findByUserInfoAndEnabled(userInfo, true);

	    Address defaultAddress = addresses
	            .stream()
	            .filter(Address::isDefaultAddress) // Filter addresses with isDefaultAddress true
	            .findFirst() // Find the first matching address
	            .orElse(null);

	    if (defaultAddress == null) {
	        defaultAddress = addresses.get(0);
	    }

	    if (addressUUID == null) {
	        model.addAttribute("address", defaultAddress);
	    } else {
	        model.addAttribute("address", addressService.findById(addressUUID));
	    }

	    return "shop/cart";
	}
	
	
    @PostMapping("/addcart")
    public String addToCart(@RequestParam("quantity") int quantity,
    		@RequestParam("productUuid") UUID productUuid,Model model) {
        String currentUsername = String.valueOf(getCurrentUsername());
        System.out.println("inside addcart");

        if (quantity < 0 || quantity > 100) {
            return "invalid quantity, accepted values are 0-100";
        }

        if (cartService.addToCart(productUuid,quantity)) {
        	return "redirect:/productDetail?productUuid=" + productUuid;
        } else {
            return "not add";
        }
    }
    
    
//    @RequestParam(name = "amount")Double amount
    
//    @PostMapping("/checkout")
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//    @Transactional
//    public String checkout(@ModelAttribute NewOrderDto newOrderDto,
//    		@RequestParam(name = "addressId") UUID addressId, Model model,
//            @RequestParam(name = "paymentMethod") String paymentMethod) {
//        // Get current user from spring session
//    
//        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
//        
//        
//        List<Cart> cartItems = cartService.findByUser(userInfo);
//
//        if (!cartItems.isEmpty()) {
//        	if(newOrderDto.getPaymentMethod().equals("Online")) {
//        		
//
//            System.out.println("Processing Online order from user:" + userInfo.getUsername());
//            List<OrderItems> orderItemsList = new ArrayList<>();
//
//            OrderHistory orderHistory = new OrderHistory();
//            orderHistory.setOrderStatus(OrderStatus.PROCESSING);
//            orderHistory.setOrderType(OrderType.ONLINE);
//            orderHistory.setUserInfo(userInfo);
//            orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//            orderHistory.setCreatedAt(LocalDateTime.now());
//            
//            orderHistory = orderHistoryRepository.save(orderHistory);
//
//            float gross =0;
//            float total =0;
//            float tax=0;
//            
//            for (Cart item : cartItems) {
//                if (item.getQuantity() != 0) {
//                    OrderItems orderItem = new OrderItems();
//                    orderItem.setQuantity(item.getQuantity());
//                    orderItem.setOrderPrice(item.getProductId().getPrice());
//                    orderItem.setOrderHistory(orderHistory);
//                    orderItem.setProductId(item.getProductId());
//                    orderItemsList.add(orderItem);
//
//                    gross += (item.getProductId().getPrice())*item.getQuantity(); // Calculate the gross amount based on order items
//                    tax += ((item.getProductId().getPrice())*item.getQuantity()) / 100f * 18f; // Calculate tax based on gross amount
//                    total += ((item.getProductId().getPrice())*item.getQuantity()) + tax;
//
//                    
//                    // Update stock quantity
//                    Product product = item.getProductId();
//                    int remainingStock = product.getStock() - item.getQuantity();
//                    product.setStock(remainingStock);
//                    productService.save(product); // Update the product's stock
//                    
//                }
//            }
//
//            // Create Order
//            orderHistory.setTotal(total);
//            orderHistory.setTax(tax);
//            orderHistory.setGross(gross);
//            orderHistory.setItems(orderItemsList);
//
//            orderHistory = orderHistoryService.save(orderHistory);
//
//            for (Cart item : cartItems) {
//                cartService.delete(item);
//                System.out.println("Cart Cleared for User:" + userInfo.getUsername());
//            }
//            
//            TransactionDetails transactionDetails =orderHistoryService.createtransaction(amount);
//            System.out.println(transactionDetails);
//            model.addAttribute("transactionDetails", transactionDetails);
//            model.addAttribute("addressId",addressId);
//            model.addAttribute("amounts",amount);
//
//            return "shop/razorpay";
//        } else {
//        	
//        	 System.out.println("Processing Online order from user:" + userInfo.getUsername());
//             List<OrderItems> orderItemsList = new ArrayList<>();
//
//             OrderHistory orderHistory = new OrderHistory();
//             orderHistory.setOrderStatus(OrderStatus.PROCESSING);
//             orderHistory.setOrderType(OrderType.ONLINE);
//             orderHistory.setUserInfo(userInfo);
//             orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//             orderHistory.setCreatedAt(LocalDateTime.now());
//             
//             orderHistory = orderHistoryRepository.save(orderHistory);
//
//             float gross =0;
//             float total =0;
//             float tax=0;
//             
//             for (Cart item : cartItems) {
//                 if (item.getQuantity() != 0) {
//                     OrderItems orderItem = new OrderItems();
//                     orderItem.setQuantity(item.getQuantity());
//                     orderItem.setOrderPrice(item.getProductId().getPrice());
//                     orderItem.setOrderHistory(orderHistory);
//                     orderItem.setProductId(item.getProductId());
//                     orderItemsList.add(orderItem);
//
//                     gross += (item.getProductId().getPrice())*item.getQuantity(); // Calculate the gross amount based on order items
//                     tax += ((item.getProductId().getPrice())*item.getQuantity()) / 100f * 18f; // Calculate tax based on gross amount
//                     total += ((item.getProductId().getPrice())*item.getQuantity()) + tax;
//
//                     
//                     // Update stock quantity
//                     Product product = item.getProductId();
//                     int remainingStock = product.getStock() - item.getQuantity();
//                     product.setStock(remainingStock);
//                     productService.save(product); // Update the product's stock
//                     
//                 }
//             }
//
//             // Create Order
//             orderHistory.setTotal(total);
//             orderHistory.setTax(tax);
//             orderHistory.setGross(gross);
//             orderHistory.setItems(orderItemsList);
//
//             orderHistory = orderHistoryService.save(orderHistory);
//
//             for (Cart item : cartItems) {
//                 cartService.delete(item);
//                 System.out.println("Cart Cleared for User:" + userInfo.getUsername());
//             }
////             return "redirect:/orderDetails?orderId=" + orderHistory.getUuid() + "&newOrderFlag=true";
//             return "redirect:/orderDetails?orderId=\" + orderHistory.getUuid() + \"&newOrderFlag=true";
//        }
//        
//        	
//        }else {
//            // Fetch order from orderHistory and convert to COD.
//            System.out.println("Converting Order to COD");
//
//            OrderHistory order = orderHistoryService.findById(UUID.fromString(newOrderDto.getGeneratedOrderUuid()));
//
////            order.setOrderType(OrderType.COD);
////            order.setOrderStatus(OrderStatus.PROCESSING);
////
////            System.out.println("Order Converted to COD");
//
//            return "redirect:/orderDetails?orderId=" + order.getUuid() + "&newOrderFlag=true";
//        }
//        	
//        
//    }
    
    
//   order COD--------------------Working 
    
//    @PostMapping("/checkout")
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//    @Transactional
//    public String checkout(@ModelAttribute NewOrderDto newOrderDto,Model model) {
//        // Get current user from spring session
//    
//        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
//        
//        
//        List<Cart> cartItems = cartService.findByUser(userInfo);
//
//        if (!cartItems.isEmpty()) {
//        	
//        	 System.out.println("Processing Online order from user:" + userInfo.getUsername());
//             List<OrderItems> orderItemsList = new ArrayList<>();
//
//             OrderHistory orderHistory = new OrderHistory();
//             orderHistory.setOrderStatus(OrderStatus.PROCESSING);
//             orderHistory.setOrderType(OrderType.COD);
//             orderHistory.setUserInfo(userInfo);
//             orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//             orderHistory.setCreatedAt(LocalDateTime.now());
//             
//             orderHistory = orderHistoryRepository.save(orderHistory);
//
//             float gross =0;
//             float total =0;
//             float tax=0;
//             
//             for (Cart item : cartItems) {
//                 if (item.getQuantity() != 0) {
//                     OrderItems orderItem = new OrderItems();
//                     orderItem.setQuantity(item.getQuantity());
//                     orderItem.setOrderPrice(item.getProductId().getPrice());
//                     orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//                     orderItem.setProductId(item.getProductId());
//                     orderItemsList.add(orderItem);
//
//                     gross += (item.getProductId().getPrice())*item.getQuantity(); // Calculate the gross amount based on order items
//                     tax += ((item.getProductId().getPrice())*item.getQuantity()) / 100f * 18f; // Calculate tax based on gross amount
//                     total += ((item.getProductId().getPrice())*item.getQuantity()) + tax;
//
//                     
//                     // Update stock quantity
//                     Product product = item.getProductId();
//                     int remainingStock = product.getStock() - item.getQuantity();
//                     product.setStock(remainingStock);
//                     productService.save(product); // Update the product's stock
//                     
//                 }
//             }
//
//             // Create Order
//             orderHistory.setTotal(total);
//             orderHistory.setTax(tax);
//             orderHistory.setGross(gross);
//             orderHistory.setItems(orderItemsList);
//
//             orderHistory = orderHistoryService.save(orderHistory);
//
//             for (Cart item : cartItems) {
//                 cartService.delete(item);
//                 System.out.println("Cart Cleared for User:" + userInfo.getUsername());
//             }
////             return "redirect:/orderDetails?orderId=" + orderHistory.getUuid() + "&newOrderFlag=true";
//             return "redirect:/orderDetails?orderId=" + orderHistory.getUuid() + "&newOrderFlag=true";
//        
//        }else {
//            // Fetch order from orderHistory and convert to COD.
//            System.out.println("Converting Order to COD");
//
//            OrderHistory order = orderHistoryService.findById(UUID.fromString(newOrderDto.getGeneratedOrderUuid()));
//
////            order.setOrderType(OrderType.COD);
////            order.setOrderStatus(OrderStatus.PROCESSING);
////
////            System.out.println("Order Converted to COD");
//
//            return "redirect:/orderDetails?orderId=" + order.getUuid() + "&newOrderFlag=true";
//        }
//        	
//        
//    }
    
    
    
    //move items from cart to order
    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Transactional
    public String checkout(@ModelAttribute NewOrderDto newOrderDto, Model model){
        //get current user from spring session
        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());

        List<Cart> cartItems = cartService.findByUser(userInfo);

        if(!cartItems.isEmpty()){
        	if(newOrderDto.getPaymentMethod().equals("Online")) { 
        		 List<OrderItems> orderItemsList = new ArrayList<>();

                 OrderHistory orderHistory = new OrderHistory();
                 orderHistory.setOrderStatus(OrderStatus.PAID);
                 orderHistory.setOrderType(OrderType.ONLINE);
                 orderHistory.setUserInfo(userInfo);
                 orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
                 orderHistory.setCreatedAt(LocalDateTime.now());
                 
                 orderHistory = orderHistoryRepository.save(orderHistory);

                 
                 float gross =0;
                 float total =0;
                 float tax=0;
                 
                 for (Cart item : cartItems) {

                     if (item.getQuantity() != 0) { //if itemQty == 0, after the previous if condition, it means that the product has gone out of stock.
                         OrderItems orderItem = new OrderItems();
                         orderItem.setQuantity(item.getQuantity());
                         orderItem.setOrderPrice(item.getProductId().getPrice());
                         orderItem.setOrderHistory(orderHistory);
                         orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
                         orderItem.setProductId(item.getProductId());
                         orderItemsList.add(orderItem);
                         
                         gross += (item.getProductId().getPrice())*item.getQuantity(); // Calculate the gross amount based on order items
                         tax += ((item.getProductId().getPrice())*item.getQuantity()) / 100f * 18f; // Calculate tax based on gross amount
                         total += ((item.getProductId().getPrice())*item.getQuantity()) + tax;

                         
                         // Update stock quantity
                         Product product = item.getProductId();
                         int remainingStock = product.getStock() - item.getQuantity();
                         product.setStock(remainingStock);
                         productService.save(product); // Update the product's stock
                     }
                 }
                 
//                 CouponValidityResponseDto couponValidityResponseDto = cartService.checkCouponValidity();
//
//                 if (couponValidityResponseDto.isValid()) {
//                     // Assuming coupon discount is provided in percentage
//                     float couponDiscount = gross * (couponValidityResponseDto.getCouponDiscountPercentage() / 100f);
//                     
//                     // Apply coupon discount to gross amount
//                     gross -= couponDiscount;
//                     
//                     // Recalculate tax based on updated gross amount
//                     tax = gross / 100f * 18f;
//                     
//                     // Update total amount after applying coupon discount and tax
//                     total = gross + tax;
//                 
//                 }
//               

//                 CouponValidityResponseDto couponValidityResponseDto = cartService.checkCouponValidity();
//
//                 float gross = (float) couponValidityResponseDto.getCartTotal();
//                 float tax = gross / 100f *18f;
//                 float total = gross - tax;

                 //Create Order

                 orderHistory.setTotal(total);
                 orderHistory.setTax(tax);
                 orderHistory.setGross(gross);
                 orderHistory.setItems(orderItemsList);
                 
                 if(userInfo.getCoupon()!=null) {
                 	couponService.redeem(userInfo.getCoupon().getCode()) ;
                 }

                 orderHistory = orderHistoryService.save(orderHistory);

//                 for (OrderItems item : orderItemsList) {
//                     //reduce stock
//                     Variant variant = variantService.findById(item.getVariant().getUuid());
//                     System.out.println(variant.getProductId().getName()+" "+variant.getName());
//                     variantService.save(variant);
//                 }

                 //Delete items from cart
                 for (Cart item : cartItems) {
                     cartService.delete(item);
                     System.out.println("Cart Cleared for User:" + userInfo.getUsername());
                 }
                 TransactionDetails transactionDetails=orderHistoryService.createtransaction(orderHistory.getTotal());
                 model.addAttribute("transactionDetails",transactionDetails);
                 model.addAttribute("amounts",orderHistory.getTotal());
             	System.out.println(transactionDetails);
                 return "shop/razorpay";
		
        	}else {
            System.out.println("Processing COD order from user:"+userInfo.getUsername());
            List<OrderItems> orderItemsList = new ArrayList<>();

            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrderStatus(OrderStatus.PROCESSING);
            orderHistory.setOrderType(OrderType.COD);
            orderHistory.setUserInfo(userInfo);
            orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
            orderHistory = orderHistoryRepository.save(orderHistory);

            float gross =0;
            float total =0;
            float tax=0;
            
            for (Cart item : cartItems) {

                if (item.getQuantity() != 0) { //if itemQty == 0, after the previous if condition, it means that the product has gone out of stock.
                    OrderItems orderItem = new OrderItems();
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setOrderPrice(item.getProductId().getPrice());
                    orderItem.setOrderHistory(orderHistory);
                    orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
                    orderItem.setProductId(item.getProductId());
                    orderHistory.setCreatedAt(LocalDateTime.now());
                    
                    orderItemsList.add(orderItem);
                    
                    
                    gross += (item.getProductId().getPrice())*item.getQuantity(); // Calculate the gross amount based on order items
                    tax += ((item.getProductId().getPrice())*item.getQuantity()) / 100f * 18f; // Calculate tax based on gross amount
                    total += ((item.getProductId().getPrice())*item.getQuantity()) + tax;

                    
                    // Update stock quantity
                    Product product = item.getProductId();
                    int remainingStock = product.getStock() - item.getQuantity();
                    product.setStock(remainingStock);
                    productService.save(product); // Update the product's stock
                }
            	
//            	if (item.getQuantity() != 0) {
//                    OrderItems orderItem = new OrderItems();
//                    orderItem.setQuantity(item.getQuantity());
//                    orderItem.setOrderPrice(item.getProductId().getPrice());
//                    orderItem.setOrderHistory(orderHistory);
//                    orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//                    orderItem.setProductId(item.getProductId());
//                    orderHistory.setCreatedAt(LocalDateTime.now());
//
//                    orderItemsList.add(orderItem);
//
//                    // Calculate the gross amount based on order items
//                    float itemGross = item.getProductId().getPrice() * item.getQuantity();
//                    gross += itemGross;
//
//                    // Calculate tax for the current item
//                    float itemTax = itemGross / 100f * 18f;
//                    tax += itemTax;
//
//                    // Update stock quantity
//                    Product product = item.getProductId();
//                    int remainingStock = product.getStock() - item.getQuantity();
//                    product.setStock(remainingStock);
//                    productService.save(product); // Update the product's stock
//
//                    // Calculate total for the current item including tax
//                    float itemTotal = itemGross + itemTax;
//                    total += itemTotal;
//                }
            }

//            CouponValidityResponseDto couponValidityResponseDto = cartService.checkCouponValidity();

//            float gross = (float) couponValidityResponseDto.getCartTotal();
//            float tax = gross / 100f *18f;
//            float total = gross - tax;
//
//            //Create Order
//
            orderHistory.setTotal(total);
            orderHistory.setTax(tax);
            orderHistory.setGross(gross);
            orderHistory.setItems(orderItemsList);
            
            if(userInfo.getCoupon()!=null) {
            	couponService.redeem(userInfo.getCoupon().getCode()) ;
            }

            orderHistory = orderHistoryService.save(orderHistory);

//            for (OrderItems item : orderItemsList) {
//                //reduce stock
//                Variant variant = variantService.findById(item.getVariant().getUuid());
//                System.out.println(variant.getProductId().getName()+" "+variant.getName());
//                variantService.save(variant);
//            }

            //Delete items from cart
            for (Cart item : cartItems) {
                cartService.delete(item);
                System.out.println("Cart Cleared for User:" + userInfo.getUsername());
            }
        	
            return "redirect:/orderDetails?orderId=" + orderHistory.getUuid() + "&newOrderFlag=true";
        	}
        	
        }else{
            //fetch order from orderHistory and convert to COD.
            System.out.println("Converting Order to COD");

            OrderHistory order = orderHistoryService.findById(UUID.fromString(newOrderDto.getGeneratedOrderUuid()));

            order.setOrderType(OrderType.COD);

            order.setOrderStatus(OrderStatus.PROCESSING);

            System.out.println("Order Converted to COD");

            return "redirect:/orderDetails?orderId=" + order.getUuid() + "&newOrderFlag=true";

        }
        
    }
    
//    ---------------------------------------------------------------

	private float calculateTotal(List<OrderItems> orderItemsList) {
	    float total = 0.0f;
	    for (OrderItems orderItem : orderItemsList) {
	        total += orderItem.getOrderPrice();
	    }
	    return total;
	}

	private float calculateTax(float total) {
	    // Assuming a flat 10% tax rate
	    float taxRate = 0.10f; // 10%
	    float tax = total * taxRate;
	    return tax;
	}
	
//	delete and update
	
	 @GetMapping("/deletecart/{uuid}")
	    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	    public String delete(@PathVariable UUID uuid){
	        cartService.deleteCartById(uuid);
	        return "redirect:/viewCart";
	    }
	 
	 	@PostMapping("/updatecart")
	    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	    public String updateCart(@RequestParam("cartItemId") List<UUID> cartItemIds,
	                             @RequestParam("qty") List<Integer> quantities) {
	        // Assuming the order of cartItemIds and quantities corresponds to the cart items in the table

	        for (int i = 0; i < cartItemIds.size(); i++) {
	            UUID cartItemId = cartItemIds.get(i);
	            int quantity = quantities.get(i);

	            if (quantity == 0) {
	                cartService.deleteCartById(cartItemId);
	                System.out.println(cartItemId + " deleted");
	            } else {
	                Cart existingCartItem = cartService.findCartByUuid(cartItemId);
	                if (existingCartItem.getQuantity() != quantity) {
	                    System.out.println(cartItemId + " updated");
	                    existingCartItem.setQuantity(quantity);
	                    cartService.save(existingCartItem);
	                }
	            }
	        }

	        return "redirect:/viewCart"; // Redirect back to the same update page after saving changes
	    }
	 	
	 	
//	 	-------------------------
	 	
	 	@GetMapping("changeAddress/{uuid}")
	    public String changeAddress(@PathVariable(name = "uuid") UUID uuid) {
	        return "redirect:/viewCart?addressUUID=" + uuid;
	    }
	 	
	 	
	 	
//	 	@GetMapping("/buyNow")
//	 	public String buyNow(@AuthenticationPrincipal(expression = "username") String username,
//	 	                     @RequestParam(name = "cartitemId") UUID cartUuid, Model model,
//	 	                     @RequestParam(name = "quantity") Integer quantity) {
//	 	    // Retrieve user info
//	 	    UserInfo userInfo = userInfoService.findByUsername(username);
//
//	 	    // Retrieve the current user's username (assuming you have a method for this)
//	 	    String currentUsername = getCurrentUsername();
//	 	    UserInfo userInfo1 = userInfoService.findByUsername(currentUsername);
//	 	    String firstName = userInfo1.getFirstName();
//	 	    model.addAttribute("firstName", firstName);
//
//	 	    // Calculate total (make sure to replace 'productId' with 'cartitemId')
//	 	    Cart cartItem = cartService.findCartByUuid(cartUuid);
//	 	    Product product = cartItem.getProductId();
//	 	    float total = product.getPrice() * quantity;
//
//	 	    // Retrieve cart items
//	 	    List<Cart> cartItems = cartService.findByUser(userInfo);
//
//	 	    model.addAttribute("nameOfUser", userInfo.getFirstName() + " " + userInfo.getLastName());
//
//	 	    if (cartItems.isEmpty()) {
//	 	        model.addAttribute("cartEmpty", true);
//	 	    } else {
//	 	        model.addAttribute("cartEmpty", false);
//	 	    }
//
//	 	    model.addAttribute("loggedIn", true);
//	 	    model.addAttribute("cartItems", cartItems);
//	 	    model.addAttribute("cartTotal", Math.round(total));
//
//	 	    // Retrieve addresses
//	 	    List<Address> addresses = addressService.findByUserInfoAndEnabled(userInfo, true);
//
//	 	    Address defaultAddress = addresses
//	 	            .stream()
//	 	            .filter(Address::isDefaultAddress)
//	 	            .findFirst()
//	 	            .orElse(null);
//
//	 	    if (defaultAddress == null && !addresses.isEmpty()) {
//	 	        defaultAddress = addresses.get(0);
//	 	    }
//
//	 	    model.addAttribute("address", defaultAddress);
//
//	 	    return "shop/SingleOrder?cartitemid" + cartUuid; // Update the template path and query parameter accordingly
//	 	}
	 	
	 	
	 	//To buy individual products
//	    @GetMapping("/buyNow")
//	    public String buyNow(@AuthenticationPrincipal(expression = "username")String username,
//	                         @RequestParam(name = "productId")UUID productId, Model model,
//	                         @RequestParam(name = "quantity")Integer quantity){
//	        model.addAttribute("username", username);
//	        UserInfo user = userInfoService.findByUsername(getCurrentUsername());
////	        User user = adminService.getUsersByUsername(username);
//	        model.addAttribute("savedAddress",user.getSavedAddresses());
//	        model.addAttribute("user", user);
//
//	        Product product = productService.getProduct(productId);
//	        float total = product.getPrice()*quantity;
//	        model.addAttribute("products",product);
//	        model.addAttribute("total",total);
//
//	        List<Cart> cartItems=cartService.getCartItems(username);
//	        List<Cart> filteredCartItemsByProductId = cartItems.stream()
//	                .filter(cart -> cart.getProductId().getUuid() == productId)
//	                .collect(Collectors.toList());
//	        model.addAttribute("username",username);
//	        model.addAttribute("cartItems",filteredCartItemsByProductId);
//
//	        
//
//	        return "user/individualPurchase";
//	    }

//	    Apply-Coupon-------------------------------
	    @PostMapping("/apply-coupon")
	    public String applyCoupon(@RequestParam("couponCode") String coupon,
	    		@RequestParam double cartTotal,Model model) {
	    	
	    	String currentUsername = String.valueOf(getCurrentUsername());
	        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
	        
	   	
	    	if(couponService.findByCode(coupon)!=null&& coupon.equals(couponService.findByCode(coupon).getCode())) {
	    		System.out.println(couponService.findByCode(coupon).getCode());    		
	    		couponService.saveToUser(couponService.findByCode(coupon));
	    		System.out.println("Coupon Valid");
	    			
	    	}else {
	    		
	    		System.out.println("Coupon invalid");
	    	}
	    	return "redirect:/viewCart";
	    } 
	 
	    
//	    @PostMapping("/checkout")
//	    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//	    @Transactional
//	    public String checkout(@ModelAttribute NewOrderDto newOrderDto, Model model){
//	        //get current user from spring session
//	        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
//
//	        List<Cart> cartItems = cartService.findByUser(userInfo);
//
//	        if(!cartItems.isEmpty()){
//	        	if(newOrderDto.getPaymentMethod().equals("Online")) {
//	        		 List<OrderItems> orderItemsList = new ArrayList<>();
//
//	                 OrderHistory orderHistory = new OrderHistory();
//	                 orderHistory.setOrderStatus(OrderStatus.PAID);
//	                 orderHistory.setOrderType(OrderType.ONLINE);
//	                 orderHistory.setUserInfo(userInfo);
//	                 orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//	                 orderHistory = orderHistoryRepository.save(orderHistory);
//
//	                 for (Cart item : cartItems) {
//	     //
////	                     if (item.getQuantity() > item.getVariant().getStock()) {
////	                         item.setQuantity(item.getVariant().getStock());
////	                     }
//
//	                     if (item.getQuantity() != 0) { //if itemQty == 0, after the previous if condition, it means that the product has gone out of stock.
//	                         OrderItems orderItem = new OrderItems();
////	                         orderItem.setVariant(item.getVariant());
//	                         orderItem.setQuantity(item.getQuantity());
//	                         orderItem.setOrderPrice(item.getProductId().getPrice());
//	                         orderItem.setOrderHistory(orderHistory);;
//	                         orderItem.setProductId(item.getProductId());
//	                         orderItemsList.add(orderItem);
//	                     }
//	                 }
//
//	                 CouponValidityResponseDto couponValidityResponseDto = cartService.checkCouponValidity();
//
//	                 float gross = (float) couponValidityResponseDto.getCartTotal();
//	                 float tax = gross / 100f *18f;
//	                 float total = gross - tax;
//
//	                 //Create Order
//
//	                 orderHistory.setTotal(total);
//	                 orderHistory.setTax(tax);
//	                 orderHistory.setGross(gross);
//	                 orderHistory.setItems(orderItemsList);
//	                 
//	                 if(userInfo.getCoupon()!=null) {
//	                 	couponService.redeem(userInfo.getCoupon().getCode()) ;
//	                 }
//
//	                 orderHistory = orderHistoryService.save(orderHistory);
//	                 //Delete items from cart
//	                 for (Cart item : cartItems) {
//	                     cartService.delete(item);
//	                     System.out.println("Cart Cleared for User:" + userInfo.getUsername());
//	                 }
//	             	
//	                 return "redirect:/pay?orderUuid=" + orderHistory.getUuid() + "&newOrderFlag=true";
//			
//	        	}else {
//	            System.out.println("Processing COD order from user:"+userInfo.getUsername());
//	            List<OrderItems> orderItemsList = new ArrayList<>();
//
//	            OrderHistory orderHistory = new OrderHistory();
//	            orderHistory.setOrderStatus(OrderStatus.PROCESSING);
//	            orderHistory.setOrderType(OrderType.COD);
//	            orderHistory.setUserInfo(userInfo);
//	            orderHistory.setUserAddress(addressService.findById(newOrderDto.getAddressId()));
//	            orderHistory = orderHistoryRepository.save(orderHistory);
//
//	            for (Cart item : cartItems) {
//
//	                if (item.getQuantity() != 0) { //if itemQty == 0, after the previous if condition, it means that the product has gone out of stock.
//	                    OrderItems orderItem = new OrderItems();
//	                    orderItem.setQuantity(item.getQuantity());
//	                    orderItem.setOrderPrice(item.getProductId().getPrice());
//	                    orderItem.setOrderHistory(orderHistory);;
//	                    orderItem.setProductId(item.getProductId());
//	                    orderItemsList.add(orderItem);
//	                }
//	            }
//
//	            CouponValidityResponseDto couponValidityResponseDto = cartService.checkCouponValidity();
//
//	            float gross = (float) couponValidityResponseDto.getCartTotal();
//	            float tax = gross / 100f *18f;
//	            float total = gross - tax;
//
//	            //Create Order
//
//	            orderHistory.setTotal(total);
//	            orderHistory.setTax(tax);
//	            orderHistory.setGross(gross);
//	            orderHistory.setItems(orderItemsList);
//	            
//	            if(userInfo.getCoupon()!=null) {
//	            	couponService.redeem(userInfo.getCoupon().getCode()) ;
//	            }
//
//	            orderHistory = orderHistoryService.save(orderHistory);
//
////	            for (OrderItems item : orderItemsList) {
////	                //reduce stock
////	                Variant variant = variantService.findById(item.getVariant().getUuid());
////	                System.out.println(variant.getProductId().getName()+" "+variant.getName());
////	                variantService.save(variant);
////	            }
//
//	            //Delete items from cart
//	            for (Cart item : cartItems) {
//	                cartService.delete(item);
//	                System.out.println("Cart Cleared for User:" + userInfo.getUsername());
//	            }
//	        	
//	            return "redirect:/orderDetails?orderId=" + orderHistory.getUuid() + "&newOrderFlag=true";
//	        	}
//	        	
//	        }else{
//	            //fetch order from orderHistory and convert to COD.
//	            System.out.println("Converting Order to COD");
//
//	            OrderHistory order = orderHistoryService.findById(UUID.fromString(newOrderDto.getGeneratedOrderUuid()));
//
//	            order.setOrderType(OrderType.COD);
//
//	            order.setOrderStatus(OrderStatus.PROCESSING);
//
//	            System.out.println("Order Converted to COD");
//
//	            return "redirect:/orderDetails?orderId=" + order.getUuid() + "&newOrderFlag=true";
//
//	        }
//	        
//	    }
}
