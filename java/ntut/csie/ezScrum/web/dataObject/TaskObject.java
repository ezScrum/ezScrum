package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.StatusEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;

public class TaskObject implements IBaseObject {
	public final static int STATUS_UNCHECK = StatusEnum.NEW;
	public final static int STATUS_CHECK = StatusEnum.ASSIGNED;
	public final static int STATUS_DONE = StatusEnum.CLOSED;
	public final static int NO_PARENT = -1;
	private final static int DEFAULT_VALUE = -1;
	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;
	private long mStoryId = DEFAULT_VALUE;
	private long mHandlerId = DEFAULT_VALUE;
	private ArrayList<Long> mPartnersId = new ArrayList<>();
	private String mName = "";
	private String mNotes = "";
	private int mEstimate = 0;
	private int mRemains = 0;
	private int mActual = 0;
	private int mStatus = STATUS_UNCHECK;
	private long mCreateTime = DEFAULT_VALUE;
	private long mUpdateTime = DEFAULT_VALUE;

	public static TaskObject get(long id) {
		return TaskDAO.getInstance().get(id);
	}

	public TaskObject(long projectId) {
		mProjectId = projectId;
	}

	public TaskObject(long id, long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
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

	public TaskObject setPartnersId(ArrayList<Long> newPartnersId) {
		mPartnersId = newPartnersId;
		return this;
	}

	public void addPartner(long partnerId) {
		AccountObject partner = AccountObject.get(partnerId);
		if (partner != null && !TaskDAO.getInstance().partnerExists(mId, partnerId)) {
			TaskDAO.getInstance().addPartner(mId, partnerId);
		}
	}

	public void removePartner(long partnerId) {
		TaskDAO.getInstance().removePartner(mId, partnerId);
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
		AccountObject handler = AccountDAO.getInstance().get(mHandlerId);
		return handler;
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

	public int getStatus(Date date) {
		long lastSecondOfTheDate = getLastMillisecondOfDate(date);
		int status = STATUS_UNCHECK;
		ArrayList<HistoryObject> histories = getHistories();
		for (HistoryObject history : histories) {
			long historyTime = history.getCreateTime();
			int historyType = history.getHistoryType();
			if (historyType == HistoryObject.TYPE_STATUS
					&& historyTime <= lastSecondOfTheDate) {
				String statusInHistory = history.getNewValue();
				if (statusInHistory.equals(String.valueOf(STATUS_CHECK))) {
					status = STATUS_CHECK;
				} else if (history.getNewValue().equals(
						String.valueOf(STATUS_DONE))) {
					status = STATUS_DONE;
				} else if (history.getNewValue().equals(
						String.valueOf(STATUS_UNCHECK))) {
					status = STATUS_UNCHECK;
				}
			}
		}
		return status;
	}

	/*
	 * 之後要拔掉,為了符合目前的IIssue
	 */
	public String getStatusString() {
		if (mStatus == STATUS_UNCHECK) {
			return "new";
		} else if (mStatus == STATUS_CHECK) {
			return "assigned";
		} else {
			return "closed";
		}
	}

	public long getDoneTime() {
		long doneTime = 0;
		ArrayList<HistoryObject> histories = getHistories();
		for (HistoryObject history : histories) {
			long historyTime = history.getCreateTime();
			if (history.getHistoryType() == HistoryObject.TYPE_STATUS
					&& history.getNewValue()
							.equals(String.valueOf(STATUS_DONE))) {
				if (doneTime < historyTime) {
					doneTime = historyTime;
				}
			}
		}
		return doneTime;
	}

	private long getLastMillisecondOfDate(Date date) {
		if (date == null) {
			date = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,
				calendar.getMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND,
				calendar.getMaximum(Calendar.MILLISECOND));
		Date endOfDate = calendar.getTime();
		long lastSecondOfDate = endOfDate.getTime();
		return lastSecondOfDate;
	}

	public double getRemains(Date date) {
		long lastSecondOfTheDate = getLastMillisecondOfDate(date);
		double remains = mRemains;
		ArrayList<HistoryObject> histories = getHistories();
		ArrayList<HistoryObject> remainsHistories = new ArrayList<HistoryObject>();
		for (HistoryObject history : histories) {
			if (history.getHistoryType() == HistoryObject.TYPE_REMAIMS) {
				remainsHistories.add(history);
			}
		}
		if (remainsHistories.size() > 0) {
			String firstRemainsOldValue = remainsHistories.get(0).getOldValue();
			remains = Double.parseDouble(firstRemainsOldValue); // Because
																// create task
																// no remains
																// history, get
																// first remains
																// history old
																// value
		}
		for (HistoryObject remainsHistory : remainsHistories) {
			if (remainsHistory.getCreateTime() <= lastSecondOfTheDate) {
				String remainsInHistory = remainsHistory.getNewValue();
				remains = Double.parseDouble(remainsInHistory);
			}
		}
		return remains;
	}

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
		ArrayList<Long> partnersId = TaskDAO.getInstance().getPartnersId(mId);
		return partnersId;
	}

	public ArrayList<AccountObject> getPartners() {
		ArrayList<AccountObject> partners = new ArrayList<AccountObject>();
		ArrayList<Long> partnersId = getPartnersId();
		for (long partnerId : partnersId) {
			AccountObject partner = AccountDAO.getInstance().get(partnerId);
			if (partner != null) {
				partners.add(partner);
			}
		}
		return partners;
	}

	public String getPartnersUsername() {
		StringBuilder partnersName = new StringBuilder();
		ArrayList<AccountObject> partners = getPartners();
		int partnersAmount = partners.size();
		int lastIndex = partnersAmount - 1;
		for (int i = 0; i < partnersAmount; i++) {
			partnersName.append(partners.get(i).getUsername());
			if (i != lastIndex) {
				partnersName.append(";");
			}
		}
		return partnersName.toString();
	}

	public ArrayList<HistoryObject> getHistories() {
		ArrayList<HistoryObject> histories = HistoryDAO.getInstance()
				.getHistoriesByIssue(mId, IssueTypeEnum.TYPE_TASK);
		return histories;
	}

	public ArrayList<AttachFileObject> getAttachFiles() {
		ArrayList<AttachFileObject> attachFiles = AttachFileDAO.getInstance()
				.getAttachFilesByTaskId(mId);
		return attachFiles;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject taskJson = new JSONObject();
		JSONArray partnerJsonArray = new JSONArray();
		JSONArray attachFileJsonArray = new JSONArray();
		JSONArray historyJsonArray = new JSONArray();

		for (AccountObject partner : getPartners()) {
			partnerJsonArray.put(partner.toJSON());
		}

		for (AttachFileObject file : getAttachFiles()) {
			attachFileJsonArray.put(file.toJSON());
		}

		for (HistoryObject history : getHistories()) {
			historyJsonArray.put(history.toJSON());
		}
		
		taskJson.put(TaskEnum.NAME, mName).put(TaskEnum.ESTIMATE, mEstimate)
				.put(TaskEnum.ACTUAL, mActual).put(TaskEnum.STORY_ID, mStoryId)
				.put(TaskEnum.PROJECT_ID, mProjectId)
				.put(TaskEnum.NOTES, mNotes).put(TaskEnum.REMAIN, mRemains)
				.put(TaskEnum.STATUS, mStatus)
				.put(TaskEnum.SERIAL_ID, mSerialId).put(TaskEnum.ID, mId)
				.put(TaskEnum.CREATE_TIME, mCreateTime)
				.put(TaskEnum.UPDATE_TIME, mUpdateTime)
				.put("partners", partnerJsonArray).put("attach_files", attachFileJsonArray)
				.put("histories", historyJsonArray);

		if (getHandler() != null) {
			taskJson.put(TaskEnum.HANDLER, getHandler().toJSON());
		} else {
			taskJson.put(TaskEnum.HANDLER, new JSONObject());
		}
		
		return taskJson;
	}

	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	@Override
	public void save() {
		if (exists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}

	/**
	 * Update time will equal to parameter you passed.
	 * 
	 * @param specificTime
	 */
	public void save(long specificTime) {
		if (exists()) {
			doUpdate(specificTime);
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			TaskObject task = TaskDAO.getInstance().get(mId);
			resetData(task);
		}
	}

	@Override
	public boolean delete() {
		HistoryDAO.getInstance().deleteByIssue(mId, IssueTypeEnum.TYPE_TASK);
		boolean success = TaskDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
			mProjectId = DEFAULT_VALUE;
		}
		return success;
	}

	public boolean exists() {
		TaskObject task = TaskDAO.getInstance().get(mId);
		return task != null;
	}

	private void resetData(TaskObject task) {
		mId = task.getId();
		mSerialId = task.getSerialId();
		mProjectId = task.getProjectId();
		setName(task.getName());
		setNotes(task.getNotes());
		setStoryId(task.getStoryId());
		setEstimate(task.getEstimate());
		setRemains(task.getRemains());
		setActual(task.getActual());
		setHandlerId(task.getHandlerId());
		setPartnersId(task.getPartnersId());
		setStatus(task.getStatus());
		setCreateTime(task.getCreateTime());
		setUpdateTime(task.getUpdateTime());
	}

	private void doCreate() {
		// default remains hour should equal to estimate
		mRemains = mEstimate;
		mId = TaskDAO.getInstance().create(this);
		for (long partnerId : mPartnersId) {
			addPartner(partnerId);
		}
		// 為了拿到 update time 來新增 history, 所以需要 reload 一次從 DB 拿回時間
		reload();
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_TASK,
				HistoryObject.TYPE_CREATE, "", "", mCreateTime);
		history.save();
		// add task relation history
		if (mStoryId > 0) {
			addHistoryOfAddRelation(mStoryId, mId);
		}
	}

	private void doUpdate() {
		mUpdateTime = System.currentTimeMillis();
		TaskObject oldTask = TaskObject.get(mId);
		TaskDAO.getInstance().update(this);
		
		if (!mName.equals(oldTask.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldTask.getName(), mName, mUpdateTime);
		}
		if (!mNotes.equals(oldTask.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldTask.getNotes(), mNotes, mUpdateTime);
		}
		if (mStatus != oldTask.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldTask.getStatus(), mStatus, mUpdateTime);
		}
		if (mEstimate != oldTask.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldTask.getEstimate(),
					mEstimate, mUpdateTime);
		}
		if (mActual != oldTask.getActual()) {
			addHistory(HistoryObject.TYPE_ACTUAL, oldTask.getActual(), mActual, mUpdateTime);
		}
		if (mRemains != oldTask.getRemains()) {
			addHistory(HistoryObject.TYPE_REMAIMS, oldTask.getRemains(),
					mRemains, mUpdateTime);
		}
		if (mStoryId != oldTask.getStoryId()) {
			// task drop from story
			if (mStoryId <= 0 && oldTask.getStoryId() > 0) {
				addHistoryOfTaskRemoveFromStory(oldTask.getStoryId(), mId, mUpdateTime); // for task
				addHistoryOfStoryDropTask(oldTask.getStoryId(), mId, mUpdateTime); // for
																		// story
			}
			// task append to story
			else if (mStoryId > 0 && oldTask.getStoryId() <= 0) {
				addHistoryOfAddRelation(mStoryId, mId, mUpdateTime);
			}
		}
		if (mHandlerId != oldTask.getHandlerId()) {
			addHistory(HistoryObject.TYPE_HANDLER, oldTask.getHandlerId(),
					mHandlerId, mUpdateTime);
		}
		
		setPartnersAndHistory(mUpdateTime);
	}

	// for specific time update
	private void doUpdate(long specificTime) {
		mUpdateTime = specificTime;
		TaskObject oldTask = TaskObject.get(mId);
		TaskDAO.getInstance().update(this);
		
		if (!mName.equals(oldTask.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldTask.getName(), mName,
					mUpdateTime);
		}
		if (!mNotes.equals(oldTask.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldTask.getNotes(), mNotes,
					mUpdateTime);
		}
		if (mEstimate != oldTask.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldTask.getEstimate(),
					mEstimate, mUpdateTime);
		}
		if (mActual != oldTask.getActual()) {
			addHistory(HistoryObject.TYPE_ACTUAL, oldTask.getActual(), mActual,
					mUpdateTime);
		}
		if (mRemains != oldTask.getRemains()) {
			addHistory(HistoryObject.TYPE_REMAIMS, oldTask.getRemains(),
					mRemains, mUpdateTime);
		}
		if (mStatus != oldTask.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldTask.getStatus(), mStatus,
					mUpdateTime);
		}
		if (mStoryId != oldTask.getStoryId()) {
			// task drop from story
			if (mStoryId <= 0 && oldTask.getStoryId() > 0) {
				addHistoryOfTaskRemoveFromStory(oldTask.getStoryId(), mId, mUpdateTime); // for
																				// task
				addHistoryOfStoryRemoveTask(oldTask.getStoryId(), mId,
						mUpdateTime); // for story
			}
			// task append to story
			else if (mStoryId > 0 && oldTask.getStoryId() <= 0) {
				addHistoryOfAddRelation(mStoryId, mId, mUpdateTime);
			}
		}
		if (mHandlerId != oldTask.getHandlerId()) {
			addHistory(HistoryObject.TYPE_HANDLER, oldTask.getHandlerId(),
					mHandlerId, mUpdateTime);
		}
		
		setPartnersAndHistory(mUpdateTime);
	}
	
	private void setPartnersAndHistory(long specificTime) {
		List<Long> oldPartnersId = getPartnersId();
		@SuppressWarnings("unchecked")
		List<Long> intersectionPartnersId = (List<Long>) CollectionUtils
				.intersection(oldPartnersId, mPartnersId);
		@SuppressWarnings("unchecked")
		List<Long> shouldRemovePartnersId = (List<Long>) CollectionUtils
				.subtract(oldPartnersId, intersectionPartnersId);
		@SuppressWarnings("unchecked")
		List<Long> shouldAddPartnersId = (List<Long>) CollectionUtils.subtract(
				mPartnersId, intersectionPartnersId);
		for (long partnerId : shouldRemovePartnersId) {
			removePartner(partnerId);
			TaskDAO.getInstance().removePartner(mId, partnerId);
			addHistory(HistoryObject.TYPE_REMOVE_PARTNER, "", String.valueOf(partnerId), specificTime);
		}
		for (long partnerId : shouldAddPartnersId) {
			addPartner(partnerId);
			addHistory(HistoryObject.TYPE_ADD_PARTNER, "", String.valueOf(partnerId), specificTime);
		}
	}

	private void addHistoryOfAddRelation(long storyId, long taskId) {
		addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mStoryId));
		HistoryObject history = new HistoryObject(mStoryId,
				IssueTypeEnum.TYPE_STORY, HistoryObject.TYPE_ADD, "",
				String.valueOf(mId), System.currentTimeMillis());
		history.save();
	}

	// addHistoryOfAddRelation for specific time
	private void addHistoryOfAddRelation(long storyId, long taskId,
			long specificTime) {
		addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mStoryId));
		HistoryObject history = new HistoryObject(mStoryId,
				IssueTypeEnum.TYPE_STORY, HistoryObject.TYPE_ADD, "",
				String.valueOf(mId), specificTime);
		history.save();
	}

	// addHistoryOfTaskDropFromStory for specific time
	private void addHistoryOfTaskRemoveFromStory(long storyId, long taskId,
			long specificTime) {
		addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(storyId),
				specificTime);
	}

	private void addHistoryOfStoryDropTask(long storyId, long taskId, long specificTime) {
		HistoryObject history = new HistoryObject(storyId,
				IssueTypeEnum.TYPE_STORY, HistoryObject.TYPE_DROP, "",
				String.valueOf(mId), specificTime);
		history.save();
	}

	// addHistoryOfStoryRemoveTask for specific time
	private void addHistoryOfStoryRemoveTask(long storyId, long taskId,
			long specificTime) {
		HistoryObject history = new HistoryObject(storyId,
				IssueTypeEnum.TYPE_STORY, HistoryObject.TYPE_REMOVE, "",
				String.valueOf(mId), specificTime);
		history.save();
	}

	// add history for specific time
	private void addHistory(int type, long oldValue, long newValue,
			long specificTime) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue),
				specificTime);
	}

	private void addHistory(int type, String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_TASK,
				type, oldValue, newValue, System.currentTimeMillis());
		history.save();
	}

	// add history for specific time
	private void addHistory(int type, String oldValue, String newValue,
			long specificTime) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_TASK,
				type, oldValue, newValue, specificTime);
		history.save();
	}
}
