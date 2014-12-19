package ntut.csie.ezScrum.web.dataInfo;

public class AccountInfo {
	private String id = "";
	private String account = "";
	private String password = "";
	private String email = "";
	private String name = "";
	private String enable = "";

	public AccountInfo(String account, String name, String password, String email, String enable) {
		this.setAccount(account);
		this.setName(name);
		this.setPassword(password);
		this.setEmail(email);
		this.setEnable(enable);
	}
	
	public AccountInfo(String id, String account, String name, String password, String email, String enable) {
		this.setId(id);
		this.setAccount(account);
		this.setName(name);
		this.setPassword(password);
		this.setEmail(email);
		this.setEnable(enable);
	}

	public String getId() {
	    return id;
    }

	public void setId(String id) {
	    this.id = id;
    }

	public void setAccount(String account) {
		if (account != null) {
			this.account = account;
		}
	}

	public String getAccount() {
		return account;
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
			this.name = name;
		}
	}

	public String getName() {
		return name;
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
