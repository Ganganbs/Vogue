package ecom.Ecom.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, UUID> {
    List<OrderItems> findByOrderHistory(OrderHistory orderHistory);
}
