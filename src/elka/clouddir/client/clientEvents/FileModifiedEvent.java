package elka.clouddir.client.clientEvents;

public class FileModifiedEvent extends FileChanged {

	private final String name;
	
	public FileModifiedEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
