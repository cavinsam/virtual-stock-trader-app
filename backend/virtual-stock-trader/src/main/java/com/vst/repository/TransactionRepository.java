package com.vst.repository;

import com.vst.entity.Transaction;
import com.vst.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ✅ Fetch all transactions of a user
    List<Transaction> findByUser(User user);

    // ✅ Fetch by user + stock symbol (e.g., show transaction history of one stock)
    List<Transaction> findByUserAndStockSymbol(User user, String stockSymbol);

    // ✅ Fetch latest transactions (optional - useful for UI dashboard)
    List<Transaction> findTop10ByUserOrderByTimestampDesc(User user);
}
