package com.example.order_service.repository;

import com.example.order_service.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    List<PurchaseOrder> findByStatusIgnoreCase(String status);
}
