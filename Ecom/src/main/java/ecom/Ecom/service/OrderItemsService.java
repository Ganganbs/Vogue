package ecom.Ecom.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecom.Ecom.entity.OrderHistory;
import ecom.Ecom.entity.OrderItems;
import ecom.Ecom.repository.OrderItemsRepository;

@Service
public class OrderItemsService{
    @Autowired
    OrderItemsRepository orderItemsRepository;
    
    
    public OrderItems save(OrderItems item) {
        return orderItemsRepository.save(item);
    }

    public List<OrderItems> findByOrder(OrderHistory orderHistory) {
        return orderItemsRepository.findByOrderHistory(orderHistory);
    }
}

