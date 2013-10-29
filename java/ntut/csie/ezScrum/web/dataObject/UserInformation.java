package ntut.csie.ezScrum.web.dataObject;

public class UserInformation {
	
	private String id = "";
	private String password = "";
	private String email = "";
	private String name = "";
	private String enable = "";
	
	public UserInformation(	String id, String name, String password,String email, String enable){
		this.setId(id);
		this.setName(name);
		this.setPassword(password);
		this.setEmail(email);
		this.setEnable(enable);
	}
	
	public void setId(String id) {
		if(id != null ){
			this.id = id;
		}
	}
	public String getId() {
		return id;
	}
	public void setPassword(String password) {
		if(password != null){
			this.password = password;
		}
	}
	public String getPassword() {
		return password;
	}
	public void setEmail(String email) {
		if(email != null ){
			this.email = email;
		}
	}
	public String getEmail() {
		return email;
	}
	public void setName(String name) {
		if(name != null){
			this.name = name;
		}
	}
	public String getName() {
		return name;
	}
	public void setEnable(String enable) {
		if( enable != null ){
			this.enable = enable;
		}
	}
	public String getEnable() {
		return enable;
	}
}
