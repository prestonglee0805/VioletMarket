package violetmarket.model;

import violetmarket.exception.InsufficientFundsException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wallet {

    private double balance;
    private final List<String> transactionHistory;

    public Wallet(){
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
    }

    // Operations
    public void deposit(double amount){
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
        log("DEPOSIT", "+", amount);
    }

    public void debit(double amount) throws InsufficientFundsException {
        if (amount > balance) throw new InsufficientFundsException(balance, amount);
        balance -= amount;
        log("DEBIT", "-", amount);
    }

    public void credit(double amount){
        balance += amount;
        log("CREDIT", "+", amount);
    }

    // Getters
    public double getBalance(){
        return balance;
    }
    public List<String> getTransactionHistory(){
        return Collections.unmodifiableList(transactionHistory);
    }

    // Helpers
    private void log(String type, String sign, double amount){
        transactionHistory.add(String.format(
            "[%s] %s %s$%.2f  |  Balance: $%.2f",
            LocalDateTime.now().toLocalDate(), type, sign, amount, balance));
    }
}
