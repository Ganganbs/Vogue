package ecom.Ecom.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.Offer;
import ecom.Ecom.entity.Product;
import ecom.Ecom.repository.OfferRepository;

@Service
public class OfferService {

	@Autowired
    OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public Offer getOfferById(UUID id) {
        return offerRepository.findById(id).orElse(null);
    }

    public void saveOffer(Offer offer) {
        offerRepository.save(offer);
    }

    public void deleteOffer(UUID id) {
        offerRepository.deleteById(id);
    }
    
    public Offer getOfferByProductId(UUID productId) {
        return offerRepository.findByProductId(productId);
    }

    public boolean offerExistsForProduct(UUID productId) {
        return offerRepository.existsByProductId(productId);
    }
    	
}