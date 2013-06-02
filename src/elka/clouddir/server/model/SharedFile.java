package elka.clouddir.server.model;


import java.util.Date;

public class SharedFile extends AbstractFileInfo{

	private String md5sum;
	private long size;
	
	public SharedFile(String relativePath, long modified, String lastModifiedBy, String md5sum, long size, Date lastUploadTime) {
		super(relativePath, modified, lastModifiedBy, lastUploadTime);
		// TODO Auto-generated constructor stub
		this.md5sum = md5sum;
		this.size = size;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "File:\nMD5SUM: " + md5sum + "\nSize: " + size + "\n" + super.toString() + "\n";
	}
	
}
