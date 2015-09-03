package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.dao.RetrospectiveDAO;
import ntut.csie.ezScrum.web.databaseEnum.RetrospectiveEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RetrospectiveObject implements IBaseObject {
	public final static int TYPE_GOOD = 1;
	public final static int TYPE_IMPROVEMENT = 2;
	public final static int STATUS_NEW = 1;
	public final static int STATUS_CLOSED = 2;
	public final static int STATUS_RESOLVED = 3;
	public final static int STATUS_ASSIGNED = 4;
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;
	private long mSprintId = DEFAULT_VALUE;

	private String mName = "";
	private String mDescription = "";
	private int mType = DEFAULT_VALUE;
	private int mStatus = DEFAULT_VALUE;

	private long mCreateTime = 0;
	private long mUpdateTime = 0;

	public RetrospectiveObject(long projectId) {
		mProjectId = projectId;
	}

	public RetrospectiveObject(long id, Long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}
	
	public static RetrospectiveObject get(long retrospectiveId) {
		return RetrospectiveDAO.getInstance().get(retrospectiveId);
	}

	public RetrospectiveObject setName(String name) {
		mName = name;
		return this;
	}

	public RetrospectiveObject setDescription(String description) {
		mDescription = description;
		return this;
	}
	
	public RetrospectiveObject setTypeString(String typeString) {
		if (typeString.equals("Good")) {
			mType = TYPE_GOOD;
		} else {
			mType = TYPE_IMPROVEMENT;
		}
		return this;
	}
	
	public RetrospectiveObject setStatusString(String statusString) {
		if (statusString.equals("new")) {
			mStatus = STATUS_NEW;
		} else if (statusString.equals("assigned")) {
			mStatus = STATUS_ASSIGNED;
		} else if (statusString.equals("resolved")) {
			mStatus = STATUS_RESOLVED;
		} else {
			mStatus = STATUS_CLOSED;
		}
		return this;
	}

	public RetrospectiveObject setType(int type) {
		mType = type;
		return this;
	}

	public RetrospectiveObject setStatus(int status) {
		mStatus = status;
		return this;
	}

	public RetrospectiveObject setSprintId(long sprintId) {
		mSprintId = sprintId;
		return this;
	}

	public RetrospectiveObject setProjectId(Long projectId) {
		mProjectId = projectId;
		return this;
	}

	public RetrospectiveObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public RetrospectiveObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
	}

	public long getId() {
		return mId;
	}

	public long getSerialId() {
		return mSerialId;
	}

	public long getProjectId() {
		return mProjectId;
	}

	public String getName() {
		return mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public int getType() {
		return mType;
	}
	
	public String getTypeString() {
		if (mType == TYPE_GOOD) {
			return "Good";
		} else {
			return "Improvement";
		}
	}

	public int getStatus() {
		return mStatus;
	}
	
	public String getStatusString() {
		if (mStatus == STATUS_NEW) {
			return "new";
		} else if (mStatus == STATUS_ASSIGNED) {
			return "assigned";
		} else if (mStatus == STATUS_RESOLVED) {
			return "resolved";
		} else {
			return "closed";
		}
	}
	
	public static int getTypeByTypeString(String typeString) {
		if (typeString.equals("Good")) {
			return TYPE_GOOD;
		} else {
			return TYPE_IMPROVEMENT;
		}
	}
	
	public static int getStatusByStatusString(String statusString) {
		if (statusString.equals("new")) {
			return STATUS_NEW;
		} else if (statusString.equals("assigned")) {
			return STATUS_ASSIGNED;
		} else if (statusString.equals("resolved")) {
			return STATUS_RESOLVED;
		} else {
			return STATUS_CLOSED;
		}
	}

	public long getSprintId() {
		return mSprintId;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	@Override
	public void save() {
		if (exists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			RetrospectiveObject sprint = RetrospectiveDAO.getInstance().get(mId);
			resetData(sprint);
		}
	}

	@Override
	public boolean delete() {
		boolean success = RetrospectiveDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
			mProjectId = DEFAULT_VALUE;
		}
		return success;
	}

	private void doCreate() {
		mId = RetrospectiveDAO.getInstance().create(this);
		reload();
	}

	private void doUpdate() {
		mUpdateTime = System.currentTimeMillis();
		RetrospectiveDAO.getInstance().update(this);
	}

	private void resetData(RetrospectiveObject retrospective) {
		mId = retrospective.getId();
		mProjectId = retrospective.getProjectId();
		mSerialId = retrospective.getSerialId();

		setName(retrospective.getName());
		setDescription(retrospective.getDescription());
		setType(retrospective.getType());
		setStatus(retrospective.getStatus());
		setSprintId(retrospective.getSprintId());
		setCreateTime(retrospective.getCreateTime());
		setUpdateTime(retrospective.getUpdateTime());
	}

	private boolean exists() {
		RetrospectiveObject retrospective = RetrospectiveDAO.getInstance().get(mId);
		return retrospective != null;
	}
	
	public String toString() {
		try {
			return toJSON().toString();			
		} catch(JSONException e) {
			return "JSON Exception";
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject retrspectiveJson = new JSONObject();
		retrspectiveJson.put(RetrospectiveEnum.ID, mId)
		                .put(RetrospectiveEnum.SERIAL_ID, mSerialId)
		                .put(RetrospectiveEnum.NAME, mName)
		                .put(RetrospectiveEnum.DESCRIPTION, mDescription)
		                .put(RetrospectiveEnum.TYPE, mType)
		                .put(RetrospectiveEnum.STATUS, mStatus)
		                .put(RetrospectiveEnum.SPRINT_ID, mSprintId)
		                .put(RetrospectiveEnum.PROJECT_ID, mProjectId)
		                .put(RetrospectiveEnum.CREATE_TIME, mCreateTime)
		                .put(RetrospectiveEnum.UPDATE_TIME, mUpdateTime);
		return retrspectiveJson;
	}

}
