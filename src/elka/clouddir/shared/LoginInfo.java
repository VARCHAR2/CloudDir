package elka.clouddir.shared;

import java.io.Serializable;

/**
 * Class for keeping the log in information while sending it to server
 * @author bogdan
 *
 */
public class LoginInfo implements Serializable {

	private static final long serialVersionUID = -3189573616787247176L;
	
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
