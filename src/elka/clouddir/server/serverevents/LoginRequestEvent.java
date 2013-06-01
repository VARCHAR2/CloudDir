package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.shared.LoginInfo;

/**
 * Żądanie zalogowania
 */
public class LoginRequestEvent extends CommunicationEvent {
    private final LoginInfo loginInfo;

    public LoginRequestEvent(ClientCommunicationThread senderThread, LoginInfo loginInfo) {
        super(senderThread);
        this.loginInfo = loginInfo;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
