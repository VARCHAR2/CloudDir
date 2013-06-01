package elka.clouddir.shared.protocol;

import java.io.Serializable;
import java.util.List;

import elka.clouddir.server.model.SharedEmptyFolder;
import elka.clouddir.server.model.SharedFile;


/**
 * @author Lukasz
 * Used for storing metadata of shared directory. Object of this class will be sent when user connects after the period of being offline.
 */
public class SharedDirectoryMetadataBean implements Serializable{
	List<SharedFile> files;
	List<SharedEmptyFolder> folders;
	
	
	public SharedDirectoryMetadataBean() {
		
	}
}
