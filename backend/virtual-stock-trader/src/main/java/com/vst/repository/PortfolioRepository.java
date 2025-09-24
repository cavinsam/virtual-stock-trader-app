package com.vst.repository;

import com.vst.entity.Portfolio;
import com.vst.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * Finds all portfolio holdings for a specific user.
     * Spring Data JPA automatically creates the query based on the method name.
     * @param user The user whose portfolio to find.
     * @return A list of portfolio items.
     */
    List<Portfolio> findByUser(User user);

    /**
     * Finds a specific stock holding for a given user.
     * This is used to check if a user already owns a stock before buying more.
     * @param user The user.
     * @param stockSymbol The stock symbol (e.g., "AAPL").
     * @return An Optional containing the portfolio item if it exists.
     */
    Optional<Portfolio> findByUserAndStockSymbol(User user, String stockSymbol);
}

