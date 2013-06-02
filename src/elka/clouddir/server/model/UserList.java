package elka.clouddir.server.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import elka.clouddir.shared.FilesMetadata;

public class UserList implements Serializable{
	List<User> users;

	public UserList() {
		users = new ArrayList<>();
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	
	/**
	 * Saves the state of the object to the file ser/user-list.ser. 
	 */
	public void pushToFile() {
		try (FileOutputStream fileOut = new FileOutputStream(
				"ser/user-list.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the state of the object from the file ser/user-list.ser.
	 */
	public void pullFromFile() {
		try (FileInputStream fileIn = new FileInputStream("ser/user-list.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			UserList userList = (UserList) in.readObject();
			this.users = userList.users;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
