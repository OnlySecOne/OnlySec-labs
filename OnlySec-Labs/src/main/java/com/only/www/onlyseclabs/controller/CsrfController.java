package com.only.www.onlyseclabs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/csrf")
public class CsrfController {

    // 模拟数据存储
    private static Map<String, Double> balances = new HashMap<>();
    private static List<Transaction> transactionsA = new ArrayList<>();
    private static List<Transaction> transactionsB = new ArrayList<>();

    // 初始化账户余额
    static {
        balances.put("A", 10000.00);
        balances.put("B", 5000.00);
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("balanceA", balances.get("A"));
        model.addAttribute("balanceB", balances.get("B"));
        model.addAttribute("transactionsA", transactionsA);
        model.addAttribute("transactionsB", transactionsB);
        return "web_vul_basic/csrf";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam Double amount) {

        // 检查余额
        if (amount <= 0 || amount > balances.get(fromAccount)) {
            return "redirect:/csrf?error=invalid_amount";
        }

        // 执行转账
        balances.put(fromAccount, balances.get(fromAccount) - amount);
        balances.put(toAccount, balances.get(toAccount) + amount);

        // 记录交易
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        if ("A".equals(fromAccount)) {
            transactionsA.add(0, transaction);
        } else {
            transactionsB.add(0, transaction);
        }

        return "redirect:/csrf?success=true";
    }

    // 内部交易记录类
    private static class Transaction {
        private String fromAccount;
        private String toAccount;
        private Double amount;
        private LocalDateTime timestamp;

        // Getters and Setters
        public String getFromAccount() { return fromAccount; }
        public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }
        public String getToAccount() { return toAccount; }
        public void setToAccount(String toAccount) { this.toAccount = toAccount; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}