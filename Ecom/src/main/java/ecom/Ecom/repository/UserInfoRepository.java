package ecom.Ecom.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import ecom.Ecom.entity.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {
	
    Optional<UserInfo>findByUsername(String username);

    UserInfo findByEmail(String email);

    UserInfo findByPhone(String phone);

	List<UserInfo> findByUsernameLike(String pattern);

    @Query(value = "SELECT * FROM user_info WHERE username LIKE :keyword% OR phone LIKE :keyword% OR phone LIKE :keyword% OR first_name LIKE :keyword% OR last_name LIKE :keyword% OR uuid like :keyword%", nativeQuery = true)
    Page<UserInfo> search(String keyword, Pageable pageable);
    
    Page<UserInfo> findByUsernameContainingIgnoreCase(String filter, Pageable pageable);
    
    boolean existsByPhoneOrEmailOrUsername(String phone, String email, String username);
    
    
}
