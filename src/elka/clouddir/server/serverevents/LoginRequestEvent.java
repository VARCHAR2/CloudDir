package elka.clouddir.server.serverevents;

import elka.clouddir.shared.LoginInfo;

/**
 * Żądanie zalogowania
 */
public class LoginRequestEvent extends ServerEvent {
    private final LoginInfo loginInfo;

    public LoginRequestEvent(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
