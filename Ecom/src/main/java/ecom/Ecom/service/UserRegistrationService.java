package ecom.Ecom.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.UserInfoRepository;

@Service
public class UserRegistrationService {

	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public String addUser(UserInfo userInfo) {
		
		if(userInfo.getUsername().equals("anonymousUser")){
			return "username";
		}
		
		Optional<UserInfo> existingUser1=userInfoRepository.findByUsername(userInfo.getUsername());
		
		UserInfo existingUser2=userInfoRepository.findByEmail(userInfo.getEmail());
		UserInfo existingUser3=userInfoRepository.findByPhone(userInfo.getPhone());

		if(existingUser1.isPresent()) {
			return "username";
		}
		if(existingUser2!=null) {
			return "email";
		}	
		if(existingUser3!=null) {
			return "phone";
		}
		
	    userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
	    
	    userInfo.setVerified(false);
		
		userInfo=userInfoRepository.save(userInfo);
		
		return "signupSuccess";
				
	}

	public boolean userExists(String phone, String email, String username) {
        // Check if a user with the same phone number, email, or username already exists
        return userInfoRepository.existsByPhoneOrEmailOrUsername(phone, email, username);
    }
}
