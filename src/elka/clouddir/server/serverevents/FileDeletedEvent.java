package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Usunięto plik
 * @author Michał Toporowski
 */
public class FileDeletedEvent extends FileEvent{
    public FileDeletedEvent(ClientCommunicationThread senderThread, AbstractFileInfo metadata) {
        super(senderThread, metadata);
    }
}
