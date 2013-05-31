package elka.clouddir.server.model;


public class SharedEmptyFolder extends AbstractFileInfo {

	public SharedEmptyFolder(String relativePath, long modified, String lastModifiedBy) {
		super(relativePath, modified, lastModifiedBy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Empty folder: \n" + super.toString() + "\n";
	}

}
