package com.vst.service;

import com.vst.dto.PortfolioDto;
import com.vst.dto.TradeRequest;
import com.vst.entity.Portfolio;
import com.vst.entity.Transaction;
import com.vst.entity.User;
import com.vst.repository.PortfolioRepository;
import com.vst.repository.TransactionRepository;
import com.vst.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            TransactionRepository transactionRepository,
                            UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    // ✅ Get user’s full portfolio
    public List<PortfolioDto> getPortfolioForUser(User user) {
        return portfolioRepository.findByUser(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ✅ Buy stock (weighted avg)
    @Transactional
    public Portfolio buyStock(User user, TradeRequest tradeRequest) {
        Portfolio holding = portfolioRepository.findByUserAndStockSymbol(user, tradeRequest.getStockSymbol())
                .orElse(new Portfolio(user, tradeRequest.getStockSymbol(), 0, 0.0));

        int existingShares = holding.getSharesOwned();
        double existingAvgPrice = holding.getAveragePrice();

        int newShares = existingShares + tradeRequest.getQuantity();
        double totalCost = (existingShares * existingAvgPrice) +
                           (tradeRequest.getQuantity() * tradeRequest.getPrice());
        double newAvgPrice = totalCost / newShares;

        holding.setSharesOwned(newShares);
        holding.setAveragePrice(newAvgPrice);

        Portfolio savedHolding = portfolioRepository.save(holding);

        // ✅ Log transaction
        Transaction transaction = new Transaction(
                user,
                tradeRequest.getStockSymbol(),
                "BUY",
                tradeRequest.getQuantity(),
                tradeRequest.getPrice()
        );
        transactionRepository.save(transaction);

        return savedHolding;
    }

    // ✅ Sell stock
    @Transactional
    public Portfolio sellStock(User user, TradeRequest tradeRequest) {
        Portfolio holding = portfolioRepository.findByUserAndStockSymbol(user, tradeRequest.getStockSymbol())
                .orElseThrow(() -> new RuntimeException("You don't own this stock: " + tradeRequest.getStockSymbol()));

        if (holding.getSharesOwned() < tradeRequest.getQuantity()) {
            throw new RuntimeException("Not enough shares to sell. Owned: "
                    + holding.getSharesOwned()
                    + ", Tried to sell: "
                    + tradeRequest.getQuantity());
        }

        holding.setSharesOwned(holding.getSharesOwned() - tradeRequest.getQuantity());

        Portfolio updatedHolding;
        if (holding.getSharesOwned() == 0) {
            portfolioRepository.delete(holding);
            updatedHolding = holding; // returning deleted reference for DTO
        } else {
            updatedHolding = portfolioRepository.save(holding);
        }

        // ✅ Log transaction
        Transaction transaction = new Transaction(
                user,
                tradeRequest.getStockSymbol(),
                "SELL",
                tradeRequest.getQuantity(),
                tradeRequest.getPrice()
        );
        transactionRepository.save(transaction);

        return updatedHolding;
    }

    private PortfolioDto convertToDto(Portfolio portfolio) {
        return new PortfolioDto(
                portfolio.getId(),
                portfolio.getStockSymbol(),
                portfolio.getSharesOwned(),
                portfolio.getAveragePrice()
        );
    }
}
