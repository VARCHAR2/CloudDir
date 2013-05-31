package elka.clouddir.shared;

import java.io.Serializable;

import elka.clouddir.server.model.AbstractFileInfo;

public class FilesMetadata implements Serializable {

	private final AbstractFileInfo[] filesMetadataArray;
	
	public FilesMetadata(AbstractFileInfo[] filesMetadataArray) {
		this.filesMetadataArray = filesMetadataArray;
	}

	public AbstractFileInfo[] getFilesMetadataArray() {
		return filesMetadataArray;
	}
	
}
