package elka.clouddir.server.model;


public class SharedFile extends AbstractFileInfo{

	private String md4sum;
	private long size;
	
	public SharedFile(String relativePath, long modified, String lastModifiedBy, String md4sum, long size) {
		super(relativePath, modified, lastModifiedBy);
		// TODO Auto-generated constructor stub
		this.md4sum = md4sum;
		this.size = size;
	}

	public String getMd4sum() {
		return md4sum;
	}

	public void setMd4sum(String md4sum) {
		this.md4sum = md4sum;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "File:\nMD4SUM: " + md4sum + "\nSize: " + size + "\n" + super.toString() + "\n";
	}
	
}
