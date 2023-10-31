package ecom.Ecom.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.Coupon;
import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderStatus;
import ecom.Ecom.entity.OrderType;
import ecom.Ecom.entity.UserInfo;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {
    List<OrderHistory> findByUserInfo(UserInfo userInfo);
    Page<OrderHistory> findByUserInfo(UserInfo userInfo, Pageable pageable);
    
    List<OrderHistory> findByCreatedAtBetween(Date startDate, Date endDate);

    @Query(value = "SELECT * FROM order_history ORDER BY created_at DESC LIMIT 5", nativeQuery = true)
    List<OrderHistory> getLastFiveOrders();

    //TODO; not working
    @Query(value = "select order_history.* from order_history" +
            " inner join user_info on order_history.user_id = user_info.uuid" +
            " where user_info.username like :keyword% or order_history.uuid like keyword%", nativeQuery = true)
    Page<OrderHistory> findOrderByKeyword(String keyword, Pageable pageable);

    Page<OrderHistory> findByUuidLike(UUID uuid, Pageable pageable);

    @Query(value = "SELECT * FROM order_history WHERE :filter = :val",
            countQuery = "SELECT COUNT(*) FROM order_history WHERE :filter = :val",
            nativeQuery = true)
    Page<OrderHistory> filter(@Param("filter") String filter, @Param("val") String val, Pageable pageable);
    
    Page<OrderHistory> findByOrderType(OrderType type, Pageable pageable);

    Page<OrderHistory> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    Page<OrderHistory> findByUserInfoAndDeletedBy(UserInfo userInfo, Boolean b, Pageable pageable);
    
    List<OrderHistory> findAllByOrderByCreatedAtDesc();
    
//    @Query("SELECT p.category, COUNT(p) FROM Orders o JOIN o.product p GROUP BY p.category")
//	List<Object[]> countProductsSoldByCategory();
//	
//	@Query("SELECT p.category, COUNT(p) FROM Orders o JOIN o.product p WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay GROUP BY p.category")
//	List<Object[]> countProductsSoldByCategoryForDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
//	
//	@Query("SELECT p.category, COUNT(p) FROM Orders o JOIN o.product p WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month GROUP BY p.category")
//	List<Object[]> countProductsSoldByCategoryForMonth(int year, int month);
//	
//	@Query("SELECT p.category, COUNT(p) FROM Orders o JOIN o.product p WHERE YEAR(o.orderDate) =" +" :year GROUP BY p.category")
//	List<Object[]> countProductsSoldByCategoryForYear(int year);
//	
//	@Query("SELECT DISTINCT DATE(o.orderDate) FROM Orders o")
//	List findAllDistinctOrderDates();
//	
//	@Query("SELECT o.status, COUNT(o) FROM Orders o WHERE o.orderDate >= :startOfDay AND o.orderDate <= :endOfDay GROUP BY o.status")
//	List<Object[]> countProductsSoldByStatusForDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
//	
//	
//	List<OrderHistory> getOrdersBySelectedDate(LocalDateTime createdAt);
//	Page<OrderHistory> getOrdersInRange(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
//	
//	@Query("SELECT o.status, COUNT(o) FROM Orders o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month GROUP BY o.status")
//	Page<OrderHistory> getOrdersInMonth(int year, int month, Pageable pageable);
//	Page<OrderHistory> getOrdersInYear(int year, Pageable pageable);
//	
//	@Query("SELECT o.status, COUNT(o) FROM Orders o GROUP BY o.status")
//	List<Object[]> countProductsSoldByStatus();
	
	
	
	
//	 @Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.product p GROUP BY p.category")
//	    List<Object[]> countProductsSoldByCategory();

//	    @Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.product p WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay GROUP BY p.category")
//	    List<Object[]> countProductsSoldByCategoryForDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

//	    @Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.product p WHERE YEAR(o.createdAt) =" + " :year GROUP BY p.category")
//	    List<Object[]> countProductsSoldByCategoryForYear(@Param("year") int year);

//	    @Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.product p WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month GROUP BY p.category")
//	    List<Object[]> countProductsSoldByCategoryForMonth(@Param("year") int year, @Param("month") int month);

	    @Query("SELECT DISTINCT DATE(o.createdAt) FROM OrderHistory o")
	    List<LocalDate> findAllDistinctOrderDates();

	    @Query("SELECT o.orderStatus, COUNT(o) FROM OrderHistory o GROUP BY o.orderStatus")
	    List<Object[]> countProductsSoldByStatus();

	    @Query("SELECT o.orderStatus, COUNT(o) FROM OrderHistory o WHERE o.createdAt >= :startOfDay AND o.createdAt <= :endOfDay GROUP BY o.orderStatus")
	    List<Object[]> countProductsSoldByStatusForDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

	    @Query("SELECT o.orderStatus, COUNT(o) FROM OrderHistory o WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month GROUP BY o.orderStatus")
	    List<Object[]> countProductsSoldByStatusForMonth(@Param("year") int year, @Param("month") int month);

	    @Query("SELECT o.orderStatus, COUNT(o) FROM OrderHistory o WHERE YEAR(o.createdAt) = :year GROUP BY o.orderStatus")
	    List<Object[]> countProductsSoldByStatusForYear(@Param("year") int year);

	    @Query("SELECT o FROM OrderHistory o WHERE DATE(o.createdAt) = :selectedDate")
	    List<OrderHistory> getOrdersBySelectedDate(@Param("selectedDate") LocalDateTime selectedDate);

	    @Query("SELECT o FROM OrderHistory o WHERE o.createdAt BETWEEN :startDate AND :endDate")
	    Page<OrderHistory> getOrdersInRange(@Param("startDate") LocalDateTime startDate,
	                               @Param("endDate") LocalDateTime endDate, Pageable pageable);
	    
	    
	    @Query("SELECT o FROM OrderHistory o WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month")
	    Page<OrderHistory> getOrdersInMonth(@Param("year") int year, @Param("month") int month, Pageable pageable);

	    @Query("SELECT o FROM OrderHistory o WHERE YEAR(o.createdAt) = :year")
	    Page<OrderHistory> getOrdersInYear(@Param("year") int year, Pageable pageable);

	    
	    
	    
	    @Query("SELECT oi.productId.category, COUNT(oi) FROM OrderItems oi JOIN oi.orderHistory o GROUP BY oi.productId.category")
	    List<Object[]> countProductsSoldByCategory();

//	    @Query("SELECT p.category, COUNT(p), SUM(oi.quantity) FROM OrderHistory o JOIN o.items oi JOIN oi.productId p WHERE o.orderStatus = :status AND o.createdAt >= :startOfDay AND o.createdAt < :endOfDay GROUP BY p.category")
//	    List<Object[]> countProductsSoldByCategoryForDateAndStatus(@Param("status") OrderStatus status, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

//	    @Query("SELECT p.category, COUNT(o), SUM(oi.quantity) FROM OrderHistory o JOIN o.items oi JOIN oi.productId p WHERE o.orderStatus = :status AND o.createdAt >= :startOfDay AND o.createdAt < :endOfDay GROUP BY p.category")
//	    List<Object[]> countProductsSoldByCategoryForDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
//	    
//	    @Query("SELECT oi.productId.category, COUNT(oi) FROM OrderHistory o JOIN o.items oi WHERE YEAR(o.createdAt) = :year GROUP BY oi.productId.category")
//	    List<Object[]> countProductsSoldByCategoryForYear(@Param("year") int year);
	    
	    @Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.items oi JOIN oi.productId p " +
	    	       "WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay GROUP BY p.category")
	    	List<Object[]> countProductsSoldByCategoryForDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

	    	@Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.items oi JOIN oi.productId p " +
	    		       "WHERE YEAR(o.createdAt) = :year GROUP BY p.category")
	    		List<Object[]> countProductsSoldByCategoryForYear(@Param("year") int year);


	    @Query("SELECT p.category, COUNT(p) FROM OrderHistory o JOIN o.items oi JOIN oi.productId p " +
	    	       "WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month GROUP BY p.category")
	    	List<Object[]> countProductsSoldByCategoryForMonth(@Param("year") int year, @Param("month") int month);

		
	    Page<OrderHistory> findByCoupon(Coupon coupon, Pageable pageable);
}

