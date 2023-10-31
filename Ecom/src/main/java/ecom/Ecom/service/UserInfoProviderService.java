package ecom.Ecom.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import  ecom.Ecom.security.UserInfoToUserDetailsConversion;
import  ecom.Ecom.entity.UserInfo;
import  ecom.Ecom.repository.UserInfoRepository;

@Service
public class UserInfoProviderService implements UserDetailsService {
	
   @Autowired
   UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
      Optional<UserInfo> userInfo =   userInfoRepository.findByUsername(username);
          return userInfo.map(UserInfoToUserDetailsConversion::new)
                  .orElseThrow(()-> new UsernameNotFoundException("user not found:"+username));

    }
}

