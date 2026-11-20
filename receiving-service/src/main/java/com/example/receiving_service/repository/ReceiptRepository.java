package com.example.receiving_service.repository;

import com.example.receiving_service.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    List<Receipt> findByStatusIgnoreCase(String status);
}
