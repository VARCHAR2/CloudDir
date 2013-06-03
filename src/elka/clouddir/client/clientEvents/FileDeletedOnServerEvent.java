package elka.clouddir.client.clientEvents;

import elka.clouddir.server.model.AbstractFileInfo;

public class FileDeletedOnServerEvent extends ClientEvent {

	private final AbstractFileInfo metadata;
	public FileDeletedOnServerEvent(AbstractFileInfo metadata) {
		this.metadata = metadata;
	}

	public AbstractFileInfo getMetadata() {
		return metadata;
	}
	
}
