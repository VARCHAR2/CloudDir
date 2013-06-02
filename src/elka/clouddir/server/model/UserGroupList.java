package elka.clouddir.server.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserGroupList implements Serializable{
	List<UserGroup> groups;

	public UserGroupList() {
		groups = new ArrayList<>();
	}

	public List<UserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}
	
	/**
	 * Saves the state of the object to the file ser/group-list.ser. 
	 */
	public void pushToFile() {
		try (FileOutputStream fileOut = new FileOutputStream(
				"ser/group-list.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the state of the object from the file ser/group-list.ser.
	 */
	public void pullFromFile() {
		try (FileInputStream fileIn = new FileInputStream("ser/group-list.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			UserGroupList userGroupList = (UserGroupList) in.readObject();
			this.groups = userGroupList.groups;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
