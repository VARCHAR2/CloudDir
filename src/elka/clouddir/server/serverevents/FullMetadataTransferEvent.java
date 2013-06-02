package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Full metadata transfer
 */
public class FullMetadataTransferEvent extends CommunicationEvent {
    private final AbstractFileInfo[] metadata;

    public FullMetadataTransferEvent(ClientCommunicationThread senderThread, AbstractFileInfo[] metadata) {
        super(senderThread);
        this.metadata = metadata;
    }

    public AbstractFileInfo[] getMetadata() {
        return metadata;
    }
}
