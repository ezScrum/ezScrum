package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.StatusEnum;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;

public class StoryObject implements IBaseObject {
	public final static int STATUS_UNCHECK = StatusEnum.NEW;
	public final static int STATUS_DONE = StatusEnum.CLOSED;
	public final static int NO_PARENT = -1;
	public final static int DEFAULT_VALUE = -1;
	
	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;
	private long mSprintId = DEFAULT_VALUE;
	
	private String mName = "";
	private String mNotes = "";
	private String mHowToDemo = "";
	
	private int mImportance = 0;
	private int mValue = 0;
	private int mEstimate = 0;
	private int mStatus = STATUS_UNCHECK;
	
	private long mCreateTime = 0;
	private long mUpdateTime = 0;
	
	private ArrayList<Long> mCacheTagsId = new ArrayList<Long>();
	private boolean mUpdateTags = false;
	
	public static StoryObject get(long id) {
		return StoryDAO.getInstance().get(id);
	}
	
	public static StoryObject get(long projectId, long serialId) {
		return StoryDAO.getInstance().get(projectId, serialId);
	}
	
	public StoryObject(long projectId) {
		mProjectId = projectId;
	}
	
	public StoryObject(long id, long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}
	
	public StoryObject setName(String name) {
		mName = name;
		return this;
	}
	
	public StoryObject setNotes(String notes) {
		mNotes = notes;
		return this;
	}
	
	public StoryObject setHowToDemo(String howToDemo) {
		mHowToDemo = howToDemo;
		return this;
	}
	
	public StoryObject setImportance(int importance) {
		mImportance= importance;
		return this;
	}
	
	public StoryObject setValue(int value) {
		mValue = value;
		return this;
	}
	
	public StoryObject setEstimate(int estimate) {
		mEstimate = estimate;
		return this;
	}
	
	public StoryObject setStatus(int status) {
		mStatus = status;
		return this;
	}
	
	public StoryObject setSprintId(long sprintId) {
		mSprintId = sprintId;
		return this;
	}
	
	public StoryObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}
	
	public StoryObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
	}
	
	public long getId() {
		return mId;
	}
	
	public long getProjectId() {
		return mProjectId;
	}
	
	public long getSerialId() {
		return mSerialId;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getNotes() {
		return mNotes;
	}
	
	public String getHowToDemo() {
		return mHowToDemo;
	}
	
	public int getImportance() {
		return mImportance;
	}
	
	public int getEstimate() {
		return mEstimate;
	}
	
	public int getValue() {
		return mValue;
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
				if (statusInHistory.equals(String.valueOf(STATUS_UNCHECK))) {
					status = STATUS_UNCHECK;
				} else if (history.getNewValue().equals(
						String.valueOf(STATUS_DONE))) {
					status = STATUS_DONE;
				}
			}
		}
		return status;
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
	
	public String getStatusString() {
		if (mStatus == STATUS_UNCHECK) {
			return "new";
		} else {
			return "closed";
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
	
	public ArrayList<TaskObject> getTasks() {
		return TaskDAO.getInstance().getTasksByStoryId(mId);
	}
	
	public ArrayList<HistoryObject> getHistories() {
		return HistoryDAO.getInstance().getHistoriesByIssue(mId, IssueTypeEnum.TYPE_STORY);
	}
	
	public ArrayList<AttachFileObject> getAttachFiles() {
		return AttachFileDAO.getInstance().getAttachFilesByStoryId(mId);
	}
	
	public ArrayList<TagObject> getTags() {
		return TagDAO.getInstance().getTagsByStoryId(mId);
	}
	
	public ArrayList<Long> getTagsId() {
		ArrayList<Long> tagsId = new ArrayList<Long>();
		for (TagObject tag : getTags()) {
			tagsId.add(tag.getId());
		}
		return tagsId;
	}
	
	public double getTotalTaskPoints() {
		ArrayList<TaskObject> tasks = getTasks();
		double point = 0;
		for (TaskObject task : tasks) {
			point += task.getEstimate();
		}
		return point;
	}
	
	public double getTaskRemainsPoints() {
		ArrayList<TaskObject> tasks = getTasks();
		double point = 0;
		for (TaskObject task : tasks) {
			if (task.getStatus() == TaskObject.STATUS_DONE) {
				continue;
			}
			point += task.getRemains();
		}
		return point;
	}
	
	public void removeTag(long tagId) {
		if (mCacheTagsId.size() == 0) {
			mCacheTagsId = getTagsId();
		}
		
		for (int i = 0; i < mCacheTagsId.size(); i++) {
			long cacheTagId = mCacheTagsId.get(i);
			if (cacheTagId == tagId) {
				mCacheTagsId.remove(i);
				mUpdateTags = true;
			}
		}
	}
	
	public void addTag(long tagId) {
		if (mCacheTagsId.size() == 0) {
			mCacheTagsId = getTagsId();
		}
		
		for (int i = 0; i < mCacheTagsId.size(); i++) {
			long cacheTagId = mCacheTagsId.get(i);
			if (cacheTagId == tagId) {
				return;
			}
		}
		mCacheTagsId.add(tagId);
		mUpdateTags = true;
	}
	
	public StoryObject setTags(ArrayList<Long> tagIds) {
		mCacheTagsId = tagIds;
		mUpdateTags = true;
		return this;
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
			StoryObject story = StoryDAO.getInstance().get(mId);
			resetData(story);
		}
	}

	@Override
	public boolean delete() {
		HistoryDAO.getInstance().deleteByIssue(mId, IssueTypeEnum.TYPE_STORY);
		boolean success = StoryDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
			mProjectId = DEFAULT_VALUE;
		}
		return success;
	}

	public String toString() {
		try {
			return toJSON().toString();			
		} catch(JSONException e) {
			return "JSON Exception";
		}
	}
	
	public boolean containsTask(TaskObject targetTask) {
		boolean isContainingTask = false;
		ArrayList<TaskObject> tasks = getTasks();
		for (TaskObject task : tasks) {
			if (task.getId() == targetTask.getId()) {
				isContainingTask = true;
			}
		}
		return isContainingTask;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject storyJson = new JSONObject();
		JSONArray taskJsonArray = new JSONArray();
		JSONArray historyJsonArray = new JSONArray();
		JSONArray tagJsonArray = new JSONArray();
		
		for (TaskObject task : getTasks()) {
			taskJsonArray.put(task.toJSON());
		}
		
		for (HistoryObject history : getHistories()) {
			historyJsonArray.put(history.toJSON());
		}
		
		for (TagObject tag : getTags()) {
			tagJsonArray.put(tag.toJSON());
		}
		
		storyJson
			.put(StoryEnum.ID, mId)
			.put(StoryEnum.SERIAL_ID, mSerialId)
			.put(StoryEnum.NAME, mName)
			.put(StoryEnum.NOTES, mNotes)
			.put(StoryEnum.HOW_TO_DEMO, mHowToDemo)
			.put(StoryEnum.IMPORTANCE, mImportance)
			.put(StoryEnum.VALUE, mValue)
			.put(StoryEnum.ESTIMATE, mEstimate)
			.put(StoryEnum.STATUS, mStatus)
			.put(StoryEnum.SPRINT_ID, mSprintId)
			.put("totalTaskPoint", getTotalTaskPoints())
			.put("tasks", taskJsonArray)
			.put("histories", historyJsonArray)
			.put("tags", tagJsonArray);
		
		return storyJson;
	}
	
	@Override
	public boolean exists() {
		StoryObject story = StoryDAO.getInstance().get(mId);
		return story != null;
	}
	
	private void doCreate() {
		mCreateTime = System.currentTimeMillis();
		mId = StoryDAO.getInstance().create(this);
		saveTags();
		reload();
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_STORY,
				HistoryObject.TYPE_CREATE, "", "", mCreateTime); 
		history.save();
		if (mSprintId != DEFAULT_VALUE) {
			// Append this story to sprint
			addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mSprintId), mCreateTime);
		}
	}
	
	private void doUpdate() {
		mUpdateTime = System.currentTimeMillis();
		StoryObject oldStory = StoryDAO.getInstance().get(mId);
		StoryDAO.getInstance().update(this);
		saveTags();
		
		if (!mName.equals(oldStory.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldStory.getName(), mName, mUpdateTime);
		}
		if (!mNotes.equals(oldStory.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldStory.getNotes(), mNotes, mUpdateTime);
		}
		if (!mHowToDemo.equals(oldStory.getHowToDemo())) {
			addHistory(HistoryObject.TYPE_HOW_TO_DEMO, oldStory.getHowToDemo(), mHowToDemo, mUpdateTime);
		}
		if (mImportance != oldStory.getImportance()) {
			addHistory(HistoryObject.TYPE_IMPORTANCE, oldStory.getImportance(), mImportance, mUpdateTime);
		}
		if (mValue != oldStory.getValue()) {
			addHistory(HistoryObject.TYPE_VALUE, oldStory.getValue(), mValue, mUpdateTime);
		}
		if (mEstimate != oldStory.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldStory.getEstimate(), mEstimate, mUpdateTime);
		}
		if (mStatus != oldStory.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldStory.getStatus(), mStatus, mUpdateTime);
		}
		if (mSprintId != oldStory.getSprintId()) {
			if (mSprintId == DEFAULT_VALUE) {
				// Remove this story from sprint
				addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()), mUpdateTime);				
			} else if (mSprintId != DEFAULT_VALUE) {
				// Append this story to sprint
				if (oldStory.getSprintId() != DEFAULT_VALUE) {
					addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()), mUpdateTime);
				}
				addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mSprintId), mUpdateTime);
			}
		}
	}
	
	private void doUpdate(long specificTime) {
		mUpdateTime = specificTime;
		StoryObject oldStory = StoryDAO.getInstance().get(mId);
		StoryDAO.getInstance().update(this);
		saveTags();
		
		if (!mName.equals(oldStory.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldStory.getName(), mName, mUpdateTime);
		}
		if (!mNotes.equals(oldStory.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldStory.getNotes(), mNotes, mUpdateTime);
		}
		if (!mHowToDemo.equals(oldStory.getHowToDemo())) {
			addHistory(HistoryObject.TYPE_HOW_TO_DEMO, oldStory.getHowToDemo(), mHowToDemo, mUpdateTime);
		}
		if (mImportance != oldStory.getImportance()) {
			addHistory(HistoryObject.TYPE_IMPORTANCE, oldStory.getImportance(), mImportance, mUpdateTime);
		}
		if (mValue != oldStory.getValue()) {
			addHistory(HistoryObject.TYPE_VALUE, oldStory.getValue(), mValue, mUpdateTime);
		}
		if (mEstimate != oldStory.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldStory.getEstimate(), mEstimate, mUpdateTime);
		}
		if (mStatus != oldStory.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldStory.getStatus(), mStatus, mUpdateTime);
		}
		if (mSprintId != oldStory.getSprintId()) {
			if (mSprintId == DEFAULT_VALUE) {
				// Remove this story from sprint
				addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()), mUpdateTime);				
			} else if (mSprintId != DEFAULT_VALUE) {
				// Append this story to sprint
				if (oldStory.getSprintId() != DEFAULT_VALUE) {
					addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()), mUpdateTime);
				}
				addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mSprintId), mUpdateTime);
			}
		}
	}
	
	private void addHistory(int type, int oldValue, int newValue, long specificTime) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue), specificTime);
	}
	
	private void addHistory(int type, String oldValue, String newValue, long specificTime) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_STORY,
				type, oldValue, newValue, specificTime);
		history.save();
	}
	
	private void resetData(StoryObject story) {
		mId = story.getId();
		mProjectId = story.getProjectId();
		mSerialId = story.getSerialId();
		
		setName(story.getName());
		setNotes(story.getNotes());
		setHowToDemo(story.getHowToDemo());
		setImportance(story.getImportance());
		setValue(story.getValue());
		setEstimate(story.getEstimate());
		setStatus(story.getStatus());
		setSprintId(story.getSprintId());
		setCreateTime(story.getCreateTime());
		setUpdateTime(story.getUpdateTime());
	}
	
	private boolean isTagExistingInStory(long tagId) {
		ArrayList<TagObject> tags = getTags();
		for (TagObject tag : tags) {
			if (tag.getId() == tagId) {
				return true;
			}
		}
		return false;
	}
	
	private void removeTagFromDB(long tagId) {
		TagObject tag = TagDAO.getInstance().get(tagId);
		if (tag != null && isTagExistingInStory(tagId)) {
			TagDAO.getInstance().removeTagFromStory(mId, tagId);
		}
	}
	
	private void addTagToDB(long tagId) {
		TagObject tag = TagDAO.getInstance().get(tagId);
		if (tag != null && !isTagExistingInStory(tagId)) {
			TagDAO.getInstance().addTagToStory(mId, tagId);			
		}
	}
	
	private void saveTags() {
		if (mUpdateTags) {
			ArrayList<Long> oldTags = new ArrayList<Long>();
			for (TagObject tag : getTags()) {
				oldTags.add(tag.getId());
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<Long> deleteTags = (ArrayList<Long>) CollectionUtils.subtract(oldTags, mCacheTagsId);
			@SuppressWarnings("unchecked")
			ArrayList<Long> addTags = (ArrayList<Long>) CollectionUtils.subtract(mCacheTagsId, oldTags);
			
			for (Long tagId : deleteTags) {
				removeTagFromDB(tagId);
			}
			
			for (Long tagId : addTags) {
				addTagToDB(tagId);
			}
			
			mCacheTagsId = new ArrayList<Long>();
			mUpdateTags = false;
		}
	}
}
