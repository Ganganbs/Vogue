package ecom.Ecom.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.Product;
import ecom.Ecom.dto.CouponValidityResponseDto;
import ecom.Ecom.entity.Cart;
import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Coupon;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.CartRepository;
import ecom.Ecom.service.CategoryService;
import ecom.Ecom.service.ProductService;
import ecom.Ecom.service.UserInfoService;

@Service
public class CartService {


    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;

    public List < Cart > findByUser(UserInfo userInfo) {
        return cartRepository.findByUserInfo(userInfo);
    }

    public Cart save(Cart newCartItem) {
        return cartRepository.save(newCartItem);
    }

    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }

    public void deleteCartById(UUID uuid) {
        cartRepository.deleteById(uuid);
    }

    public Cart findCartByUuid(UUID uuid) {
        return cartRepository.findById(uuid).orElse(null);
    }
    
//    public List<Cart> getCartItems(String username) {
//        UserInfo user = userInfoService.findByUsername(username);
//        return cartRepository.findByUserInfo(user);
//    }
    
    
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    
    public boolean addToCart(UUID productId, int qty) {
        boolean addedToCart = false;

        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
        Product product = productService.getProduct(productId);

        // Fetch existing cart if exists or create a new cart for the user
        List<Cart> cart = findByUser(userInfo);

        Cart newCartItem = new Cart();

        if (cart.size() == 0) {
            newCartItem.setProductId(product);
            newCartItem.setQuantity(Math.min(qty, 5));
            newCartItem.setUserInfo(userInfo);
            newCartItem = save(newCartItem);
            addedToCart = true;
        } else {
            // Check if the item already exists in the cart
            for (Cart existingCartItem : cart) {
                if (existingCartItem.getProductId().equals(product)) {
                    int updatedQty = existingCartItem.getQuantity() + qty;
                    existingCartItem.setQuantity(Math.min(updatedQty, 5));
                    existingCartItem = save(existingCartItem);
                    addedToCart = true;
                    break;
                }
            }

            // If the item does not exist in the cart, add a new cart item
            if (!addedToCart) {
                newCartItem.setProductId(product);
                newCartItem.setQuantity(Math.min(qty, 5));
                newCartItem.setUserInfo(userInfo);
                newCartItem = save(newCartItem);
                addedToCart = true;
            }
        }
        return addedToCart;
    }

    public CouponValidityResponseDto checkCouponValidity() {

        CouponValidityResponseDto couponValidityResponseDto = new CouponValidityResponseDto();
        
        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());
        Coupon coupon = userInfo.getCoupon();


        //find cart total
        double cartTotal = userInfo.getCartItems().stream()
                .mapToDouble(cartItem -> cartItem.getProductId().getPrice() * cartItem.getQuantity())
                .sum();
        //mrp
        double cartMrp = userInfo.getCartItems().stream()
                .mapToDouble(cartItem -> cartItem.getProductId().getPrice() * cartItem.getQuantity())
                .sum();

        couponValidityResponseDto.setCartTotal(cartTotal);

        if(coupon == null){
            couponValidityResponseDto.setValid(false);
            System.out.println("insided1 ");

            return couponValidityResponseDto;
        }

        if (!coupon.isExpired() && coupon.isEnabled()) {
        	System.out.println("insided ");

            switch (coupon.getCouponType()) {
                case 1 -> {
                    System.out.println("Product Coupon");
                    boolean productFoundInCart = false;
                    Float productPrice = 0F;
                    Product couponProduct = productService.getProduct(coupon.getApplicableFor());
                    if (couponProduct == null) {
                        System.out.println("Product in coupon not found");
                    } else {
                        for (Cart item : userInfo.getCartItems()) {
                            if (item.getProductId().equals(couponProduct)) {
                                System.out.println("Product in coupon found in cart");
                                productFoundInCart = true;
                                productPrice = item.getProductId().getPrice();
                                break;
                            }
                        }
                        if (productFoundInCart) {
                            float priceOff = productPrice / 100 * coupon.getOffPercentage();

                            priceOff = priceOff > coupon.getMaxOff() ? coupon.getMaxOff() : priceOff;

                            couponValidityResponseDto.setValid(true);
                            couponValidityResponseDto.setPriceOff(Math.round(priceOff));
                            couponValidityResponseDto.setCoupon(coupon.getCode());
                            couponValidityResponseDto.setCartTotal(Math.round(cartTotal - priceOff));

                            return couponValidityResponseDto;
                        }
                    }
                }
                case 2 -> {
                    System.out.println("Category Coupon");
                    boolean categoryFound = false;
                    float offerPrice = 0F;
                    Category category = categoryService.getCategory(coupon.getApplicableFor());
                    if (category == null) {
                        System.out.println("Category Not Found");
                    } else {
                        for (Cart item : userInfo.getCartItems()) {
                            if (item.getProductId().getCategory().equals(category)) {
                                System.out.println("Category in coupon found in cart item");
                                categoryFound = true;
                                offerPrice += item.getProductId().getPrice();
                            }
                        }
                        if (categoryFound) {
                            float priceOff = offerPrice / 100 * coupon.getOffPercentage();
                            priceOff = priceOff > coupon.getMaxOff() ? coupon.getMaxOff() : priceOff;

                            couponValidityResponseDto.setValid(true);
                            couponValidityResponseDto.setPriceOff(Math.round(priceOff));
                            couponValidityResponseDto.setCoupon(coupon.getCode());
                            couponValidityResponseDto.setCartTotal(cartTotal - priceOff);


                            return couponValidityResponseDto;

                        } else {
                            System.out.println("No items in cart belong to the coupon category");
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Brand Coupon not enabled");
                    couponValidityResponseDto.setValid(false);
                    return couponValidityResponseDto;
                }
                case 4 -> {
                    System.out.println("General Coupon");
                    float priceOff = (float) (cartTotal / 100F * coupon.getOffPercentage());
                    priceOff = priceOff > coupon.getMaxOff() ? coupon.getMaxOff() : priceOff;

                    couponValidityResponseDto.setValid(true);
                    couponValidityResponseDto.setPriceOff(Math.round(priceOff));
                    couponValidityResponseDto.setCoupon(coupon.getCode());
                    couponValidityResponseDto.setCartTotal(Math.round(cartTotal - priceOff));

                    return couponValidityResponseDto;
                }
                case 5 -> {
                    System.out.println("User Coupon not enabled");
                    couponValidityResponseDto.setValid(false);
                    return couponValidityResponseDto;
                }
                default -> {
                    System.out.println("Unknown Coupon Type");
                    couponValidityResponseDto.setValid(false);
                    return couponValidityResponseDto;
                }
            }
        }
        System.out.println("Coupon is not valid");
        couponValidityResponseDto.setValid(false);
        return couponValidityResponseDto;
    }
}
