package elka.clouddir.server.model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UserGroup {
	private String name;
	private Path sharedFolderPath;
	

	public UserGroup(String name, String sharedFolderPath) {
		super();
		this.name = name;
		this.sharedFolderPath = Paths.get(sharedFolderPath);
	}
	
	public UserGroup(String name, Path sharedFolderPath) {
		super();
		this.name = name;
		this.sharedFolderPath = sharedFolderPath;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Path getSharedFolderPath() {
		return sharedFolderPath;
	}
	
	public void setSharedFolderPath(Path sharedFolderPath) {
		this.sharedFolderPath = sharedFolderPath;
	}
	
	
}
