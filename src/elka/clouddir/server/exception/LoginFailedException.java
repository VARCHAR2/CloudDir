package elka.clouddir.server.exception;

/**
 * @author Michał Toporowski
 */
public class LoginFailedException extends Exception {
    private final String message;

    public LoginFailedException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
