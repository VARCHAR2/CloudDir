package elka.clouddir.client.exceptions;

public class MetadataNotFound extends Exception {

	private final String metadataName;
	
	public MetadataNotFound(String metadataName) {
		this.metadataName = metadataName;
	}
	
	@Override
	public String getMessage() {
		return "Metadata " + metadataName + " not found." ;
	}
	
}
