package ecom.Ecom.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ecom.Ecom.entity.Role;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.RoleRepository;
import ecom.Ecom.repository.UserInfoRepository;
import ecom.Ecom.service.UserInfoService;
import ecom.Ecom.service.UserRegistrationService;

@Controller
public class SignUpController {
	
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    UserRegistrationService userRegistrationService;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    UserInfoRepository userInfoRepository;

    public ResponseEntity<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(username);
    }

//    //landing page
//    @GetMapping("/welcome")
//    public String welcome(){
//        return "welcome";
//    }

    @GetMapping("/signup")
    public String signup(Model model){

        model.addAttribute("signupError", "false");

        return "signup";
    }
    
    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute UserInfo userInfo, Model model) {
    	System.out.println("ji");
        Optional<Role> userRoleOptional = roleRepository.findRoleByName("ROLE_USER");
        Role userRole = userRoleOptional.orElseGet(() -> {
            Role newRole = new Role();
            newRole.setRoleName("ROLE_USER");
            return roleRepository.save(newRole);
        });

        userInfo.setRole(userRole);
//        userInfoRepository.save(userInfo);

        String res = userRegistrationService.addUser(userInfo);

        if (res.equals("signupSuccess")) {
            // Redirect to the login page after successful registration
            model.addAttribute("signedUp", true);
            return "login-page";
        }

        switch (res) {
            case "phone":
                model.addAttribute("signupError", "phone");
                break;
            case "email":
                model.addAttribute("signupError", "email");
                break;
            case "username":
                model.addAttribute("signupError", "username");
                break;
            default:
                model.addAttribute("signupError", "");
                break;
        }

        return "signup";
    }

}