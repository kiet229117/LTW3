package com.example.inventory_service.repository;

import com.example.inventory_service.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
}
