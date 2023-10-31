package ecom.Ecom.controller.admin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Product;
import ecom.Ecom.entity.Role;

import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.RoleRepository;
import ecom.Ecom.repository.UserInfoRepository;
import ecom.Ecom.service.UserInfoService;
import ecom.Ecom.service.UserRegistrationService;

@Controller

public class AdminController {
	
	@Autowired
	UserInfoRepository userInfoRepository;
	
	@Autowired
	UserInfoService userInfoService;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRegistrationService userRegistrationService;
	
	
	@GetMapping("/adminpanel")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String showAdminPanel(@RequestParam(name = "page", defaultValue = "0") int page,
	                             @RequestParam(name = "size", defaultValue = "15") int size,
	                             @RequestParam(name = "sortField", defaultValue = "username") String sortField,
	                             @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
	                             @RequestParam(name = "filter", required = false) String filter,
	                             Model model) {

	    // Create a Pageable object for pagination and sorting
	    Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortField);

	    Page<UserInfo> usersPage;

	    if (filter != null && !filter.isEmpty()) {
	        // If a filter is provided, apply it
	        usersPage = userInfoRepository.findByUsernameContainingIgnoreCase(filter, pageable);
	    } else {
	        // Otherwise, retrieve all users
	        usersPage = userInfoRepository.findAll(pageable);
	    }

	    model.addAttribute("users", usersPage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("pageSize", size);
	    model.addAttribute("sortField", sortField);
	    model.addAttribute("sortDir", sortDir);
	    model.addAttribute("filter", filter);

	    return "admin/UsersList";
	}
	

	@PostMapping("/createUser")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	    public String registerUser(@ModelAttribute UserInfo userInfo, Model model) {
		
		// Check if a user with the same phone number, email, or username already exists
	    if (userRegistrationService.userExists(userInfo.getPhone(), userInfo.getEmail(), userInfo.getUsername())) {
	        // Handle the case where a user with the same phone number, email, or username already exists
	        // You can set an appropriate error message or perform any other desired action.
	        model.addAttribute("signupError", "User with the same phone, email, or username already exists.");
	        return "redirect:/createUser";
	    }
	    
	        Optional<Role> userRoleOptional = roleRepository.findRoleByName("ROLE_USER");
	        Role userRole = userRoleOptional.orElseGet(() -> {
	            Role newRole = new Role();
	            newRole.setRoleName("ROLE_USER");
	            return roleRepository.save(newRole);
	        });

	        userInfo.setRole(userRole);
//	        userInfoRepository.save(userInfo);

	        String res = userRegistrationService.addUser(userInfo);

	        if (res.equals("signupSuccess")) {
	            // Redirect to the login page after successful registration
	            model.addAttribute("signedUp", true);
	            return "redirect:/adminpanel";
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

	        return "redirect:/createUser";
	    }

	
//	Create User--------------------------
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/createUser")
    public String createUser(Model model) {
        UserInfo userInfo = new UserInfo();
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("bindingResult", new BeanPropertyBindingResult(userInfo, "userInfo"));
        return "admin/CreateUser";
    }

    @GetMapping("/users/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUserById(@PathVariable("id") UUID uuid){

        UserInfo user = userInfoService.getUser(uuid);
//        user.setDeleted(true);
        user.setEnabled(false);
//        user.setDeletedAt(new Date());
//        user.setDeletedBy(usernameProvider.get());

        System.out.println("Soft deleting user"+user.getUsername());
        userInfoService.save(user);

//        userInfoService.delete(uuid);
        return "redirect:/users";
    }
    
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/deleteUser1")
    public String deleteUser(@ModelAttribute UserInfo userInfo){
        UserInfo user = userInfoService.getUser(userInfo.getUuid());
//        user.setDeleted(true);
        user.setEnabled(false);
//        user.setDeletedAt(new Date());
//        user.setDeletedBy(usernameProvider.get());

        System.out.println("Soft deleting user"+user.getUsername());
        userInfoService.save(user);

//        System.out.println("Deleting"+userInfo.getUuid());
//        userInfoService.delete(userInfo.getUuid());
        return "redirect:/adminpanel";
    }
    
     
    
//    Update User-----------------------
    
    
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute UserInfo updatedUser){
        UserInfo oldUser = userInfoService.getUser((updatedUser.getUuid()));
        //check if there is a change in role
        if (updatedUser.getRole().getRoleName().equals(oldUser.getRole().getRoleName())){
            updatedUser.getRole().setUuid(oldUser.getRole().getUuid());
        }
        else{
            List<Role> allRoles = roleRepository.findAll();
            for(Role role : allRoles){
                if(updatedUser.getRole().getRoleName().equals(role.getRoleName())){
                    updatedUser.getRole().setUuid(role.getUuid());
                }
            }
        }
        if(updatedUser.getPassword().isEmpty()){
            updatedUser.setPassword(oldUser.getPassword());
        }
        else{
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if(!oldUser.isVerified()) {
        	updatedUser.setVerified(false);
        }
        
        userInfoService.updateUser(updatedUser);	

        return "redirect:/adminpanel";

    }
    
    
//    Detailed view--------------------------------------
    
    @GetMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String viewUser(@PathVariable UUID uuid, Model model,
                           @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "createdAt") String field,
                           @RequestParam(defaultValue = "ASC") String sort){

        pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort), field));


        UserInfo user = userInfoService.getUser(uuid);

//        model.addAttribute("deleted",user.isDeleted());
        model.addAttribute("uuid",uuid);
        model.addAttribute("firstName",user.getFirstName());
        model.addAttribute("lastName",user.getLastName());
        model.addAttribute("username",user.getUsername());
        model.addAttribute("email",user.getEmail());
        model.addAttribute("phone",user.getPhone());
        model.addAttribute("role",user.getRole());
        model.addAttribute("enabled",user.isEnabled());

        return "admin/UserDetailView";

    }
    
    
//    Search----------------------------------------------------
    
    @GetMapping("/usersearch")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchUsersByUsername(@RequestParam("searchTerm") String searchTerm, Model model) {
        List<UserInfo> users = searchUsers(searchTerm);
        
        if (users.isEmpty()) {
            model.addAttribute("message", "No users found for the provided search term.");
        } else {
            model.addAttribute("users", users);
        }
        
        return "admin/UsersList";
    }

    public List<UserInfo> searchUsers(String searchTerm) {
        String pattern = searchTerm + "%";
        return userInfoRepository.findByUsernameLike(pattern);
    }
    
    
    
//    @PostMapping("/deleteuser/{id}")
//    public String performHardDelete(@PathVariable("id") UUID id) {
//        // Perform the hard delete operation here using your service
//        userInfoService.delete(id);
//
//        return "redirect:/adminpanel";
//    }
    
    
    
//  Enable/Disable------------------------------------
  
  @GetMapping("/toggleUser/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public String toggleUserById(@PathVariable("id") UUID uuid) {
  	 UserInfo user = userInfoService.getUser(uuid);
      
      // Toggle the deleted status
      user.setEnabled(!user.isEnabled());
      
      // Save the updated category
      userInfoService.save(user);
      
      return "redirect:/adminpanel";
  }
    
    
//    SoftDelete-------------------------------------------------------------
    
    @GetMapping("/usersdelete/{id}")
    public String hardDeleteUser(@PathVariable ("id") UUID uuid, Model model) {
     	UserInfo user = userInfoService.getUser(uuid);
        user.setDeleted(!user.isDeleted());
        model.addAttribute("alertMessage", "User has been permanently deleted.");
        
        return "redirect:/adminpanel";
    }
    

    
   
	
}
