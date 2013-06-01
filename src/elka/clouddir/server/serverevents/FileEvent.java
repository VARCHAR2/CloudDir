package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Klasa bazowa dla zdarzeń typu FileChangedEvent, FileDeletedEvent itp.
 * @author Michał Toporowski
 */
public abstract class FileEvent extends CommunicationEvent {
    private final AbstractFileInfo metadata;

    protected FileEvent(ClientCommunicationThread senderThread, AbstractFileInfo metadata) {
        super(senderThread);
        this.metadata = metadata;
    }

    public AbstractFileInfo getMetadata() {
        return metadata;
    }
}
