package ntut.csie.ezScrum.SaaS.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class ScrumIssueDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long _id;
	@Persistent
	private int _status;
	@Persistent
	private String _type;
	@Persistent
	private String _name;
	@Persistent
	private String _description;
	@Persistent
	private String _projectId;
	@Persistent
	private String _sprintId;
	
	public long getId() {
		return _id;
	}
	public void setId(long id) {
		this._id = id;
	}
	public int getStatus() {
		return _status;
	}
	public void setStatus(int status) {
		this._status = status;
	}
	public String getType() {
		return _type;
	}
	public void setType(String type) {
		this._type = type;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public String getDescription() {
		return _description;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	public String getProjectId() {
		return _projectId;
	}
	public void setProjectId(String projectId) {
		this._projectId = projectId;
	}
	public String getSprintId() {
		return _sprintId;
	}
	public void setSprintId(String sprintId) {
		this._sprintId = sprintId;
	}
}
