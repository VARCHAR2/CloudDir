package elka.clouddir.shared;

import java.io.Serializable;

import elka.clouddir.server.model.AbstractFileInfo;


/**
 * Class for sending metadata from client to server
 * Use elka.clouddir.shared.protocol.SharedDirectoryMetadataBean instead
 * @author bogdan
 */
@Deprecated
public class FilesMetadata implements Serializable {

	private final AbstractFileInfo[] filesMetadataArray;
	
	public FilesMetadata(AbstractFileInfo[] filesMetadataArray) {
		this.filesMetadataArray = filesMetadataArray;
	}

	public AbstractFileInfo[] getFilesMetadataArray() {
		return filesMetadataArray;
	}
	
}
