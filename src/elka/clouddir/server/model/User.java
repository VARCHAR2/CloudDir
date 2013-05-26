package elka.clouddir.server.model;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean loggedIn;
	private UserGroup userGroup;
	
	
	public User(String name, boolean loggedIn, UserGroup userGroup) {
		super();
		this.name = name;
		this.loggedIn = loggedIn;
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
	
}
