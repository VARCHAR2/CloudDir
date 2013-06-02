package elka.clouddir.shared;

public enum Message {
	LOGIN_REQUEST("Login request"),
	LOGIN_OK("Login ok"),
	LOGIN_FAILED("Login failed"),
	FILE_CHANGED("File changed"),
	FILEPATH_CHANGED("Filepath changed"),
	FILE_DELETED("File deleted"),
	FILE_REQUEST("File request"),
	FULL_METADATA_TRANSFER("Full metadata transfer"),
	FILE_TRANSFER("File transfer"),
    CONFLICT_DETECTED("Conflict detected"),
    FILE_BEING_UPDATED("File being updated"),
    INTERNAL_SERVER_ERROR("Internal server error");
	
	private String messageName;
	
	private Message(String name) {
		this.messageName = name;
	}
	
	@Override
	public String toString() {
		return this.messageName;
	}
}