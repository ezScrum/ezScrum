package ntut.csie.ezScrum.web.dataInfo;

public class AccountInfo {
	public long id = -1;
	public String userName, password, email, nickName;
	public boolean enable = false;

//	public AccountInfo(String username, String name, String password, String email, String enable) {
//		this.setUsername(username);
//		this.setName(name);
//		this.setPassword(password);
//		this.setEmail(email);
//		this.setEnable(enable);
//	}
//	
//	public AccountInfo(String id, String username, String name, String password, String email, String enable) {
//		this.setId(id);
//		this.setUsername(username);
//		this.setName(name);
//		this.setPassword(password);
//		this.setEmail(email);
//		this.setEnable(enable);
//	}

	public String getId() {
	    return id;
    }

	public void setId(String id) {
	    this.id = id;
    }

	public void setUsername(String account) {
		if (account != null) {
			this.username = account;
		}
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		if (password != null) {
			this.password = password;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setEmail(String email) {
		if (email != null) {
			this.email = email;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setName(String name) {
		if (name != null) {
			this.nickName = name;
		}
	}

	public String getName() {
		return nickName;
	}

	public void setEnable(String enable) {
		if (enable != null) {
			this.enable = enable;
		}
	}

	public String getEnable() {
		return enable;
	}
}
