//package ecom.Ecom.controller;
//
//import java.beans.BeanProperty;
//import java.beans.JavaBean;
//import java.util.UUID;
//
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//
//import ecom.Ecom.entity.OrderHistory;
//import ecom.Ecom.service.OrderHistoryService;
//
//@Controller
//public class RazorpayController3{
//	
//	 	@Value("${razorpay.keyId}")
//	    private String razorpayKeyId;
//
//	    @Value("${razorpay.keySecret}")
//	    private String razorpayKeySecret;
//	   
//	    @Autowired
//	    RazorpayClient razorpayClient;
//
//	    @Autowired
//	    OrderHistoryService orderHistoryService;
//
//	    @GetMapping("/pay")
//	    public String initiatePayment(Model model, @RequestParam("orderUuid") UUID orderUuid) {
//	        OrderHistory order = orderHistoryService.findById(orderUuid);
//	        model.addAttribute("order", order);
//
//	        try {
//	            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//	            JSONObject orderRequest = new JSONObject();
//	            orderRequest.put("amount", order.getTotal() * 100); // Amount in paise
//	            orderRequest.put("currency", "INR");
//	            orderRequest.put("receipt", order.getUuid().toString());
//	            orderRequest.put("payment_capture", 1); // Auto capture payment
//
//	            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
//	            model.addAttribute("razorpayOrderId", razorpayOrder.get("id"));
//	            model.addAttribute("razorpayKey", razorpayKeyId);
//
//	        } catch (RazorpayException e) {
//	            // Handle exception
//	            e.printStackTrace();
//	            return "redirect:/order/" + order.getUuid();
//	        }
//
//	        return "shop/razorpay";
//	    }
//
//	
//}
//
