package elka.clouddir.server.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserGroup implements Serializable{
	private String name;
	private String sharedFolderPath;
	

	public UserGroup(String name, String sharedFolderPath) {
		super();
		this.name = name;
		this.sharedFolderPath = sharedFolderPath;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSharedFolderPath() {
		return sharedFolderPath;
	}
	
	public void setSharedFolderPath(String sharedFolderPath) {
		this.sharedFolderPath = sharedFolderPath;
	}
	
	/**
	 * Saves the state of the object to file identified by the {@code name}.
	 */
	public void pushToFile() {
		try (FileOutputStream fileOut = new FileOutputStream("ser/group-" + name
				+ ".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the state of the object from file. 
	 * You must be sure of what you are doing if you want to use this method.
	 * Better use public void pullFromFile(String userName).
	 */
	public void pullFromFile() {
		try (FileInputStream fileIn = new FileInputStream("ser/group-" + name + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			UserGroup u = (UserGroup) in.readObject();
			this.name = u.name;
			this.sharedFolderPath = u.sharedFolderPath;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the state of the object from file.
	 */
	public void pullFromFile(String groupName) {
		try (FileInputStream fileIn = new FileInputStream("ser/group-"
				+ groupName + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			UserGroup u = (UserGroup) in.readObject();
			this.name = u.name;
			this.sharedFolderPath = u.sharedFolderPath;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	
}
