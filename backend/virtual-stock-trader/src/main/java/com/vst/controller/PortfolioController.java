package com.vst.controller;

import com.vst.dto.PortfolioDto;
import com.vst.dto.TradeRequest;
import com.vst.entity.Portfolio;
import com.vst.entity.User;
import com.vst.repository.UserRepository;
import com.vst.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserRepository userRepository;

    public PortfolioController(PortfolioService portfolioService, UserRepository userRepository) {
        this.portfolioService = portfolioService;
        this.userRepository = userRepository;
    }

    // ✅ Get full portfolio
    @GetMapping
    public ResponseEntity<List<PortfolioDto>> getPortfolio() {
        User currentUser = getFreshAuthenticatedUser();
        List<PortfolioDto> portfolio = portfolioService.getPortfolioForUser(currentUser);
        return ResponseEntity.ok(portfolio);
    }

    // ✅ Buy stock
    @PostMapping("/buy")
    public ResponseEntity<PortfolioDto> buyStock(@RequestBody TradeRequest tradeRequest) {
        User currentUser = getFreshAuthenticatedUser();
        Portfolio updatedHolding = portfolioService.buyStock(currentUser, tradeRequest);
        return ResponseEntity.ok(convertToDto(updatedHolding));
    }

    // ✅ Sell stock
    @PostMapping("/sell")
    public ResponseEntity<PortfolioDto> sellStock(@RequestBody TradeRequest tradeRequest) {
        User currentUser = getFreshAuthenticatedUser();
        Portfolio updatedHolding = portfolioService.sellStock(currentUser, tradeRequest);
        return ResponseEntity.ok(convertToDto(updatedHolding));
    }

    // ✅ Convert Portfolio → DTO safely
    private PortfolioDto convertToDto(Portfolio portfolio) {
        return new PortfolioDto(
                portfolio.getId(),
                portfolio.getStockSymbol(),
                portfolio.getSharesOwned(),
                portfolio.getAveragePrice()
        );
    }

    // ✅ Get authenticated User from DB
    private User getFreshAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}
