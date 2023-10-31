package ecom.Ecom.controller.admin;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecom.Ecom.dto.OrderDto;
import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;
import ecom.Ecom.entity.OrderStatus;
import ecom.Ecom.entity.OrderType;
import ecom.Ecom.service.OrderHistoryService;

@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class OrderHistoryController {
	
    @Autowired
    OrderHistoryService orderHistoryService;

    @GetMapping("/order")
    public String all(Model model,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false, defaultValue = "") String filter,
                      @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "5") int size,
                      @RequestParam(defaultValue = "createdAt") String field,
                      @RequestParam(defaultValue = "DESC") String sort){

        pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort), field));

        Page<OrderHistory> orders;


            switch (filter){
                case "ONLINE" -> orders = orderHistoryService.findByOrderType(OrderType.ONLINE, pageable);
                case "COD" -> orders = orderHistoryService.findByOrderType( OrderType.COD, pageable);
                case "PROCESSING" -> orders = orderHistoryService.findByOrderStatus( OrderStatus.PROCESSING, pageable);
                case "SHIPPED" -> orders = orderHistoryService.findByOrderStatus(OrderStatus.SHIPPED, pageable);
                case "DELIVERED" -> orders = orderHistoryService.findByOrderStatus(OrderStatus.DELIVERED, pageable);
                case "CANCELLED" -> orders = orderHistoryService.findByOrderStatus(OrderStatus.CANCELLED, pageable);
                case "RETURNED" -> orders = orderHistoryService.findByOrderStatus(OrderStatus.RETURNED, pageable);
                case "REFUNDED" -> orders = orderHistoryService.findByOrderStatus(OrderStatus.REFUNDED, pageable);
                case "PAYMENT_PENDING" -> orders = orderHistoryService.findByOrderStatus(OrderStatus.PAYMENT_PENDING, pageable);
                default -> {
                    if (keyword == null || keyword.equals("")) {
                        orders = orderHistoryService.findAll(pageable);
                    } else {
                        orders = orderHistoryService.findByIdLike(keyword, pageable);
                    }
                }
            }

        model.addAttribute("orders", orders);
        //Pagination Values
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("field", field);
        model.addAttribute("sort", sort);
        model.addAttribute("pageSize", size);
        int startPage = Math.max(0, page - 1);
        int endPage = Math.min(page + 1, orders.getTotalPages() - 1);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("empty", orders.getTotalElements() == 0);
        return "order/OrderList";
    }

    @GetMapping("/{uuid}")
    public String view(@PathVariable UUID uuid, Model model){
        OrderHistory order = orderHistoryService.findById(uuid);
        model.addAttribute("order", order);
//        model.addAttribute("orderitems", OrderItems);

        model.addAttribute("statusList", OrderStatus.values());

        model.addAttribute("paymentFailed", order.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING));
        model.addAttribute("online", order.getOrderType().equals(OrderType.ONLINE));

        return "order/OrderDetailView";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam UUID uuid){
	    OrderHistory order = orderHistoryService.findById(uuid);
	    order.setDeleted(true);
	    orderHistoryService.save(order);
	    return "redirect:/order";
    }
        
   // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PostMapping("/updateorder")
//    public String update(OrderDto orderDto){
//
////    	System.out.println(orderDto);
////    	System.out.println(orderDto.getUuid());
////        OrderHistory order = orderHistoryService.findById(orderDto.getUuid());
////        order.setOrderStatus(orderDto.getOrderStatus());
////
////        orderHistoryService.save(order);
//    	return "redirect:/order";
////        return "redirect:/order/" + order.getUuid();
//    }
//    @PostMapping("/updateorder")
//    public String update(@ModelAttribute OrderDto orderDto) {
//        OrderHistory order = orderHistoryService.findById(orderDto.getUuid());
//        order.setOrderStatus(orderDto.getOrderStatus());
//        orderHistoryService.save(order);
//
//        return "redirect:/order";
//    }
// 
    
//    @PostMapping("/updateorder")
//    public String updateOrder(@ModelAttribute OrderHistory order) {
//        OrderHistory existingOrder = orderHistoryService.findById(order.getUuid());
//        existingOrder.setOrderStatus(order.getOrderStatus());
//        orderHistoryService.save(existingOrder);
//        return "redirect:/order";
//    }
    
    @PostMapping("/updateorder")
    public String updateOrder(
        @RequestParam("uuid") UUID uuid,
        @RequestParam ("orderStatus")OrderStatus orderStatus)
    {
        OrderHistory existingOrder = orderHistoryService.findById(uuid);
        existingOrder.setOrderStatus(orderStatus);
        orderHistoryService.save(existingOrder);
        return "redirect:/order";
    }
    
    

    
 

}
