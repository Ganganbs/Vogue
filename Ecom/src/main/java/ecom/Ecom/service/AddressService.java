package ecom.Ecom.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.Address;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.AddressRepository;
import ecom.Ecom.service.AddressService;

@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    
    public void deleteByUser() {

        //userAddressRepository.deleteByUser
    }

    
    public List<Address> findByUser(UserInfo user) {
        return addressRepository.findByUserInfo(user);
    }

    
    public void deleteById(UUID uuid) {
        addressRepository.deleteById(uuid);
    }

    
    public Address findById(UUID addressUUID) {
        return addressRepository.findById(addressUUID).orElse(null);
    }

    
    public void disableAddress(UUID uuid) {
        Address userAddress = findById(uuid);
        userAddress.setEnabled(false);
        this.save(userAddress);
    }

    
    public List<Address> findByUserInfoAndEnabled(UserInfo userInfo, boolean b) {
        return addressRepository.findByUserInfoAndEnabled(userInfo, b);
    }
}
