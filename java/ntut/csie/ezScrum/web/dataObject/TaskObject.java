package ntut.csie.ezScrum.web.dataObject;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TaskObject implements IBaseObject {
	public final static int STATUS_UNCHECK = 1;
	public final static int STATUS_CHECK = 2;
	public final static int STATUS_DONE = 3;
	public final static int WILD = -1;

	private long mId = -1;
	private long mSerialId = -1;
	private long mProjectId = -1;
	private long mStoryId = -1;
	private long mHandlerId = -1;
	private String mName = "";
	private String mNotes = "";
	private int mEstimate = 0;
	private int mRemains = 0;
	private int mActual = 0;
	private int mStatus = STATUS_UNCHECK;
	private long mCreateTime = -1;
	private long mUpdateTime = -1;
	private ArrayList<Long> mPartnersId = null;
	private ArrayList<AccountObject> mPartners = null;
	private ArrayList<AttachFileObject> mAttachFiles = null;
	private ArrayList<HistoryObject> mHistories = null;

	public static TaskObject get(long id) throws SQLException {
		return TaskDAO.getInstance().get(id);
	}

	public TaskObject() {
	}

	public TaskObject(String name) {
		mName = name;
	}

	public TaskObject(long id, long serialId) {
		mId = id;
		mSerialId = serialId;
	}

	public TaskObject setName(String name) {
		mName = name;
		return this;
	}

	public TaskObject setHandlerId(long handlerId) {
		mHandlerId = handlerId;
		return this;
	}

	public TaskObject setEstimate(int estimate) {
		mEstimate = estimate;
		return this;
	}

	public TaskObject setRemains(int remains) {
		mRemains = remains;
		return this;
	}

	public TaskObject setActual(int actual) {
		mActual = actual;
		return this;
	}

	public TaskObject setNotes(String notes) {
		mNotes = notes;
		return this;
	}

	public TaskObject setStatus(int status) {
		mStatus = status;
		return this;
	}

	public TaskObject setProjectId(long projectId) {
		mProjectId = projectId;
		return this;
	}

	public TaskObject setStoryId(long storyId) {
		mStoryId = storyId;
		return this;
	}

	public TaskObject setCreateTime(long createtime) {
		mCreateTime = createtime;
		return this;
	}

	public TaskObject setUpdateTime(long updatetime) {
		mUpdateTime = updatetime;
		return this;
	}

	public TaskObject setPartnersId(ArrayList<Long> partnersId) {
		mPartnersId = partnersId;
		return this;
	}

	public TaskObject setAttachFiles(ArrayList<AttachFileObject> attachFiles) {
		mAttachFiles = attachFiles;
		return this;
	}

	public TaskObject setHistories(ArrayList<HistoryObject> histories) {
		mHistories = histories;
		return this;
	}

	public void addPartner(long partnerId) {
		this.mPartnersId.add(partnerId);
	}

	public void addAttachFile(AttachFileObject attachFile) {
		this.mAttachFiles.add(attachFile);
	}

	public void addHistory(HistoryObject history) {
		this.mHistories.add(history);
	}

	public void removePartner(long partnerId) {
		this.mPartnersId.remove(partnerId);
	}

	public long getId() {
		return mId;
	}

	public long getSerialId() {
		return mSerialId;
	}

	public String getName() {
		return mName;
	}

	public long getHandlerId() {
		return mHandlerId;
	}
	
	public AccountObject getHandler() {
		if (mHandlerId > 0) {
			try {
				return AccountDAO.getInstance().get(mHandlerId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public int getEstimate() {
		return mEstimate;
	}

	public int getRemains() {
		return mRemains;
	}

	public int getActual() {
		return mActual;
	}

	public String getNotes() {
		return mNotes;
	}

	public int getStatus() {
		return mStatus;
	}

	// public int getStatus(Date date) {
	// long time = date.getTime();
	// time += 1000 * 60 * 60 * 24 - 1;
	// date = new Date(time);
	//
	// int status = TaskObject.STATUS_UNCHECK;
	// for (HistoryObject history : mHistories) {
	// if (history.getHistoryType() == HistoryObject.TYPE_STATUS
	// && (new Date(history.getModifiedTime()).before(date))) {
	// if (history.getNewValue().equals("\"Checked Out\"")) {
	// status = TaskObject.STATUS_CHECK;
	// } else if (history.getNewValue().equals("\"Done\"")) {
	// status = TaskObject.STATUS_DONE;
	// }
	// }
	// }
	// return status;
	// }
	//
	// public int getRemainsByDate(Date date) {
	// return searchValue(HistoryObject.TYPE_REMAIMS, date);
	// }
	//
	// public int getEstimateByDate(Date date) {
	// return searchValue(HistoryObject.TYPE_ESTIMATE, date);
	// }
	//
	// private int searchValue(int searchType, Date date) {
	// int value = -1;
	// for (HistoryObject history : mHistories) {
	// if (history.getHistoryType() == searchType
	// && (new Date(history.getModifiedTime()).before(date))) {
	// value = Integer.parseInt(history.getNewValue());
	// }
	// }
	// return value;
	// }
	//
	// public int getDateStatus(Date date) {
	// int status = -1;
	// for (HistoryObject history : mHistories) {
	// if (history.getHistoryType() == HistoryObject.TYPE_STATUS) {
	// status = Integer.parseInt(history.getOldValue());
	// }
	// }
	// return status;
	// }
	//
	// public long getAssignedTime() {
	// long assignedTime = 0;
	// for (HistoryObject history : mHistories) {
	// if (history.getHistoryType() == HistoryObject.TYPE_STATUS
	// && history.getNewValue().equals("\"Checked Out\"")) {
	// if (assignedTime < history.getModifiedTime()) {
	// assignedTime = history.getModifiedTime();
	// }
	// }
	// }
	// return assignedTime;
	// }
	//
	// public long getDoneTime() {
	// long doneTime = 0;
	// for (HistoryObject history : mHistories) {
	// if (history.getHistoryType() == HistoryObject.TYPE_STATUS
	// && history.getNewValue().equals("\"Done\"")) {
	// if (doneTime < history.getModifiedTime()) {
	// doneTime = history.getModifiedTime();
	// }
	// }
	// }
	// return doneTime;
	// }

	public long getProjectId() {
		return mProjectId;
	}

	public long getStoryId() {
		return mStoryId;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	public ArrayList<Long> getPartnersId() {
		if (mPartnersId == null) {
			try {
				mPartnersId = TaskDAO.getInstance().getPartnersId(mId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return mPartnersId;
	}

	public ArrayList<AccountObject> getPartners() {
		if (mPartners == null) {
			mPartners = new ArrayList<AccountObject>();
			for (long partnerId : mPartnersId) {
				try {
					AccountObject partner = AccountDAO.getInstance().get(
							partnerId);
					mPartners.add(partner);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return mPartners;
	}

	public ArrayList<HistoryObject> getHistories() {
		if (mHistories == null) {
			try {
				mHistories = HistoryDAO.getInstance().getHistoriesByIssue(mId,
						IssueTypeEnum.TYPE_TASK);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return mHistories;
	}

	public ArrayList<AttachFileObject> getAttachFiles() {
		if (mAttachFiles == null) {
			mAttachFiles = AttachFileDAO.getInstance().getAttachFilesByTaskId(
					mId);
		}
		return mAttachFiles;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject task = new JSONObject();
		JSONArray partners = new JSONArray();

		for (Long partnerId : mPartnersId) {
			partners.put(partnerId);
		}

		task.put(TaskEnum.NAME, mName).put(TaskEnum.ESTIMATE, mEstimate)
				.put(TaskEnum.ACTUAL, mActual).put(TaskEnum.STORY_ID, mStoryId)
				.put(TaskEnum.PROJECT_ID, mProjectId)
				.put(TaskEnum.NOTES, mNotes).put(TaskEnum.REMAIN, mRemains)
				.put(TaskEnum.STATUS, mStatus)
				.put(TaskEnum.SERIAL_ID, mSerialId).put(TaskEnum.ID, mId)
				.put(TaskEnum.CREATE_TIME, mCreateTime)
				.put(TaskEnum.UPDATE_TIME, mUpdateTime)
				.put("partners", partners);

		return task;
	}

	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	public void checkOut() {
		mStatus = TaskObject.STATUS_CHECK;
		save();
	}

	public void done() {
		mStatus = TaskObject.STATUS_DONE;
		save();
	}

	public void renew() {
		mStatus = TaskObject.STATUS_UNCHECK;
		save();
	}

	@Override
	public void save() {
		if (recordExists()) {
			doUpdate();			
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() throws Exception {
		if (recordExists()) {
			try {
				TaskObject task = TaskDAO.getInstance().get(mId);
				resetData(task);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			throw new Exception("Task does not exist");
		}
	}

	@Override
	public boolean delete() {
		boolean success = TaskDAO.getInstance().delete(mId);
		if (success) {
			mId = -1;
			mSerialId = -1;
		}
		return success;
	}

	private boolean recordExists() {
		return mId > 0;
	}

	private void resetData(TaskObject task) {
		mId = task.getId();
		mSerialId = task.getSerialId();
		setName(task.getName());
		setNotes(task.getNotes());
		setProjectId(task.getProjectId());
		setStoryId(task.getStoryId());
		setEstimate(task.getEstimate());
		setRemains(task.getRemains());
		setActual(task.getActual());
		setHandlerId(task.getHandlerId());
		setStatus(task.getStatus());
		setCreateTime(task.getCreateTime());
		setUpdateTime(task.getUpdateTime());
		mHistories = null;
		mPartnersId = null;
		mAttachFiles = null;
	}

	private void doCreate() {
		mId = TaskDAO.getInstance().create(this);
		try {
			reload();

			HistoryDAO historyDao = HistoryDAO.getInstance();
			historyDao.create(new HistoryObject(mId, IssueTypeEnum.TYPE_TASK,
					HistoryObject.TYPE_CREATE, "", "", mCreateTime));

			// add task relation history
			if (mStoryId > 0) {
				addHistoryOfAddRelation(mStoryId, mId);
			}
		} catch (Exception e) {
		}
	}

	private void doUpdate() {
		try {
			TaskObject oldTask = TaskObject.get(mId);
			TaskDAO.getInstance().update(this);

			if (!mName.equals(oldTask.getName())) {
				addHistory(HistoryObject.TYPE_NAME, oldTask.getName(), mName);
			}
			if (!mNotes.equals(oldTask.getNotes())) {
				addHistory(HistoryObject.TYPE_NOTE, oldTask.getNotes(), mNotes);
			}
			if (mEstimate != oldTask.getEstimate()) {
				addHistory(HistoryObject.TYPE_ESTIMATE, oldTask.getEstimate(),
						mEstimate);
			}
			if (mActual != oldTask.getActual()) {
				addHistory(HistoryObject.TYPE_ACTUAL, oldTask.getActual(),
						mActual);
			}
			if (mRemains != oldTask.getRemains()) {
				addHistory(HistoryObject.TYPE_REMAIMS, oldTask.getRemains(),
						mRemains);
			}
			if (mStatus != oldTask.getStatus()) {
				addHistory(HistoryObject.TYPE_STATUS, oldTask.getStatus(),
						mStatus);
			}
			if (mStoryId != oldTask.getStoryId()) {
				if (mStoryId <= 0 && oldTask.getStoryId() > 0) {
					addHistoryOfAddRelation(mStoryId, mId);
				} else if (mStoryId > 0 && oldTask.getStoryId() <= 0) {
					addHistoryOfRemoveRelation(mStoryId, mId);
				}
			}
			if (mHandlerId != oldTask.getHandlerId()) {
				addHistory(HistoryObject.TYPE_HANDLER, oldTask.getHandlerId(),
						mHandlerId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addHistoryOfAddRelation(long storyId, long taskId) {
		addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mStoryId));
		HistoryObject history = new HistoryObject(mStoryId,
				IssueTypeEnum.TYPE_STORY, HistoryObject.TYPE_ADD, "",
				String.valueOf(mId), System.currentTimeMillis());
		HistoryDAO.getInstance().create(history);
	}

	private void addHistoryOfRemoveRelation(long storyId, long taskId) {
		addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(mStoryId));
		HistoryObject history = new HistoryObject(mStoryId,
				IssueTypeEnum.TYPE_STORY, HistoryObject.TYPE_DROP, "",
				String.valueOf(mId), System.currentTimeMillis());
		HistoryDAO.getInstance().create(history);
	}

	private void addHistory(int type, long oldValue, long newValue) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue));
	}

	private void addHistory(int type, String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_TASK,
				type, oldValue, newValue, System.currentTimeMillis());
		HistoryDAO.getInstance().create(history);
	}
}
