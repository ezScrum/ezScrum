package ntut.csie.ezScrum.SaaS.database;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ExtensionDataStore {
	@SuppressWarnings("unused")
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	@Persistent	
	private String ExtensionType;
	@Persistent	
	private List<String> extensionvalue;
	@Persistent	
	private Long newExtensionId;
	
	public ExtensionDataStore(Key key) {
		this.key = key;
		this.extensionvalue = new ArrayList<String>();
	}
	
	public ExtensionDataStore() {
		key = null;    
		this.extensionvalue = new ArrayList<String>();
	}

	public void setExtensionType(String extensionType) {
		ExtensionType = extensionType;
	}


	public String getExtensionType() {
		return ExtensionType;
	}

	public List<String> getExtensionvalue() {
		return extensionvalue;
	}
		
	public Long getNewExtensionId() {
		return this.newExtensionId;
	}

	public void setNewExtensionId(Long newExtensionId) {
		this.newExtensionId = newExtensionId;
	}
}
