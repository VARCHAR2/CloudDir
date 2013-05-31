package elka.clouddir.server.serverevents;

/**
 * Żądanie zalogowania
 */
public class LoginRequestEvent extends ServerEvent {
    private final String username;
    private final String password;


    public LoginRequestEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
