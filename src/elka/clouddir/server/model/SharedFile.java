package elka.clouddir.server.model;


import java.util.Date;

public class SharedFile extends AbstractFileInfo{

	public SharedFile(String relativePath, long modified, String lastModifiedBy, Date lastUploadTime) {
		super(relativePath, modified, lastModifiedBy, lastUploadTime);
	}

}
