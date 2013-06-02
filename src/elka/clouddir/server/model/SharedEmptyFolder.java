package elka.clouddir.server.model;


import java.util.Date;

public class SharedEmptyFolder extends AbstractFileInfo {

	public SharedEmptyFolder(String relativePath, long modified, String lastModifiedBy, Date lastUploadTime) {
		super(relativePath, modified, lastModifiedBy, lastUploadTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Empty folder: \n" + super.toString() + "\n";
	}

}
