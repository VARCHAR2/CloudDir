package elka.clouddir.server.model;

import java.nio.file.Path;
import java.util.Date;

public class SharedEmptyFolder extends AbstractFileInfo {

	public SharedEmptyFolder(String relativePath, long modified, User lastModifiedBy) {
		super(relativePath, modified, lastModifiedBy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Empty folder: \n" + super.toString() + "\n";
	}

}
