package com.only.www.onlyseclabs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@RequestMapping("/race")
public class RaceConditionController {

    // 使用单例模式存储账户余额
    private static class AccountHolder {
        // 不安全的账户余额实现
        private static Integer accountA = 1000;
        private static Integer accountB = 1000;

        // 安全的账户余额实现
        private static final AtomicInteger safeAccountA = new AtomicInteger(1000);
        private static final AtomicInteger safeAccountB = new AtomicInteger(1000);

        // 用于安全实现的锁
        private static final Lock transferLock = new ReentrantLock();

        public static void reset() {
            accountA = 1000;
            accountB = 1000;
            safeAccountA.set(1000);
            safeAccountB.set(1000);
        }
    }

    @GetMapping("/web")
    public String showPage() {
        return "web_vul_basic/race_condition";
    }

    @PostMapping("/reset")
    @ResponseBody
    public String resetAccounts() {
        AccountHolder.reset();
        return "账户余额已重置为: 1000";
    }

    @PostMapping("/check")
    @ResponseBody
    public String checkBalance() {
        return String.format("账户A余额: %d, 账户B余额: %d",
                AccountHolder.accountA, AccountHolder.accountB);
    }

    /**
     * 不安全的转账实现
     */
    @PostMapping("/unsafe")
    @ResponseBody
    public String unsafeTransfer(@RequestParam(defaultValue = "100") int amount) {
        try {
            // 模拟网络延迟
            Thread.sleep(100);

            // 检查余额是否足够
            if (AccountHolder.accountA >= amount) {
                // 这里可能发生并发问题
                AccountHolder.accountA -= amount;
                AccountHolder.accountB += amount;
                return String.format("转账成功! A余额:%d, B余额:%d",
                        AccountHolder.accountA, AccountHolder.accountB);
            } else {
                return "余额不足，转账失败";
            }
        } catch (InterruptedException e) {
            return "转账发生错误: " + e.getMessage();
        }
    }

    /**
     * 安全的转账实现
     */
    @PostMapping("/safe")
    @ResponseBody
    public String safeTransfer(@RequestParam(defaultValue = "100") int amount) {
        try {
            // 使用锁保证原子性
            AccountHolder.transferLock.lock();

            // 模拟网络延迟
            Thread.sleep(100);

            int currentBalanceA = AccountHolder.safeAccountA.get();
            if (currentBalanceA >= amount) {
                AccountHolder.safeAccountA.addAndGet(-amount);
                AccountHolder.safeAccountB.addAndGet(amount);
                return String.format("转账成功! A余额:%d, B余额:%d",
                        AccountHolder.safeAccountA.get(), AccountHolder.safeAccountB.get());
            } else {
                return "余额不足，转账失败";
            }
        } catch (InterruptedException e) {
            return "转账发生错误: " + e.getMessage();
        } finally {
            AccountHolder.transferLock.unlock();
        }
    }
}