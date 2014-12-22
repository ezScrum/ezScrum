package ntut.csie.ezScrum.web.dataObject;

import java.sql.SQLException;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.web.databasEnum.SerialNumberEnum;

public class SerialNumberObject {
	private long mId = -1;
	private long mProjectId = -1;
	private HashMap<String, Long> mIdMap = new HashMap<String, Long>();
	
	public SerialNumberObject() {}
	
	public SerialNumberObject(long projectId, long releaseId, long sprintId,
			long storyId, long taskId, long unplannedId, long retrospectiveId) {
		setProjectId(projectId);
		mIdMap.put(SerialNumberEnum.RELEASE, releaseId);
		mIdMap.put(SerialNumberEnum.SPRINT, sprintId);
		mIdMap.put(SerialNumberEnum.STORY, storyId);
		mIdMap.put(SerialNumberEnum.TASK, taskId);
		mIdMap.put(SerialNumberEnum.UNPLANNED, unplannedId);
		mIdMap.put(SerialNumberEnum.RETROSPECTIVE, retrospectiveId);
	}
	
	public SerialNumberObject setId(long id) {
		mId = id;
		return this;
	}
	
	public SerialNumberObject setProjectId(long projectId) {
		mProjectId = projectId;
		return this;
	}
	
	public SerialNumberObject setReleaseId(long releaseId) {
		setIdMap(SerialNumberEnum.RELEASE, releaseId);
		return this;
	}
	
	public SerialNumberObject setSprintId(long sprintId) {
		setIdMap(SerialNumberEnum.SPRINT, sprintId);
		return this;
	}
	
	public SerialNumberObject setStoryId(long storyId) {
		setIdMap(SerialNumberEnum.STORY, storyId);
		return this;
	}
	
	public SerialNumberObject setTaskId(long taskId) {
		setIdMap(SerialNumberEnum.TASK, taskId);
		return this;
	}
	
	public SerialNumberObject setUnplannedId(long unplannedId) {
		setIdMap(SerialNumberEnum.UNPLANNED, unplannedId);
		return this;
	}
	
	public SerialNumberObject setRetrospectiveId(long retrospectiveId) {
		setIdMap(SerialNumberEnum.RETROSPECTIVE, retrospectiveId);
		return this;
	}
	
	public SerialNumberObject setId(String key, long id) {
		setIdMap(key, id);
		return this;
	}
	
	public long getProjectId() {
		return mProjectId;
	}
	
	public long getReleaseId() {
		return mIdMap.get(SerialNumberEnum.RELEASE);
	}
	
	public long getSprintId() {
		return mIdMap.get(SerialNumberEnum.SPRINT);
	}
	
	public long getStoryId() {
		return mIdMap.get(SerialNumberEnum.STORY);
	}
	
	public long getTaskId() {
		return mIdMap.get(SerialNumberEnum.TASK);
	}
	
	public long getUnplannedId() {
		return mIdMap.get(SerialNumberEnum.UNPLANNED);
	}
	
	public long getRetrospectiveId() {
		return mIdMap.get(SerialNumberEnum.RETROSPECTIVE);
	}
	
	public long getId(String type) {
		return mIdMap.get(type);
	}
	
	private void setIdMap(String type, long id) {
		mIdMap.put(type, id);
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object
			.put(SerialNumberEnum.PROJECT_ID, mProjectId)
			.put(SerialNumberEnum.RELEASE, getId(SerialNumberEnum.RELEASE))
			.put(SerialNumberEnum.SPRINT, getId(SerialNumberEnum.SPRINT))
			.put(SerialNumberEnum.STORY, getId(SerialNumberEnum.STORY))
			.put(SerialNumberEnum.TASK, getId(SerialNumberEnum.TASK))
			.put(SerialNumberEnum.UNPLANNED, getId(SerialNumberEnum.UNPLANNED))
			.put(SerialNumberEnum.RETROSPECTIVE, getId(SerialNumberEnum.RETROSPECTIVE));
		return object;
	}
	
	public void save() {
		if (isDataExists()) {
			SerialNumberDAO.getInstance().create(this);
		} else {
			SerialNumberDAO.getInstance().update(this);
		}
	}
	
	public void reload() {
		if (isDataExists()) {
			try {
				SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(mProjectId);
				updateData(serialNumber);
			} catch (SQLException e) {
				
			}
		}
	}
	
	private boolean isDataExists() {
		if (mId > 0) {
			return true;
		}
		return false;
	}
	
	private void updateData(SerialNumberObject serialNumber) {
		setProjectId(serialNumber.getProjectId());
		setIdMap(SerialNumberEnum.RELEASE, serialNumber.getReleaseId());
	}
}
