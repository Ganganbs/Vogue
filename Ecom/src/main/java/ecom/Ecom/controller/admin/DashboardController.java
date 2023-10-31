package ecom.Ecom.controller.admin;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.OrderStatus;
import ecom.Ecom.entity.OrderType;
//import ecom.Ecom.handler.DateFormatter;

import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.OrderHistoryRepository;
import ecom.Ecom.service.CategoryService;
import ecom.Ecom.service.OrderHistoryService;
import ecom.Ecom.service.ProductService;
import ecom.Ecom.service.UserInfoService;

@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class DashboardController {
	
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    OrderHistoryService orderHistoryService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserInfoService userintInfoService;
    @Autowired
    ProductService productService;
    
    
    @GetMapping("dashboard")
    public String viewDashboard(Model model){
//        long count = userService.getLoggedInUserCount();
//        model.addAttribute("count",count);

        //To populate the bar graph showing the category wise product sales
        Map<Category, Long> salesCount = (Map<Category, Long>) model.getAttribute("salesCount");
        if (salesCount == null) {
            Map<Category, Long> newSalesCount = orderHistoryService.getOrderCountByCategory();
            model.addAttribute("salesCount", newSalesCount);
        } else {
            model.addAttribute("salesCount", salesCount);
        }

        Map<OrderStatus, Long> salesStatistics = (Map<OrderStatus, Long>) model.getAttribute("salesStatistics");
        if (salesStatistics == null) {
            //To populate the bar graph showing the product sales based on status
            Map<OrderStatus, Long> newSalesStatistics = orderHistoryService.getOrderCountByStatus();
            model.addAttribute("salesStatistics",newSalesStatistics);
        } else {
            model.addAttribute("salesStatistics", salesStatistics);
        }

        Long totalCategory=categoryService.countAllCategory();
        model.addAttribute("totalCategory",totalCategory);

        Long userCount=userInfoService.countAllUsers();
        model.addAttribute("userCount", userCount-1);

        Long orderCount=orderHistoryService.countAllOrders();
        model.addAttribute("orderCount", orderCount);

        Long productCount=productService.countAllProducts();
        model.addAttribute("productCount",productCount);

        return "dashboard/dashboard";
    }

    @PostMapping("getDateSelection")
    public String getDateSelection(@RequestParam(value = "date", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                   @RequestParam(value = "month", required = false) Integer month,
                                   @RequestParam(value = "year", required = false) Integer year,
                                   Model model, RedirectAttributes redirectAttributes){
    	System.out.println(year);
    	System.out.println(month);
    	System.out.println(date);

        if (year==null && month ==null && date!=null){
            //method for daily
            Map<Category, Long> salesCount = orderHistoryService.getOrderCountByCategoryDay(date);
            redirectAttributes.addFlashAttribute("salesCount", salesCount);

            Map<OrderStatus, Long> salesStatistics = orderHistoryService.getOrderCountByStatus(date);
            redirectAttributes.addFlashAttribute("salesStatistics",salesStatistics);
            return "redirect:/dashboard";
        } else if (date ==null && year!=null && month !=null){
            //method for monthly
            Map<Category, Long> salesCount = orderHistoryService.getOrderCountByCategoryMonth(year,month);
            redirectAttributes.addFlashAttribute("salesCount", salesCount);

            Map<OrderStatus, Long> salesStatistics = orderHistoryService.getOrderCountByStatus(year,month);
            redirectAttributes.addFlashAttribute("salesStatistics",salesStatistics);
            return "redirect:/dashboard";
        } else if (date==null && month ==null && year!=null){
            //method for yearly
            Map<Category, Long> salesCount = orderHistoryService.getOrderCountByCategoryYear(year);
            redirectAttributes.addFlashAttribute("salesCount", salesCount);

            Map<OrderStatus, Long> salesStatistics = orderHistoryService.getOrderCountByStatus(year);
            redirectAttributes.addFlashAttribute("salesStatistics",salesStatistics);
            return "redirect:/dashboard";
        }
        Map<Category, Long> salesCount = orderHistoryService.getOrderCountByCategory();
        redirectAttributes.addFlashAttribute("salesCount", salesCount);
        Map<OrderStatus, Long> salesStatistics = orderHistoryService.getOrderCountByStatus();
        redirectAttributes.addFlashAttribute("salesStatistics",salesStatistics);
        return "redirect:/dashboard";
    }
    
//    @GetMapping("get-orders-by-date-range")
//    public String getOrderByRange(@RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
//                                  @PageableDefault(size = 5) Pageable pageable, Model model){
//        Page<Orders> ordersInRange = orderService.getAllOrderItems(
//                PageRequest.of(pageable.getPageNumber(), pageSize));
//        model.addAttribute("orderItems", ordersInRange);
//        return "admin/AdminOrderReport";
//    }
//    @PostMapping("get-orders-by-date-range")
//    public String getOrderByRange(@RequestParam(value = "date", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//                                  @RequestParam(value = "month", required = false) Integer month,
//                                  @RequestParam(value = "year", required = false) Integer year,
//                                  @RequestParam(value = "startDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//                                  @RequestParam(value = "endDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//                                  Model model, Pageable pageable) {
//        System.out.println("first:");
//        System.out.println(date);
//        System.out.println(startDate);
//        System.out.println(endDate);
//        System.out.println(month);
//        System.out.println(year);
//        Page<Orders> ordersInRange = orderService.getRange(year,month,date,startDate,endDate,pageable);
//        model.addAttribute("orderItems", ordersInRange);
//        return "admin/AdminOrderReport";
//    }

//    @GetMapping("/generate-sales-report")
//    public ResponseEntity<byte[]> generateSalesReport(@RequestParam(value = "format", defaultValue = "pdf") String format,
//                                                      @RequestParam(value = "date", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//                                                      @RequestParam(value = "month", required = false) Integer month,
//                                                      @RequestParam(value = "year", required = false) Integer year,
//                                                      @RequestParam(value = "startDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//                                                      @RequestParam(value = "endDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//                                                      Pageable pageable) throws Exception {
//        Page<OrderHistory> ordersInRange = orderHistoryService.getRange(year,month,date,startDate,endDate,pageable);
//        byte[] reportBytes;
//        String contentType;
//        String filename;
//
//        if ("pdf".equals(format)) {
//            reportBytes = orderHistoryService.generateSalesReport(ordersInRange);
//            contentType = MediaType.APPLICATION_PDF_VALUE;
//            filename = "SalesReport.pdf";
//        } else if ("excel".equals(format)) {
//            reportBytes = orderHistoryService.generateExcelReport(ordersInRange.getContent());
//            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
//            filename = "SalesReport.xlsx";
//        } else {
//            throw new IllegalArgumentException("Invalid report format");
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(contentType));
//        headers.setContentDispositionFormData("attachment", filename);
//
//        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
//   }

    //    Invoice Generate----------------
    
    @Autowired
    OrderHistoryRepository orderHistoryRepository;
    @GetMapping("/generate-sales-report")
    public ResponseEntity<byte[]> generateSalesReport(@RequestParam(value = "format", defaultValue = "pdf") String format,
            @RequestParam(value = "date", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "startDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Pageable pageable) throws Exception {
//Page<OrderHistory> ordersInRange = orderHistoryService.getRange(year,month,date,startDate,endDate,pageable);

Page<OrderHistory> ordersInRange = orderHistoryRepository.findAll(pageable);
byte[] reportBytes;
String contentType;
String filename;

if ("pdf".equals(format)) {
reportBytes = orderHistoryService.generateSalesReport(ordersInRange);
contentType = MediaType.APPLICATION_PDF_VALUE;
filename = "SalesReport.pdf";
} else if ("excel".equals(format)) {
reportBytes = orderHistoryService.generateExcelReport(ordersInRange.getContent());
contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
filename = "SalesReport.xlsx";
} else {
throw new IllegalArgumentException("Invalid report format");
}

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.parseMediaType(contentType));
headers.setContentDispositionFormData("attachment", filename);

//return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }
    
	}