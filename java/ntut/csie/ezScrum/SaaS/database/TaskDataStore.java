package ntut.csie.ezScrum.SaaS.database;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class TaskDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
    private String id;
	@Persistent
    private String name;
	@Persistent
	private String estimation;
	@Persistent
    private int statusValue;
	@Persistent
	private String remains;
	@Persistent
	private String handler;
	@Persistent
	private String actualHour;
	@Persistent
	private String notes;
	@Persistent
	private String parentID;
	@Persistent
	private List<String> Historylist;
	@Persistent
	private String projectId;
	@Persistent
	private String partners;
	
	public TaskDataStore(Key key) {
		this.key = key;
		this.Historylist = new ArrayList<String>();
    }
	public Key getKey() {
		return key;
	}
	public void setId(String taskId) {
		this.id = taskId;
	}
	public String getId() {
		return this.id;
	}
	public void setName(String taskName) {
		this.name = taskName;
	}
	public String getName() {
		return this.name;
	}
	public void setEstimation(String estimation) {
		this.estimation = estimation;
	}
	public String getEstimation() {
		return estimation;
	}
	public void setRemains(String remains) {
		this.remains = remains;
	}
	public String getRemains() {
		return remains;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getHandler() {
		return handler;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getNotes() {
		return notes;
	}
	public void setActualHour(String actualHour) {
		this.actualHour = actualHour;
	}
	public String getActualHour() {
		return actualHour;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getParentID() {
		return parentID;
	}
	public List<String> getHistorylist() {
		return Historylist;
	}
	public void setStatusValue(int statusValue) {
		this.statusValue = statusValue;
	}
	public int getStatusValue() {
		return statusValue;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public void setPartners(String partners) {
		this.partners = partners;
	}
	public String getPartners() {
		return this.partners;
	}
}
