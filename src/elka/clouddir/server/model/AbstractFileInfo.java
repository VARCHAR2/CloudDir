package elka.clouddir.server.model;

import java.io.Serializable;
import java.util.Date;


public abstract class AbstractFileInfo implements Serializable {
	
	private String relativePath;
	private long modified;
	private String lastModifiedBy;
    private Date lastUploadTime;
	
	
	public AbstractFileInfo(String relativePath, long modified,
                            String username, Date lastUploadTime) {
		super();
		this.relativePath = relativePath;
		this.modified = modified;
		this.lastModifiedBy = username;
        this.lastUploadTime = lastUploadTime;

        if(relativePath.charAt(0) == '/') relativePath = relativePath.substring(1);
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

    public Date getLastUploadTime() {
        return lastUploadTime;
    }

    public void setLastUploadTime(Date lastUploadTime) {
        this.lastUploadTime = lastUploadTime;
    }


    /**
     * Gets the absolute path of a file
     */
    public String getServerPath(final UserGroup owner) {
        String path = owner.getSharedFolderPath();
        if(path.charAt(path.length() - 1) != '/' && relativePath.charAt(0) != '/') path += "/";
        path += relativePath;
        return path;
    }

}
