package ecom.Ecom.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.Cart;
import ecom.Ecom.entity.UserInfo;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

	
	List<Cart> findByUserInfo(UserInfo userInfo);

	List<Cart> findByUserInfo_uuid(UUID uuid);

}

