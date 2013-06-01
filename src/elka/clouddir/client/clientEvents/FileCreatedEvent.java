package elka.clouddir.client.clientEvents;

public class FileCreatedEvent extends FileChanged {

	private final String name;
	
	public FileCreatedEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
