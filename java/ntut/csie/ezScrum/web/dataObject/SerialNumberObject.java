package ntut.csie.ezScrum.web.dataObject;

import java.sql.SQLException;

import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.web.databasEnum.SerialNumberEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * releaseId, sprintId, storyId, taskId, unplannedId, retrospectiveId
 * 預設為 0, 為當下物件的數量, 若要拿 serialId 必須加 1
 * @author cutecool
 */
public class SerialNumberObject {
	private long mId = -1;
	private long mProjectId = -1;
	private long mReleaseId = -1;
	private long mSprintId = -1;
	private long mStoryId = -1;
	private long mTaskId = -1;
	private long mUnplannedId = -1;
	private long mRetrospectiveId = -1;
	
	public SerialNumberObject() {}
	
	public SerialNumberObject(long projectId, long releaseId, long sprintId,
			long storyId, long taskId, long unplannedId, long retrospectiveId) {
		mProjectId = projectId;
		mReleaseId = releaseId;
		mSprintId = sprintId;
		mStoryId = storyId;
		mTaskId = taskId;
		mUnplannedId = unplannedId;
		mRetrospectiveId = retrospectiveId;
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
		mReleaseId = releaseId;
		return this;
	}
	
	public SerialNumberObject setSprintId(long sprintId) {
		mSprintId = sprintId;
		return this;
	}
	
	public SerialNumberObject setStoryId(long storyId) {
		mStoryId = storyId;
		return this;
	}
	
	public SerialNumberObject setTaskId(long taskId) {
		mTaskId = taskId;
		return this;
	}
	
	public SerialNumberObject setUnplannedId(long unplannedId) {
		mUnplannedId = unplannedId;
		return this;
	}
	
	public SerialNumberObject setRetrospectiveId(long retrospectiveId) {
		mRetrospectiveId = retrospectiveId;
		return this;
	}
	
	public long getId() {
		return mId;
	}
	
	public long getProjectId() {
		return mProjectId;
	}
	
	public long getReleaseId() {
		return mReleaseId;
	}
	
	public long getSprintId() {
		return mSprintId;
	}
	
	public long getStoryId() {
		return mStoryId;
	}
	
	public long getTaskId() {
		return mTaskId;
	}
	
	public long getUnplannedId() {
		return mUnplannedId;
	}
	
	public long getRetrospectiveId() {
		return mRetrospectiveId;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object
			.put(SerialNumberEnum.PROJECT_ID, mProjectId)
			.put(SerialNumberEnum.RELEASE, mReleaseId)
			.put(SerialNumberEnum.SPRINT, mSprintId)
			.put(SerialNumberEnum.STORY, mStoryId)
			.put(SerialNumberEnum.TASK, mTaskId)
			.put(SerialNumberEnum.UNPLANNED, mUnplannedId)
			.put(SerialNumberEnum.RETROSPECTIVE, mRetrospectiveId);
		return object;
	}
	
	public void save() {
		if (exists()) {
			SerialNumberDAO.getInstance().update(this);
		} else {
			mId = SerialNumberDAO.getInstance().create(this);
		}
	}
	
	/**
	 * Using projectId to get serial number
	 * @param projectId
	 * @throws SQLException
	 */
	public static SerialNumberObject get(long projectId) throws SQLException {
		return SerialNumberDAO.getInstance().get(projectId);
	}
	
	public void reload() throws Exception {
		if (exists()) {
			SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(mProjectId);
			resetData(serialNumber);
		} else {
			throw new Exception("Record not exists");
		}
	}
	
	public boolean dalete() {
		boolean success = SerialNumberDAO.getInstance().delete(mId);
		if (success) {
			mId = -1;
		}
		return success;
	}
	
	private boolean exists() {
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(mId);
		return serialNumber != null;
	}
	
	private void resetData(SerialNumberObject serialNumber) {
		mId = serialNumber.getId();
		mProjectId = serialNumber.getProjectId();
		mReleaseId = serialNumber.getReleaseId();
		mStoryId = serialNumber.getStoryId();
		mTaskId = serialNumber.getTaskId();
		mUnplannedId = serialNumber.getUnplannedId();
		mRetrospectiveId = serialNumber.getRetrospectiveId();
	}
}
