package elka.clouddir.client.clientEvents;

public class FileModifiedEvent extends FileChangedEvent {

	private final String name;
	
	public FileModifiedEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
