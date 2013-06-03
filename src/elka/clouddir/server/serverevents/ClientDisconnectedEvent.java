package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;

/**
 * Client disconnected
 */
public class ClientDisconnectedEvent extends CommunicationEvent {
    public ClientDisconnectedEvent(ClientCommunicationThread senderThread) {
        super(senderThread);
    }
}
