package ntut.csie.ezScrum.web.dataObject;

import java.util.HashMap;

public class UserObject {
	private String id;
	private String account;
	private String password;
	private String email;
	private String name;
	private String enable;
	private HashMap<String, ProjectRole> roles;
	
	public UserObject() {}
	
	public UserObject(String id, String account, String name, String password, String email, String enable, HashMap<String, ProjectRole> roles) {
		setId(id);
		setAccount(account);
		setPassword(password);
		setName(name);
		setEmail(email);
		setEnable(enable);
		setRoles(roles);
	}
	
	public UserObject(String id, String account, String name, String password, String email, String enable) {
		setId(id);
		setAccount(account);
		setPassword(password);
		setName(name);
		setEmail(email);
		setEnable(enable);
	}
	
	public UserObject(String account, String name, String password, String email, String enable) {
		setAccount(account);
		setPassword(password);
		setName(name);
		setEmail(email);
		setEnable(enable);
	}
	
	public String toString() {
		String user = "account :" + getAccount() + 
				", password :" + getPassword() +
				", email :" + getEmail() +
				", name :" + getName() +
				", enable :" + getEnable();
		return user;
	}

	public String getId() {
	    return id;
    }

	public void setId(String id) {
	    this.id = id;
    }

	public String getAccount() {
	    return account;
    }

	public void setAccount(String account) {
	    this.account = account;
    }

	public String getPassword() {
	    return password;
    }

	public void setPassword(String password) {
	    this.password = password;
    }

	public String getEmail() {
	    return email;
    }

	public void setEmail(String email) {
	    this.email = email;
    }

	public String getName() {
	    return name;
    }

	public void setName(String name) {
	    this.name = name;
    }

	public String getEnable() {
	    return enable;
    }

	public void setEnable(String enable) {
	    this.enable = enable;
    }

	public HashMap<String, ProjectRole> getRoles() {
	    return roles;
    }

	public void setRoles(HashMap<String, ProjectRole> roles) {
	    this.roles = roles;
    }
}
