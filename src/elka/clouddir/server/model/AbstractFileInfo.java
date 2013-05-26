package elka.clouddir.server.model;

import java.nio.file.Path;
import java.util.Date;

public abstract class AbstractFileInfo {
	private Path relativePath;
	private Date modified;
	private User lastModifiedBy;
	
	
	public AbstractFileInfo(Path relativePath, Date modified,
			User lastModifiedBy) {
		super();
		this.relativePath = relativePath;
		this.modified = modified;
		this.lastModifiedBy = lastModifiedBy;
	}

	public Path getRelativePath() {
		return relativePath;
	}
	
	public void setRelativePath(Path relativePath) {
		this.relativePath = relativePath;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public User getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
}
