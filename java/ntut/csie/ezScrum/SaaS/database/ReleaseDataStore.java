package ntut.csie.ezScrum.SaaS.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ReleaseDataStore {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@SuppressWarnings("unused")
	@Persistent
	private ProjectDataStore project;
	
	@Persistent
	private String name;
	@Persistent
	private String id;
	@Persistent
	private String description;
	@Persistent
	private String startdate;
	@Persistent
	private String enddate;
	
	
	
	
	public ReleaseDataStore(Key key) {
		this.key = key;
	}
	public Key getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartDate() {
		return startdate;
	}
	public void setStartDate(String startdate) {
		this.startdate = startdate;
	}
	public String getEndDate() {
		return enddate;
	}
	public void setEndDate(String enddate) {
		this.enddate = enddate;
	}

}
