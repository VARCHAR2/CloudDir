package elka.clouddir.server.model;


public abstract class AbstractFileInfo {
	private String relativePath;
	private long modified;
	private String lastModifiedBy;
	
	
	public AbstractFileInfo(String relativePath, long modified,
			String username) {
		super();
		this.relativePath = relativePath;
		this.modified = modified;
		this.lastModifiedBy = username;
	}

	public String getRelativePath() {
		return relativePath;
	}
	
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	public long getModified() {
		return modified;
	}
	
	public void setModified(long modified) {
		this.modified = modified;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	@Override
	public String toString() {
		return "Relative path: " + relativePath + "\nModified: " + modified + "\nBy: " + lastModifiedBy;
	}
	
}
