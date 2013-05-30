package elka.clouddir.server.model;

import java.nio.file.Path;
import java.util.Date;

public abstract class AbstractFileInfo {
	private String relativePath;
	private long modified;
	private User lastModifiedBy;
	
	
	public AbstractFileInfo(String relativePath, long modified,
			User lastModifiedBy) {
		super();
		this.relativePath = relativePath;
		this.modified = modified;
		this.lastModifiedBy = lastModifiedBy;
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
	
	public User getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	@Override
	public String toString() {
		return "Relative path: " + relativePath + "\nModified: " + modified + "\nBy: " + lastModifiedBy.toString();
	}
	
}
