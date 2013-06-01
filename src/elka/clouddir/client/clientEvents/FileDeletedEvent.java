package elka.clouddir.client.clientEvents;

public class FileDeletedEvent extends FileChanged {

	private final String name;
	
	public FileDeletedEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
