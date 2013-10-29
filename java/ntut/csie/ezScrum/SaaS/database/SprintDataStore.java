package ntut.csie.ezScrum.SaaS.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class SprintDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;	
	
	@SuppressWarnings("unused")
	@Persistent
	private ProjectDataStore project;
	
	@Persistent
	private String _id ;
	@Persistent
	private String _goal;
	@Persistent
	private String _interval;
	@Persistent
	private String _memberNumber;
	@Persistent
	private String _factor;
	@Persistent
	private String _availableDays;
	@Persistent
	private String _startDate;
	@Persistent
	private String _demoDate;
	@Persistent
	private String _demoPlace;
	@Persistent
	private String _notes;
	
	public SprintDataStore(Key key) {
		this.key = key;
    }
	
	public Key getKey() {
		return key;
	}

	public void setID(String _id) {
		this._id = _id;
	}

	public String getID() {
		return _id;
	}

	public void setGoal(String _goal) {
		this._goal = _goal;
	}

	public String getGoal() {
		return _goal;
	}

	public void setInterval(String _interval) {
		this._interval = _interval;
	}

	public String getInterval() {
		return _interval;
	}

	public void setMemberNumber(String _memberNumber) {
		this._memberNumber = _memberNumber;
	}

	public String getMemberNumber() {
		return _memberNumber;
	}

	public void setFactor(String _factor) {
		this._factor = _factor;
	}

	public String getFactor() {
		return _factor;
	}

	public void setAvailableDays(String _availableDays) {
		this._availableDays = _availableDays;
	}

	public String getAvailableDays() {
		return _availableDays;
	}

	public void setStartDate(String _startDate) {
		this._startDate = _startDate;
	}

	public String getStartDate() {
		return _startDate;
	}

	public void setDemoDate(String _demoDate) {
		this._demoDate = _demoDate;
	}

	public String getDemoDate() {
		return _demoDate;
	}

	public void setDemoPlace(String _demoPlace) {
		this._demoPlace = _demoPlace;
	}

	public String getDemoPlace() {
		return _demoPlace;
	}

	public void setNotes(String _notes) {
		this._notes = _notes;
	}

	public String getNotes() {
		return _notes;
	}
}
