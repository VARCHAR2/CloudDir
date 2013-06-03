package elka.clouddir.client.exceptions;

public class MetadataNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8868029421236414198L;
	
	private final String metadataName;
	
	public MetadataNotFound(String metadataName) {
		this.metadataName = metadataName;
	}
	
	@Override
	public String getMessage() {
		return "Metadata " + metadataName + " not found." ;
	}
	
}
