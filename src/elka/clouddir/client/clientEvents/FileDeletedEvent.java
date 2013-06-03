package elka.clouddir.client.clientEvents;

public class FileDeletedEvent extends FileChangedEvent {

	private final String name;
	
	public FileDeletedEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
