package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * File transfer event
 */
@Deprecated
public class FileTransferEvent extends FileEvent {
    private final byte[] data;

    public FileTransferEvent(ClientCommunicationThread senderThread, AbstractFileInfo metadata, byte[] data) {
        super(senderThread, metadata);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
