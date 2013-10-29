package ntut.csie.ezScrum.web.dataObject;

public class UserObject {
	public String account;
	public String password;
	public String email;
	public String name;
	public String enable;
	
	public String toString() {
		String user = "account :" + account + 
				", password :" + password +
				", email :" + email +
				", name :" + name +
				", enable :" + enable;
		return user;
	}
}
