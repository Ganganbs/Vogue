package ecom.Ecom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import ecom.Ecom.entity.Address;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.service.AddressService;
import ecom.Ecom.service.UserInfoService;

@Controller
public class ProfileController {
	
	@Autowired
	UserInfoService userInfoService;
	
	@Autowired
	AddressService addressService;
	
	@Autowired
    PasswordEncoder passwordEncoder; 
	
	

	public String getCurrentUsername() {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	@GetMapping("/profile")
    public String viewProfile(@RequestParam(name="addAddress", defaultValue = "false", required = false)boolean addAddress,
                              Model model) {

        String currentUsername = String.valueOf(getCurrentUsername());
        if (currentUsername.equals("anonymousUser")) {
            return "redirect:/login";
        }
        if (currentUsername.equals("anonymousUser")) {
            model.addAttribute("loggedIn", false);
        } else {
            model.addAttribute("loggedIn", true);
        }

        UserInfo userInfo = userInfoService.findByUsername(getCurrentUsername());


        boolean noAddressFound = false;

        if (userInfo.getSavedAddresses().size() == 0 || Objects.equals(userInfo.getSavedAddresses().get(0).getFlat(), null) || Objects.equals(userInfo.getSavedAddresses().get(0).getFlat(), "")) {
            model.addAttribute("setupAddressWarning", true);
            noAddressFound = true;
            //create an empty address if it's null
//            if(userInfo.getSavedAddresses().get(0) == null){
//
//                UserAddress userAddress = new UserAddress();
//                userAddress.setUserInfo(userInfo);
//                userAddress = userAddressService.save(userAddress);
//                List<UserAddress> list = new ArrayList<>();
//                list.add(userAddress);
//                userInfo.setSavedAddresses(list);
//                userInfo = userInfoService.save(userInfo);
//            }
        }

//        model.addAttribute("wishlistCount", userInfoService.findByUsername(currentUsername).getWishlist().size());

        model.addAttribute("user", userInfo);


        List<Address> addresses;
        if (noAddressFound) {
            addresses = new ArrayList<>();
//            addresses.add(new UserAddress());
            System.out.println("No addresses found");
        } else {
            addresses = addressService.findByUser(userInfo);

        }
        model.addAttribute("addresses", addresses);

//        Map<String, Object> userPageValues = currentUserPageValues();


        //show add address error
        model.addAttribute("addAddress",addAddress);
        
        String firstName = userInfo.getFirstName();
        model.addAttribute("firstName", firstName);


        return "shop/profile";
    }
    
	
//	Address---------------------
	
//    @PostMapping("/address/save")
//    public String save(@ModelAttribute Address userAddress) {
//        UserInfo user = userInfoService.findByUsername(getCurrentUsername());
//        userAddress.setUserInfo(user);
//
//        List<Address> userAddressList = addressService.findByUser(user);
//
//        if (userAddressList.isEmpty()) {
//            // Saving first address
//            userAddress.setDefaultAddress(true);
//            addressService.save(userAddress);
//            return "redirect:/profile";
//        }else {
//            if(userAddress.isDefaultAddress()){
//                Address existingDefaultAddress = userAddressList.stream()
//                        .filter(Address::isDefaultAddress)
//                        .findFirst()
//                        .orElse(null);
//
//                if (existingDefaultAddress != null) {
//                    existingDefaultAddress.setDefaultAddress(false);
//                    //remove default from previous default address
//                    addressService.save(existingDefaultAddress);
//                }
//                //save new address(default)
//                addressService.save(userAddress);
//            }
//        }
//
//        return "redirect:/profile";
//    }
    
    
	@PostMapping("/address/save")
	public String save(@ModelAttribute Address userAddress,Model model) {
	    UserInfo user = userInfoService.findByUsername(getCurrentUsername());
	    userAddress.setUserInfo(user);

	    List<Address> userAddressList = addressService.findByUser(user);

	    // Check for duplicate addresses
	    boolean isDuplicate = userAddressList.stream()
	            .anyMatch(address -> addressEquals(address, userAddress));

	    if (!isDuplicate) {
	    	
	        if (userAddress.isDefaultAddress()) {
	            // If the address is set as default, remove default from previous default address
	            Address existingDefaultAddress = userAddressList.stream()
	                    .filter(Address::isDefaultAddress)
	                    .findFirst()
	                    .orElse(null);

	            if (existingDefaultAddress != null) {
	                existingDefaultAddress.setDefaultAddress(false);
	                addressService.save(existingDefaultAddress);
	            }
	        }

	        // Save the new address
	        addressService.save(userAddress);
	    }else {
	        // Set an error message if there is a duplicate address
	    	model.addAttribute("addressExists", true);
	        return "redirect:/profile";
	    }
	    return "redirect:/profile";
	}

	// Helper method to check if two addresses are equal
	private boolean addressEquals(Address address1, Address address2) {
	    return address1.getFlat().equals(address2.getFlat()) &&
	           address1.getArea().equals(address2.getArea()) ;
	}

	
	
	
	
    
    
    @PostMapping("/user/save")
    public String save(@ModelAttribute UserInfo userInfo,
                       BindingResult bindingResult){
            UserInfo user = userInfoService.findByUsername(getCurrentUsername());
            if(userInfo.getUuid().equals(user.getUuid())){
                user.setFirstName(userInfo.getFirstName());
                user.setLastName(userInfo.getLastName());
                user.setPhone(userInfo.getPhone());
                user.setEmail(userInfo.getEmail());
                user.setPassword(userInfo.getPassword());
                //save new and updated addresses
               //check if there is password change and change password if required
            }
        return "redirect:/profile";
    }
    
      
//    @GetMapping("/address/delete/{id}")
//    public String deleteAddress(@PathVariable("id") UUID uuid ) {
//    	addressService.deleteById(uuid);
//    	return "redirect:/profile";
//    }
    
    @GetMapping("/address/delete/{id}")
    public String deleteAddress(@PathVariable("id") UUID uuid) {
        Address address = addressService.findById(uuid);
        if (address != null) {
            address.setDeleted(true);
            addressService.save(address);
        }
        return "redirect:/profile";
    }
}
