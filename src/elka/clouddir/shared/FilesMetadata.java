package elka.clouddir.shared;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Class for sending metadata from client to server
 * 
 * @author bogdan
 */

public class FilesMetadata implements Serializable {

	private List<AbstractFileInfo> filesMetaList;

	public FilesMetadata(List<AbstractFileInfo> filesMetaList) {
		this.filesMetaList = filesMetaList;
	}

	public List<AbstractFileInfo> getFilesMetaList() {
		return filesMetaList;
	}

	/**
	 * @param groupName
	 * Used by server to distinct different groups
	 */
	public void pushToFile(String groupName) {
		try (FileOutputStream fileOut = new FileOutputStream(
				"ser/files-meta-" + groupName + ".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @param groupName
	 * Used by server to distinct different groups
	 */
	public void pullFromFile(String groupName) {
		try (FileInputStream fileIn = new FileInputStream("ser/files-meta-" + groupName + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			FilesMetadata serializedfm = (FilesMetadata) in.readObject();
			this.filesMetaList = serializedfm.filesMetaList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void pushToFile() {
		try (FileOutputStream fileOut = new FileOutputStream(
				"ser/files-meta.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pullFromFile() {
		try (FileInputStream fileIn = new FileInputStream("ser/files-meta.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			FilesMetadata serializedfm = (FilesMetadata) in.readObject();
			this.filesMetaList = serializedfm.filesMetaList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
