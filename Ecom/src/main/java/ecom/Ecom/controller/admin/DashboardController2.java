//package ecom.Ecom.controller.admin;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TimeZone;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import ecom.Ecom.handler.DateFormatter;
//
//import ecom.Ecom.entity.Category;
//import ecom.Ecom.entity.OrderStatus;
//import ecom.Ecom.entity.OrderType;
////import ecom.Ecom.handler.DateFormatter;
//
//import ecom.Ecom.entity.OrderHistory;
//import ecom.Ecom.entity.UserInfo;
//import ecom.Ecom.service.OrderHistoryService;
//import ecom.Ecom.service.UserInfoService;
//
//@Controller
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
//public class DashboardController2 {
//	
//    @Autowired
//    UserInfoService userInfoService;
//    @Autowired
//    OrderHistoryService orderHistoryService;
//    
//    
//    @GetMapping("/dashboard")
//    public String dashboard(Model model,
//                            @RequestParam(required = false) String keyword,
//                            @RequestParam(required = false, defaultValue = "") String filter,
//                            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
//                            @RequestParam(defaultValue = "0") int page,
//                            @RequestParam(defaultValue = "5") int size,
//                            @RequestParam(defaultValue = "createdAt") String field,
//                            @RequestParam(defaultValue = "DESC") String sort) {
//
//        pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort), field));
//
//        Page<OrderHistory> orders;
//
//        switch (filter) {
//            case "PROCESSING":
//                orders = orderHistoryService.findByOrderStatus(OrderStatus.PROCESSING, pageable);
//                break;
//            case "SHIPPED":
//                orders = orderHistoryService.findByOrderStatus(OrderStatus.SHIPPED, pageable);
//                break;
//            case "DELIVERED":
//                orders = orderHistoryService.findByOrderStatus(OrderStatus.DELIVERED, pageable);
//                break;
//            case "CANCELLED":
//                orders = orderHistoryService.findByOrderStatus(OrderStatus.CANCELLED, pageable);
//                break;
//            case "RETURNED":
//                orders = orderHistoryService.findByOrderStatus(OrderStatus.RETURNED, pageable);
//                break;
//            default:
//                if (keyword == null || keyword.equals("")) {
//                    orders = orderHistoryService.findAll(pageable);
//                } else {
//                    orders = orderHistoryService.findByIdLike(keyword, pageable);
//                }
//                break;
//        }
//        
//        double totalRevenue = 0;
//        for (OrderHistory order : orders) {
//            totalRevenue += order.getTotal();
//        }
//
//        // Add total revenue to the model
//        model.addAttribute("totalRevenue", totalRevenue);
//
//        model.addAttribute("orders", orders);
//        // Pagination Values
//        model.addAttribute("filter", filter);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", orders.getTotalPages());
//        model.addAttribute("field", field);
//        model.addAttribute("sort", sort);
//        model.addAttribute("pageSize", size);
//        int startPage = Math.max(0, page - 1);
//        int endPage = Math.min(page + 1, orders.getTotalPages() - 1);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//
//        model.addAttribute("empty", orders.getTotalElements() == 0);
//        return "dashboard";
//    }
//
//    
////    @GetMapping("/dashboard")
////    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////     public String admin(Model model, HttpServletRequest request,
////                         @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
////                         @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
////                         @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws IOException {
////
////         String username = SecurityContextHolder.getContext().getAuthentication().getName();
//////         if (username.equals("anonymousUser")) {
//////             saveAnonUserDetails.save(request.getSession().getId(), request.getRemoteAddr());
//////         }
////
////         String period;
////
////         switch (filter) {
////             case "week" -> {
////                 period="week";
////                 // Get the starting date of the current week
////                 Calendar calendar = Calendar.getInstance();
////                 calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
////                 startDate = calendar.getTime();
////                 // Get today's date
////                 endDate = new Date();
////             }
////             case "month" -> {
////                 period="month";
////                 // Get the starting date of the current month
////                 Calendar calendar = Calendar.getInstance();
////                 calendar.set(Calendar.DAY_OF_MONTH, 1);
////                 startDate = calendar.getTime();
////                 // Get today's date
////                 endDate = new Date();
////             }
////             case "day" -> {
////                 period = "day";
////                 // Get today's date
////                 LocalDate today = LocalDate.now();
////                 // Set the start date to 12:00:00 AM
////                 LocalDateTime startDateTime = today.atStartOfDay();
////                 // Set the end date to 11:59:59 PM
////                 LocalDateTime endDateTime = today.atTime(23, 59, 59);
////
////                 // Convert to Date objects
////                 ZoneId zone = ZoneId.systemDefault();
////                 startDate = Date.from(startDateTime.atZone(zone).toInstant());
////                 endDate = Date.from(endDateTime.atZone(zone).toInstant());
////             }
////
////             case "year" -> {
////                 period="year";
////                 // Get the starting date of the current year
////                 Calendar calendar = Calendar.getInstance();
////                 calendar.set(Calendar.DAY_OF_YEAR, 1);
////                 startDate = calendar.getTime();
////                 // Get today's date
////                 endDate = new Date();
////             }
////             default -> {
////                 // Default case: filter
////                 period="Custom Date Range";
////                 System.out.println("order search");
////                 //for testing
////                 // Get the starting date of the current week
////                 Calendar calendar = Calendar.getInstance();
////                 calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
////                 startDate = calendar.getTime();
////                 // Get today's date
////                 endDate = new Date();
////             }
////         }
////
////         List<OrderHistory> orders = orderHistoryService.findOrdersByDate(startDate, endDate);
////
////         model.addAttribute("numberOfOrders", orders.size());
////
////         long revenue = orders.stream()
////                 .mapToLong(OrderHistory::getGrossLong)
////                 .sum();
////         model.addAttribute("revenue",revenue);
////
////         Float taxSum = orders.stream()
////                 .map(OrderHistory::getTax)
////                 .reduce(0F, Float::sum);
////
////
////         model.addAttribute("taxSum", taxSum);
////
////         AtomicReference<Float> totalProfit = new AtomicReference<>(0F);
////         orders.forEach(order -> {
////             order.getItems().forEach(item -> {
////                 totalProfit.updateAndGet(value -> value + item.getOrderPrice());
////             });
////         });
////
////
////         model.addAttribute("totalProfit", totalProfit);
////
////
////         Map<Category,Integer> catCount = new HashMap<>();
////         orders.forEach(order ->{
////            order.getItems().forEach(item ->{
////                Category category = item.getProductId().getCategory();
////                catCount.put(category, catCount.getOrDefault(category, 0) + 1);
////            });
////         });
////
////         model.addAttribute("categoryCount", catCount);
////
////         Map<LocalDateTime, Float> revenueMap = new HashMap<>();
////
////         orders.forEach(order->{
////             LocalDateTime date = order.getCreatedAt();
////             Calendar calendar = Calendar.getInstance();
////
////             calendar.setTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
////
////          // Set the desired time component
////          calendar.set(Calendar.HOUR_OF_DAY, 0);
////          calendar.set(Calendar.MINUTE, 0);
////          calendar.set(Calendar.SECOND, 0);
////          calendar.set(Calendar.MILLISECOND, 0);
////
////          // Get the modified Date object
////          LocalDateTime modifiedDateTime = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
////
////          order.setCreatedAt(modifiedDateTime);
////          
////
////
//////             revenueMap.put(order.getCreatedAt(), revenueMap.getOrDefault(order.getCreatedAt(), 0F)+order.getGross());
//////          revenueMap.put(order.getCreatedAt(), revenueMap.getOrDefault(order.getCreatedAt(), 0F) + order.getGross());
////          LocalDateTime orderCreatedAt = order.getCreatedAt();
////          Float existingValue = revenueMap.getOrDefault(orderCreatedAt, 0F);
////          Float updatedValue = existingValue + order.getGross();
////          revenueMap.put(orderCreatedAt, updatedValue);
////
////          
////         });
////
////         model.addAttribute("revenueMap",revenueMap);
////
////         Map<OrderStatus, Long> orderStatusCounts = orders.stream()
////                 .collect(Collectors.groupingBy(OrderHistory::getOrderStatus, Collectors.counting()));
////
////         model.addAttribute("orderStatusCounts", orderStatusCounts);
////
////         Map<OrderType, Long> orderTypeCounts = orders.stream()
////                 .collect(Collectors.groupingBy(OrderHistory::getOrderType, Collectors.counting()));
////
////         model.addAttribute("orderTypeCounts", orderTypeCounts);
////
////
//////         long couponsUsed = orders.stream()
//////                 .filter(order -> order.getCoupon() != null)
//////                 .count();
//////
//////         model.addAttribute("couponsUsed", couponsUsed);
////
////
////         int totalItemCount = orders.stream()
////                 .mapToInt(order -> order.getItems().size())
////                 .sum();
////
////
////         model.addAttribute("totalItemCount", totalItemCount);
////
////         //Recent 5 transactions
////         model.addAttribute("lastFiveOrders",orderHistoryService.getLastFiveOrders());
////
////
////
////         model.addAttribute("range", "From "+DateFormatter.format(startDate) + " to " + DateFormatter.format(endDate));
////         model.addAttribute("period", period);
////     //    model.addAttribute("orders", orders);
////         return "dashboard";
////     }
//
//    
//    
//
//	}