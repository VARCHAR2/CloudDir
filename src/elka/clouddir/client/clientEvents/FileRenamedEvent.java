package elka.clouddir.client.clientEvents;

public class FileRenamedEvent extends FileChangedEvent {

	private final String oldName;
	private final String newName;
	
	public FileRenamedEvent(String oldName, String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	public String getOldName() {
		return oldName;
	}
	
	public String getNewName() {
		return newName;
	}
	
}
