package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Zmieniono plik
 */
public class FileChangedEvent extends FileEvent {
    public FileChangedEvent(ClientCommunicationThread senderThread, AbstractFileInfo metadata) {
        super(senderThread, metadata);
    }
}
