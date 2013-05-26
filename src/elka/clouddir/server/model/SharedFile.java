package elka.clouddir.server.model;

import java.nio.file.Path;
import java.util.Date;

public class SharedFile extends AbstractFileInfo{

	private long md4sum;
	private long size;
	
	public SharedFile(Path relativePath, Date modified, User lastModifiedBy) {
		super(relativePath, modified, lastModifiedBy);
		// TODO Auto-generated constructor stub
	}

	public long getMd4sum() {
		return md4sum;
	}

	public void setMd4sum(long md4sum) {
		this.md4sum = md4sum;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	
}
