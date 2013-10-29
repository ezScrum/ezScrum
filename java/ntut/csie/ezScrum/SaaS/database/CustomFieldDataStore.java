package ntut.csie.ezScrum.SaaS.database;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable="true")
public class CustomFieldDataStore {
	@SuppressWarnings("unused")
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key = null;
	@Persistent
	private String id = "";
	@Persistent
	private List<String> mapKey;
	@Persistent
	private List<String> mapValue;
	@Persistent
	private String customFieldType = "";
	
	public CustomFieldDataStore(Key key) {
		this.key = key;
		this.mapKey = new ArrayList<String>();
		this.mapValue = new ArrayList<String>();
	}
	
	public void setField(String recordID,String extenfield) {
		//process key
		int keyIndex = 0;
		if(this.mapKey.contains(recordID)) {
			//do nothing
		} else {
			mapKey.add(recordID);
			mapValue.add("");
		}
		keyIndex = this.mapKey.indexOf(recordID);
		
		//process value
		mapValue.remove(keyIndex);
		mapValue.add(keyIndex, extenfield);
	}

	public String getField(String recordID) {
		int keyIndex = 0;
		final int NOTFOUND = -1;
		keyIndex = this.mapKey.indexOf(recordID);
		if(keyIndex == NOTFOUND) {
			return "";
		} else {
			return mapValue.get(keyIndex);
		}
	}
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCustomFieldType() {
		return this.customFieldType;
	}

	public void setCustomFieldType(String customFieldType) {
		this.customFieldType = customFieldType;
	}
	
	public List<String> getcustomFieldKeys() {
		return this.mapKey;
	}
	
	public List<String> getCustomFieldValues() {
		return this.mapValue;
	}
}
