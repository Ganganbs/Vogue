package ecom.Ecom.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.Otp;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.OtpRepository;
import ecom.Ecom.service.UserInfoService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

@Service
public class OtpService{
	
@Autowired
OtpRepository otpRepository;

@Autowired
UserInfoService userInfoService;

public void save(Otp otp) {
	otpRepository.save(otp);
}


public Otp findByUser(UserInfo user) {
	return otpRepository.findByUserInfo(user);
}


public void delete(Otp oldOtp) {
	otpRepository.delete(oldOtp);
	
}


public boolean verifyPhoneOtp(String otp, String phone) {
	UserInfo user =userInfoService.findByPhone(phone);
	System.out.println("inside verify");
	if(user.isVerified()) {
		return true;
	}
	Otp savedOtp=this.findByUser(user);
	if(savedOtp.isotpRequired()) {
		if(otp.equals(savedOtp.getOtp())) {
			user.setVerified(true);
			userInfoService.save(user);
			this.delete(savedOtp);
			return true;
		}
	}
	return false;
}

public void sendPhoneOtp(String phoneNumber){

    UserInfo user = userInfoService.findByPhone(phoneNumber);
    Otp savedOtp= this.findByUser(user);

    if(savedOtp != null){
        if (savedOtp.isotpRequired()) {
            System.out.println("valid otp already exists");
            return;
        }else{
            System.out.println("Deleting expired otp");
            otpRepository.delete(savedOtp);
        }
    }

        String otp = generateOTP();
        Otp generatedOtp = new Otp(otp);
        generatedOtp.setUserInfo(user);
        generatedOtp = otpRepository.save(generatedOtp);
        System.out.println("new otp generated");

     final String ACCOUNT_SID = "AC0e2a3e63f59d17660b19f37fca3f553a";
     final String AUTH_TOKEN = "85a3bb7820612aef59581db4fe0e4896";

    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    PhoneNumber from = new PhoneNumber("+12312416118");
    PhoneNumber to = new PhoneNumber("+917012765056");


    String content = "This is your account verification OTP. Valid for 5 minutes. OTP: " + generatedOtp.getOtp();
    MessageCreator messageCreator = new MessageCreator(to, from,  content);
    Message res = messageCreator.create();
    System.out.println(res.getSid());
}

private String generateOTP() {
    // Generate a random 6-digit OTP
    Random random = new Random();
    int otpNumber = 100_000 + random.nextInt(900_000);
    return String.valueOf(otpNumber);
}


}
