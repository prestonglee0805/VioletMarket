package violetmarket.exception;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(double balance, double required) {
        super(String.format(
            "Insufficient funds. Balance: $%.2f, Required: $%.2f", balance, required));
    }
}
