package ntut.csie.ezScrum.SaaS.database;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class AccountDataStore {
	@SuppressWarnings("unused")
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String id;
		
	@Persistent
	private String name = "";
	
	@Persistent
	private String password = "";
	
	@Persistent
    private String Email;
	
	@Persistent
	private String enable = "";	// 應該是 boolean但目前先不改 fix later
	
	@Persistent
    private List<String> permissions;
	
	public AccountDataStore(Key key, String id, String password) {
		this.key = key;
		this.id = id;
		this.password = password;
	}
	
	// Application accessors for the fields.
	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
	
	public void setEmail(String email) {
		Email = email;
	}

	public String getEmail() {
		return Email;
	}

	// boolean, fix later	
	public void setEnable(String enable) {
		this.enable = enable;
	}

	// boolean, fix later
	public String getEnable() {
		return enable;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(List<String> permissions)
	{
		this.permissions = permissions;
	}
}
