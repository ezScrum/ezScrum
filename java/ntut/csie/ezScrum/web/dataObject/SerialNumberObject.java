package ntut.csie.ezScrum.web.dataObject;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.web.databaseEnum.SerialNumberEnum;

/**
 * releaseId, sprintId, storyId, taskId, unplannedId, retrospectiveId
 * 預設為 0, 為當下物件的數量, 若要拿 serialId 必須加 1
 * @author cutecool
 */
public class SerialNumberObject implements IBaseObject {
	private long mId = -1;
	private long mProjectId = -1;
	private long mReleaseId = -1;
	private long mSprintId = -1;
	private long mStoryId = -1;
	private long mTaskId = -1;
	private long mUnplanId = -1;
	private long mRetrospectiveId = -1;
	
	public SerialNumberObject(long projectId) {
		mProjectId = projectId;
	}
	
	public SerialNumberObject(long id, long projectId) {
		mId = id;
		mProjectId = projectId;
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
	
	public SerialNumberObject setUnplanId(long unplanId) {
		mUnplanId = unplanId;
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
	
	public long getUnplanId() {
		return mUnplanId;
	}
	
	public long getRetrospectiveId() {
		return mRetrospectiveId;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject serialNumberJson = new JSONObject();
		serialNumberJson
		    .put(SerialNumberEnum.ID, mId)
			.put(SerialNumberEnum.PROJECT_ID, mProjectId)
			.put(SerialNumberEnum.RELEASE, mReleaseId)
			.put(SerialNumberEnum.SPRINT, mSprintId)
			.put(SerialNumberEnum.STORY, mStoryId)
			.put(SerialNumberEnum.TASK, mTaskId)
			.put(SerialNumberEnum.UNPLAN, mUnplanId)
			.put(SerialNumberEnum.RETROSPECTIVE, mRetrospectiveId);
		return serialNumberJson;
	}
	
	public void save() {
		if (exists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}
	
	private void doCreate() {
		mId = SerialNumberDAO.getInstance().create(this);
		reload();
	}
	
	private void doUpdate() {
		SerialNumberDAO.getInstance().update(this);
	}
	
	public static SerialNumberObject get(long id) {
		return SerialNumberDAO.getInstance().get(id);
	}
	
	public static SerialNumberObject getByProjectId(long projectId) {
		return SerialNumberDAO.getInstance().getByProjectId(projectId);
	}
	
	@Override
	public boolean delete() {
		boolean success = SerialNumberDAO.getInstance().delete(mId);
		if (success) {
			mId = -1;
		}
		return success;
	}
	
	@Override
	public void reload() {
		if (exists()) {
			SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(mId);
			resetData(serialNumber);
		}
	}
	
	@Override
	public boolean exists() {
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(mId);
		return serialNumber != null;
	}
	
	private void resetData(SerialNumberObject serialNumber) {
		mId = serialNumber.getId();
		mProjectId = serialNumber.getProjectId();
		mSprintId = serialNumber.getSprintId();
		mReleaseId = serialNumber.getReleaseId();
		mStoryId = serialNumber.getStoryId();
		mTaskId = serialNumber.getTaskId();
		mUnplanId = serialNumber.getUnplanId();
		mRetrospectiveId = serialNumber.getRetrospectiveId();
	}
}
