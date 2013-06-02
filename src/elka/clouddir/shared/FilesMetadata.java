package elka.clouddir.shared;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Class for sending metadata from client to server
 * 
 * @author bogdan
 */

public class FilesMetadata implements Serializable {

	private AbstractFileInfo[] filesMetadataArray;

	public FilesMetadata(AbstractFileInfo[] filesMetadataArray) {
		this.filesMetadataArray = filesMetadataArray;
	}

	public AbstractFileInfo[] getFilesMetadataArray() {
		return filesMetadataArray;
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
			this.filesMetadataArray = serializedfm.filesMetadataArray;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
