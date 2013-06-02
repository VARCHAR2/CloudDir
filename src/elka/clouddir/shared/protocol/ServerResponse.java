package elka.clouddir.shared.protocol;

import java.io.Serializable;

/**
 * Server response
 */
public class ServerResponse implements Serializable {
    private final String message;

    public ServerResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
