package elka.clouddir.server.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import elka.clouddir.shared.FilesMetadata;

public class User implements Serializable {
	private String name;
	private String password;
	private boolean loggedIn;
	private UserGroup userGroup;

	public User(String name, boolean loggedIn, UserGroup userGroup,
			String password) {
		super();
		this.name = name;
		this.loggedIn = loggedIn;
		this.password = password;
		this.setUserGroup(userGroup);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Saves the state of the object to file identified by the {@code name}.
	 */
	public void pushToFile() {
		try (FileOutputStream fileOut = new FileOutputStream("ser/user-" + name
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
		try (FileInputStream fileIn = new FileInputStream("ser/user-" + name + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			User u = (User) in.readObject();
			this.loggedIn = false;
			this.name = u.name;
			this.password = u.password;
			this.userGroup = u.userGroup;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the state of the object from file.
	 */
	public void pullFromFile(String userName) {
		try (FileInputStream fileIn = new FileInputStream("ser/user-"
				+ userName + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			User u = (User) in.readObject();
			this.loggedIn = false;
			this.name = u.name;
			this.password = u.password;
			this.userGroup = u.userGroup;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
