package ecom.Ecom.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.Address;
import ecom.Ecom.entity.UserInfo;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserInfo(UserInfo user);

    List<Address> findByUserInfoAndEnabled(UserInfo userInfo, boolean b);


}