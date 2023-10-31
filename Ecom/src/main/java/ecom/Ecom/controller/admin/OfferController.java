package ecom.Ecom.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Offer;
import ecom.Ecom.entity.Product;
import ecom.Ecom.repository.OfferRepository;
import ecom.Ecom.service.OfferService;
import ecom.Ecom.service.ProductService;

@Controller
public class OfferController {
	
	@Autowired
	OfferRepository offerRepository;

	@Autowired
	OfferService offerService;
	
	@Autowired
	ProductService productService;
	
	@GetMapping("/offer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String all(Model model){
        List<Offer> offer = offerRepository.findAll();
        model.addAttribute("offer",offer);
        return "offer/OfferList";
    }
	
	@GetMapping("/createoffer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createCategory(Model model){
		 List<Product> product = productService.findAll();
		 model.addAttribute("product", product);
		 
        return "offer/CreateOffer";
    }
	
	public boolean productHasOffer(UUID productId) {
	    // Implement logic to check if the product has an offer
	    // You can use your service and repository methods here
	    return offerService.offerExistsForProduct(productId); // Assuming you have this method
	}

	
}
