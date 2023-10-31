package ecom.Ecom.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.Otp;
import ecom.Ecom.entity.UserInfo;

@Repository

public interface OtpRepository extends JpaRepository<Otp, UUID> {
	
	Otp findByUserInfo(UserInfo user);
	
}

