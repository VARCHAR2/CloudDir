package elka.clouddir.shared;

import java.io.Serializable;

public class LoginInfo implements Serializable {

	private final String login;
	private final String password; 
	
	public LoginInfo(String login, String password) {
		this.login= login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
}
