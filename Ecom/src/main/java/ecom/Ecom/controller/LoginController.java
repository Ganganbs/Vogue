package ecom.Ecom.controller;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecom.Ecom.entity.UserInfo;

import ecom.Ecom.service.UserInfoService;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Product;
import ecom.Ecom.service.CategoryService;
import ecom.Ecom.service.OtpService;
import ecom.Ecom.service.ProductService;
@Controller
public class LoginController {
	
	@Autowired
	UserInfoService userInfoService;

	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	OtpService otpService;
	
	public String getCurrentUsername() {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	@GetMapping("/")
	public String getHomePage(@AuthenticationPrincipal(expression = "username") Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

//        if (isAdmin) {
//            return "redirect:/adminpanel";
//        }
        

	    String currentUsername = String.valueOf(getCurrentUsername());
        UserInfo userInfo = userInfoService.findByUsername(currentUsername);
        
        
        List<Product> products= productService.findAll();
        List<Category> categories=categoryService.findAll();
        
        if (!isVerified(currentUsername)) {
            String phone = userInfo.getPhone();
            model.addAttribute("phone", phone);
        
            //send otp
            otpService.sendPhoneOtp(phone);
        
            return "verification"; 
            
		}else {
			String firstName = userInfo.getFirstName();
			
			System.out.println(firstName+"fristName");
	        model.addAttribute("firstName", firstName);
			model.addAttribute("categories",categories);
			model.addAttribute("products", products);

		return "index";
		}
	}
	
	private boolean isVerified(String username) {
		UserInfo user = userInfoService.findByUsername(username);
		if (user.getUuid() != null) {
			return user.isVerified();
		} else {
			return true;
		}
	}	
	@GetMapping("/login")
    public String showLoginForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {

            model.addAttribute("signedUp", false);

            return "login-page";

        }
          return "redirect:/"; 
    }
	
    @PostMapping("/verify")
    public String verifyUser(@RequestParam(value = "otp") String otp,
                             @RequestParam(value = "phone") String phone,
                             Model model){
    	
    	System.out.println("inside post verify");

       boolean verified =  otpService.verifyPhoneOtp(otp, phone);
       if(verified){
           return "redirect:/";
       }else {
    	   model.addAttribute("failed", true);
           return "verification";
    	   
       }      
    }
    
	private String generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otpNumber = 100_000 + random.nextInt(900_000);
        return String.valueOf(otpNumber);
    }
	
}
