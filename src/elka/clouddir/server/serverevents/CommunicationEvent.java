package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;

/**
 * Zdarzenie wywołane przez ClientCommunicationThread
 */
public abstract class CommunicationEvent extends ServerEvent {
    private final ClientCommunicationThread senderThread;

    protected CommunicationEvent(ClientCommunicationThread senderThread) {
        this.senderThread = senderThread;
    }

    public ClientCommunicationThread getSenderThread() {
        return senderThread;
    }
}
