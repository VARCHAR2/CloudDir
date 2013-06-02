package elka.clouddir.client.clientEvents;

import elka.clouddir.shared.protocol.ServerResponse;

/*
* Server response
 */
public class ServerResponseEvent extends ClientEvent {
    private final ServerResponse serverResponse;

    public ServerResponseEvent(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }
}
