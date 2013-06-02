package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.shared.FilesMetadata;

/**
 * Full metadata transfer
 */
public class FullMetadataTransferEvent extends CommunicationEvent {
    private final FilesMetadata metadata;

    public FullMetadataTransferEvent(ClientCommunicationThread senderThread, FilesMetadata metadata) {
        super(senderThread);
        this.metadata = metadata;
    }

    public FilesMetadata getMetadata() {
        return metadata;
    }
}
