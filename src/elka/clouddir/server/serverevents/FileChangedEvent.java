package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Zmieniono plik
 */
public class FileChangedEvent extends FileEvent {
    private final byte[] data;
    public FileChangedEvent(ClientCommunicationThread senderThread, AbstractFileInfo metadata, byte[] data) {
        super(senderThread, metadata);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
