package elka.clouddir.client.clientEvents;

import elka.clouddir.server.model.AbstractFileInfo;

public class FileChangedOnServerEvent extends ClientEvent {

	private final AbstractFileInfo metadata;
    private final byte[] data;
    
    public FileChangedOnServerEvent(AbstractFileInfo metadata, byte[] data) {
        this.metadata = metadata;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
    
    public AbstractFileInfo getMetadata() {
		return metadata;
	}
    
}
