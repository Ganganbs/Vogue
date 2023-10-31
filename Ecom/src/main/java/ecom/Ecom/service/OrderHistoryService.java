package ecom.Ecom.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import ecom.Ecom.entity.Address;
import ecom.Ecom.entity.Cart;
import ecom.Ecom.entity.Category;
import ecom.Ecom.entity.Coupon;
import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;
import ecom.Ecom.entity.OrderStatus;
import ecom.Ecom.entity.OrderType;
import ecom.Ecom.entity.TransactionDetails;
import ecom.Ecom.entity.UserInfo;
import ecom.Ecom.repository.OrderHistoryRepository;

@Service
public class OrderHistoryService {

    @Autowired
    OrderHistoryRepository orderHistoryRepository;
    
    private static final String KEY="rzp_test_7B0S7CV55sgTta";
    private static final String KEY_SECRET="8fyG9K0xQ6LToDsTchnzN8D4";
    private static final String CURRENCY="INR";

    public OrderHistory save(OrderHistory orderHistory) {
        return orderHistoryRepository.save(orderHistory);
    }

    public Page<OrderHistory> findAll(Pageable pageable) {
        return orderHistoryRepository.findAll(pageable);
    }

    public OrderHistory findById(UUID uuid) {
        return orderHistoryRepository.findById(uuid).orElse(null);
    }

    public Page<OrderHistory> findByUserInfo(UserInfo userInfo, Pageable pageable) {
        return orderHistoryRepository.findByUserInfo(userInfo, pageable);
    }

    public Page<OrderHistory> findByUserInfoAndDeletedBy(UserInfo userInfo, Boolean b, Pageable pageable) {
        return orderHistoryRepository.findByUserInfoAndDeletedBy(userInfo, b, pageable);
    }

    public List<OrderHistory> findOrdersByDate(Date startDate, Date endDate) {
        return orderHistoryRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public List<OrderHistory> getLastFiveOrders() {
        return orderHistoryRepository.getLastFiveOrders();
    }

    public Page<OrderHistory> searchOrderByKeyword(String keyword, Pageable pageable) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(keyword);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("INVALID UUID");
        }
        return orderHistoryRepository.findByUuidLike(uuid, pageable);
    }

    public Page<OrderHistory> findByOrderType(OrderType orderType, Pageable pageable) {
        return orderHistoryRepository.findByOrderType(orderType, pageable);
    }

    public Page<OrderHistory> findByOrderStatus(OrderStatus orderStatus, Pageable pageable) {
        return orderHistoryRepository.findByOrderStatus(orderStatus, pageable);
    }

    public Page<OrderHistory> findByIdLike(String keyword, Pageable pageable) {
        try {
            UUID uuid = UUID.fromString(keyword);
            return orderHistoryRepository.findByUuidLike(uuid, pageable);
        } catch (IllegalArgumentException e) {
            return Page.empty();
        }
    }

    public List<OrderHistory> findByUserInfo(UserInfo userInfo) {
        return orderHistoryRepository.findByUserInfo(userInfo);
    }
    
    
    
//    RazorPay---------------------------------------------------------------------
    
    public TransactionDetails createtransaction(float amount) {
    	try {
    		
    		JSONObject jsonObject=new JSONObject();
    		jsonObject.put("amount",(amount * 100));
    		jsonObject.put("currency",CURRENCY);
    		
    		
    	RazorpayClient razorpayClient= new RazorpayClient(KEY,KEY_SECRET);
    	
    	Order order=razorpayClient.orders.create(jsonObject);
    	
    	System.out.println(order);
    	
    	TransactionDetails transactionDetails= prepareTransactionDetails(order);
    	
    	return transactionDetails;
    	
    	}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
    }	
    
    private TransactionDetails prepareTransactionDetails(Order order) {
    	
    	String orderId=order.get("id");
    	String currency=order.get("currency");
    	Integer amount=order.get("amount");
    	
    	TransactionDetails transactionDetails=new TransactionDetails(orderId,currency,amount,KEY);
    	return transactionDetails;
    	}

	public void placeOrder(List<Cart> filteredCartItemsByProductId, UserInfo userInfo, String username, OrderType orderType,
			float amount) {
		// TODO Auto-generated method stub
		
	}

    
    
    
    
    
    
    
//    ch3eck the errora-----------------------------
    
//    public void placeOrder(List<Cart> cartItems,  UserInfo userInfo, Address addressId, OrderType orderType, Float amount) {
//        // Assuming you have an OrderHistory constructor or a builder method.
//        OrderHistory orderHistory = new OrderHistory();
//        orderHistory.setUserInfo(userInfo);
//        orderHistory.setUserAddress(addressId);
//        orderHistory.setOrderType(orderType);
//        orderHistory.setTotal(amount);
//
//        // Add other properties to your orderHistory object as needed.
//
//        // Save the order history to the database
//        orderHistoryRepository.save(orderHistory);
//    }
    
	public void placeOrder(List<Cart> cartItems, String username, OrderType orderType, float amount) {
        // Create a new OrderHistory object and set its properties
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderType(orderType);
        orderHistory.setTotal(amount);
        // Set other properties as needed
        
        // Process the cart items and create order items
        List<OrderItems> orderItemsList = new ArrayList<>();
        float total = 0;
        for (Cart cartItem : cartItems) {
            if (cartItem.getQuantity() > 0) {
                OrderItems orderItem = new OrderItems();
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setOrderPrice(cartItem.getProductId().getPrice());
                // Set other order item properties as needed
                
                // Update total and other calculations
                total += (cartItem.getProductId().getPrice() * cartItem.getQuantity());
                
                // Add the order item to the list
                orderItemsList.add(orderItem);
            }
        }
        
        // Set the order items and total to the order history
        orderHistory.setItems(orderItemsList);
        orderHistory.setTotal(total);
        
        // Save the order history to the database using your repository
        orderHistoryRepository.save(orderHistory);
        
        // Perform other necessary actions
        
        
    }
	
	
	
//	invoice----------------------------------------------
	
	 public byte[] generateInvoice(UUID orderId) throws Exception {
	        OrderHistory orders = orderHistoryRepository.findById(orderId).orElseThrow();
	        String invoice = "<html><body>";
	        invoice += "<h2>TechZone Invoice</h2>";

	        invoice += "<p><strong>Customer:</strong> " + orders.getUserInfo().getFirstName() + "</p>";
	        invoice += "<p><strong>Order Date:</strong> " + orders.getCreatedAt() + "</p>";
	        invoice += "<p><strong>Invoice Number:</strong> " + orders.getUuid() + "</p>";

	        // Table for product details
	        invoice += "<table width='100%' border='1' cellpadding='8'>";
	        invoice += "<tr>";
	        invoice += "<th style='background-color: #f2f2f2; text-align: left;'>Product</th>";
	        invoice += "<th style='background-color: #f2f2f2; text-align: left;'>Quantity</th>";
	        invoice += "<th style='background-color: #f2f2f2; text-align: left;'>Payment Mode</th>";
	        invoice += "<th style='background-color: #f2f2f2; text-align: left;'>Price</th>";
	        invoice += "</tr>";
	        for (OrderItems item : orders.getItems()) {
	        invoice += "<tr>";
	        invoice += "<td>" + item.getItemName()+ "</td>";
	        invoice += "<td>" + item.getQuantity() + "</td>";
//	        invoice += "<td>" + orders.getOrderType() + "</td>";
	        invoice += "<td>" + orders.getOrderType().name() + "</td>";
	        invoice += "<td>" + item.getOrderPrice() + "</td>";
	        invoice += "</tr>";
	        }
	        invoice += "<tr>";
	        invoice += "<td colspan='3' style='text-align: right;'><strong>Total:</strong></td>";
	        invoice += "<td>" + orders.getTotal() + "</td>";
	        invoice += "</tr>";
	        
	        invoice += "</table>";
	       

	        // Formatted address
	        String formattedAddress = formatAddress(orders.getUserAddress());
	        invoice += "<p><strong>Address:</strong>" + formattedAddress + "</p>";

	        invoice += "</body></html>";

	        ITextRenderer renderer = new ITextRenderer();
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        renderer.setDocumentFromString(invoice);
	        renderer.layout();
	        renderer.createPDF(outputStream);

	        return outputStream.toByteArray();
	    }


	    private String formatAddress(Address address) {
	        String formattedAddress = "<table>";
	        formattedAddress += "<tr><td>Name:</td><td>" + address.getUserInfo().getFirstName()+ "</td></tr>";
	        formattedAddress += "<tr><td>Phone:</td><td>" + address.getUserInfo().getPhone() + "</td></tr>";
	        formattedAddress += "<tr><td>Flat:</td><td>" + address.getFlat() + "</td></tr>";
	        formattedAddress += "<tr><td>Area:</td><td>" + address.getArea() + "</td></tr>";
	        formattedAddress += "<tr><td>Town:</td><td>" + address.getTown() + "</td></tr>";
	        formattedAddress += "<tr><td>City:</td><td>" + address.getCity() + "</td></tr>";
	        formattedAddress += "<tr><td>State:</td><td>" + address.getState() + "</td></tr>";
	        formattedAddress += "<tr><td>Pin:</td><td>" + address.getPin() + "</td></tr>";
	        formattedAddress += "<tr><td>Landmark:</td><td>" + address.getLandmark() + "</td></tr>";
	        formattedAddress += "</table>";

	        return formattedAddress;
	    }

	    public Map<Category, Long> getOrderCountByCategory() {
	        List<Object[]> results = orderHistoryRepository.countProductsSoldByCategory();

	        Map<Category, Long> countByCategory = new HashMap<>();
	        for (Object[] result : results) {
	            Category category = (Category) result[0];
	            Long count = (Long) result[1];
	            countByCategory.put(category, count);
	        }

	        return countByCategory;
	    }
	    public Map<Category, Long> getOrderCountByCategoryDay(LocalDate date) {
	        LocalDateTime startOfDay = date.atStartOfDay();
	        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay(); // The next day's start time

	        List<Object[]> results = orderHistoryRepository.countProductsSoldByCategoryForDate(startOfDay, endOfDay);

	        Map<Category, Long> countByCategory = new HashMap<>();
	        for (Object[] result : results) {
	            Category category = (Category) result[0];
	            Long count = (Long) result[1];
	            countByCategory.put(category, count);
	        }

	        return countByCategory;
	    }
	    public Map<Category, Long> getOrderCountByCategoryMonth(int year, int month) {
	        List<Object[]> results = orderHistoryRepository.countProductsSoldByCategoryForMonth(year,month);

	        Map<Category, Long> countByCategory = new HashMap<>();
	        for (Object[] result : results) {
	            Category category = (Category) result[0];
	            Long count = (Long) result[1];
	            countByCategory.put(category, count);
	        }

	        return countByCategory;
	    }

	    public Map<Category, Long> getOrderCountByCategoryYear(int year) {
	        List<Object[]> results = orderHistoryRepository.countProductsSoldByCategoryForYear(year);

	        Map<Category, Long> countByCategory = new HashMap<>();
	        for (Object[] result : results) {
	            Category category = (Category) result[0];
	            Long count = (Long) result[1];
	            countByCategory.put(category, count);
	        }

	        return countByCategory;
	    }

	    public List findAllDistinctOrderDates(){
	        return orderHistoryRepository.findAllDistinctOrderDates();
	    }

	    public Map<OrderStatus, Long> getOrderCountByStatus(LocalDate date) {
	        LocalDateTime startOfDay = date.atStartOfDay();
	        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay(); // The next day's start time

	        List<Object[]> results = orderHistoryRepository.countProductsSoldByStatusForDate(startOfDay,endOfDay);

	        Map<OrderStatus, Long> countByStatus = new HashMap<>();
	        for (Object[] result : results) {
	            OrderStatus status = (OrderStatus) result[0];
	            Long count = (Long) result[1];
	            countByStatus.put(status, count);
	        }
	        return countByStatus;
	    }

	    
	    public Map<OrderStatus, Long> getOrderCountByStatus() {
	        List<Object[]> results = orderHistoryRepository.countProductsSoldByStatus();

	        Map<OrderStatus, Long> countByStatus = new HashMap<>();
	        for (Object[] result : results) {
	            OrderStatus status = (OrderStatus) result[0];
	            Long count = (Long) result[1];
	            countByStatus.put(status, count);
	        }
//	        return countByStatus;
	        return countByStatus;
	    }
	    
	    
	    public List<OrderHistory> getOrdersBySelectedDate(LocalDateTime selectedDate) {
	        return orderHistoryRepository.getOrdersBySelectedDate(selectedDate);
	    }

	    public Page<OrderHistory> getOrdersInRange(LocalDate date,
	                                         Pageable pageable) {
	        LocalDateTime startOfDay = date.atStartOfDay();
	        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay(); // The next day's start time
	        return orderHistoryRepository.getOrdersInRange(startOfDay, endOfDay, pageable);
	    }
	    public Page<OrderHistory> getOrdersInRange(LocalDate startDate, LocalDate endDate,
	                                         Pageable pageable) {
	        LocalDateTime startDateTime = startDate.atStartOfDay();
	        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
	        return orderHistoryRepository.getOrdersInRange(startDateTime, endDateTime, pageable);
	    }

	    public Page<OrderHistory> getOrdersInRange(int year, int month,
	                                         Pageable pageable) {
	        return orderHistoryRepository.getOrdersInMonth(year, month, pageable);
	    }
	    public Page<OrderHistory> getOrdersInRange(int year,
	                                         Pageable pageable) {
	        return orderHistoryRepository.getOrdersInYear(year, pageable);
	    }

	    public byte[] generateSalesReport(Page<OrderHistory> ordersPage) throws Exception{
	        List<OrderHistory> orders = ordersPage.getContent();
	        double grandTotal = 0.0;
	        for (OrderHistory order : orders) {
	            grandTotal += order.getTotal();
	        }
//	        + orders.get(0).getCreatedAt().getYear()
	        String sales = "<html><body>";
	        sales += "<h2>Vogue SalesReport</h2>" ;
	        sales += "<h4>Order summary</h4>";
	        sales += "<h5>Total orders</h5>"+orders.size();
	        sales += "<h5>Total sales</h5>"+grandTotal;

	        // Table for product details
	        sales += "<table width='100%' border='1' cellpadding='8'>";
	        sales += "<tr>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>User</th>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Product</th>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Quantity</th>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Payment Mode</th>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Price</th>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Status</th>";
	        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Date</th>";
	        sales += "</tr>";
	        for (OrderHistory order : orders) {
	            sales += "<tr>";
	            sales += "<td>" + order.getUserInfo().getFirstName() + "</td>";
//	            sales += "<td>" + order.getItems().get(0).getItemName()+ "</td>";
//	            sales += "<td>" + order.getItems().get(0).getQuantity()+ "</td>";
	            for (OrderItems item : order.getItems()) {
	                sales += "<td>" + item.getItemName() + "</td>";
	                sales += "<td>" + item.getQuantity() + "</td>";
	            }
	            sales += "<td>" + order.getOrderType() + "</td>";
	            sales += "<td>" + order.getTotal() + "</td>";
	            sales += "<td>" + order.getOrderStatus() + "</td>";
	            sales += "<td>" + order.getCreatedAt() + "</td>";
	            sales += "</tr>";
	        }
	        sales += "</table>";

	        sales += "</body></html>";

	        ITextRenderer renderer = new ITextRenderer();
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        renderer.setDocumentFromString(sales);
	        renderer.layout();
	        renderer.createPDF(outputStream);

	        return outputStream.toByteArray();
	    }

	    public byte[] generateExcelReport(List<OrderHistory> ordersList) throws Exception {
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("SalesReport");

	        // Create headers
	        String[] headers = {"User", "Product", "Quantity", "Price", "Date"};
	        XSSFRow headerRow = sheet.createRow(0);
	        for (int col = 0; col < headers.length; col++) {
	            XSSFCell cell = headerRow.createCell(col);
	            cell.setCellValue(headers[col]);
	        }

	        // Populate data
	        int rowNum = 1;
	        for (OrderHistory order : ordersList) {
	            XSSFRow row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(order.getUserInfo().getFirstName());
	            for (OrderItems item : order.getItems()) {
	                Row row1 = sheet.createRow(rowNum++);
//	            row.createCell(1).setCellValue(order.getItems().get(0).getItemName());
//	            row.createCell(2).setCellValue(order.getItems().get(0).getQuantity());
	                row1.createCell(1).setCellValue(item.getItemName());
	                row1.createCell(2).setCellValue(item.getQuantity());
	            }
	            row.createCell(3).setCellValue(order.getTotal());
	            row.createCell(4).setCellValue(order.getCreatedAt().toString());
	        }

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        workbook.close();

	        return outputStream.toByteArray();
	    }

//	
	    
	    public Page<OrderHistory> getRange(Integer year, Integer month, LocalDate date, LocalDate startDate
	            , LocalDate endDate,Pageable pageable) {
	    		
	    	return getOrdersInRange(0, pageable);
}

	    public Long countAllOrders() {
	        return orderHistoryRepository.count();
	    }
	    
	    public Map<OrderStatus, Long> getOrderCountByStatus(int year, int month) {
	        List<Object[]> results = orderHistoryRepository.countProductsSoldByStatusForMonth(year,month);

	        Map<OrderStatus, Long> countByStatus = new HashMap<>();
	        for (Object[] result : results) {
	            OrderStatus status = (OrderStatus) result[0];
	            Long count = (Long) result[1];
	            countByStatus.put(status, count);
	        }
	        return countByStatus;
	    }
	    
	    public Map<OrderStatus, Long> getOrderCountByStatus(int year) {
	        List<Object[]> results = orderHistoryRepository.countProductsSoldByStatusForYear(year);

	        Map<OrderStatus, Long> countByStatus = new HashMap<>();
	        for (Object[] result : results) {
	            OrderStatus status = (OrderStatus) result[0];
	            Long count = (Long) result[1];
	            countByStatus.put(status, count);
	        }
	        return countByStatus;
	    }


	    public Page<OrderHistory> findByCoupon(Coupon coupon, Pageable pageable) {
	        return orderHistoryRepository.findByCoupon(coupon, pageable );
	    }
}


