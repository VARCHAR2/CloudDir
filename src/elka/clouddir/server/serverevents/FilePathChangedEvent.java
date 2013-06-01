package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Ścieżka pliku zmieniona.
 * @author Michał Toporowski
 */
public class FilePathChangedEvent extends FileEvent {
    public FilePathChangedEvent(ClientCommunicationThread senderThread, AbstractFileInfo metadata) {
        super(senderThread, metadata);
    }
}
