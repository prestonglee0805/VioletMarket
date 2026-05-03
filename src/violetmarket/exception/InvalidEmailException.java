package violetmarket.exception;

public class InvalidEmailException extends Exception {

    public InvalidEmailException(String email) {
        super("Invalid email: '" + email + "'. Only @nyu.edu addresses are accepted.");
    }
}
