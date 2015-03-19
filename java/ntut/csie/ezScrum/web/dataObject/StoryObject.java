package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databasEnum.StoryEnum;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class StoryObject implements IBaseObject {
	public final static int STATUS_UNCHECK = 0;
	public final static int STATUS_DONE = 1;
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
	
	public static StoryObject get(long id) {
		return StoryDAO.getInstance().get(id);
	}
	
	public static ArrayList<StoryObject> getStoriesBySprintId(long sprintId) {
		return StoryDAO.getInstance().getStoriesBySprintId(sprintId);
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
	
	public static ArrayList<StoryObject> getAllStoriesByProjectId(long ProjectId) {
		return StoryDAO.getInstance().getStoriesByProjectId(ProjectId);
	}
	
	public ArrayList<AttachFileObject> getAttachFiles() {
		return AttachFileDAO.getInstance().getAttachFilesByStoryId(mId);
	}
	
	public ArrayList<TagObject> getTags() {
		return TagDAO.getInstance().getTagsByStoryId(mId);
	}
	
	public void removeTag(long tagId) {
		TagObject tag = TagDAO.getInstance().get(tagId);
		if (tag != null && isTagExistingInStory(tagId)) {
			TagDAO.getInstance().removeTagFromStory(mId, tagId);
		}
	}
	
	public void addTag(long tagId) {
		TagObject tag = TagDAO.getInstance().get(tagId);
		if (tag != null && !isTagExistingInStory(tagId)) {
			TagDAO.getInstance().addTagToStory(mId, tagId);			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setTags(ArrayList<Long> tagIds) {
		ArrayList<Long> oldTags = new ArrayList<Long>();
		for (TagObject tag : getTags()) {
			oldTags.add(tag.getId());
		}
		
		ArrayList<Long> deleteTags = (ArrayList<Long>) CollectionUtils.subtract(oldTags, tagIds);
		ArrayList<Long> addTags = (ArrayList<Long>) CollectionUtils.subtract(tagIds, oldTags);
		
		for (Long tagId : deleteTags) {
			removeTag(tagId);
		}
		
		for (Long tagId : addTags) {
			addTag(tagId);
		}
	}
	
	@Override
	public void save() {
		if (exists()) {
			mUpdateTime = System.currentTimeMillis();
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
			mUpdateTime = specificTime;
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
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject story = new JSONObject();
		JSONArray tasks = new JSONArray();
		JSONArray histories = new JSONArray();
		JSONArray tags = new JSONArray();
		
		for (TaskObject task : getTasks()) {
			tasks.put(task.toJSON());
		}
		
		for (HistoryObject history : getHistories()) {
			histories.put(history.toJSON());
		}
		
		for (TagObject tag : getTags()) {
			tags.put(tag.toJSON());
		}
		
		story
			.put(StoryEnum.ID, mId)
			.put(StoryEnum.NAME, mName)
			.put(StoryEnum.NOTES, mNotes)
			.put(StoryEnum.HOW_TO_DEMO, mHowToDemo)
			.put(StoryEnum.IMPORTANCE, mImportance)
			.put(StoryEnum.VALUE, mValue)
			.put(StoryEnum.ESTIMATE, mEstimate)
			.put(StoryEnum.STATUS, mStatus)
			.put(StoryEnum.SPRINT_ID, mSprintId)
			.put("tasks", tasks)
			.put("histories", histories)
			.put("tags", tags);
		
		return story;
	}
	
	private boolean exists() {
		StoryObject story = StoryDAO.getInstance().get(mId);
		return story != null;
	}
	
	private void doCreate() {
		mId = StoryDAO.getInstance().create(this);
		reload();
		HistoryDAO.getInstance().create(
				new HistoryObject(mId, IssueTypeEnum.TYPE_STORY,
						HistoryObject.TYPE_CREATE, "", "", mCreateTime));
		if (mSprintId != DEFAULT_VALUE) {
			// Append this story to sprint
			addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mSprintId), mCreateTime);
		}
	}
	
	private void doUpdate() {
		StoryObject oldStory = StoryDAO.getInstance().get(mId);
		StoryDAO.getInstance().update(this);
		
		if (!mName.equals(oldStory.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldStory.getName(), mName);
		}
		if (!mNotes.equals(oldStory.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldStory.getNotes(), mNotes);
		}
		if (!mHowToDemo.equals(oldStory.getHowToDemo())) {
			addHistory(HistoryObject.TYPE_HOWTODEMO, oldStory.getHowToDemo(), mHowToDemo);
		}
		if (mImportance != oldStory.getImportance()) {
			addHistory(HistoryObject.TYPE_IMPORTANCE, oldStory.getImportance(), mImportance);
		}
		if (mValue != oldStory.getValue()) {
			addHistory(HistoryObject.TYPE_VALUE, oldStory.getValue(), mValue);
		}
		if (mEstimate != oldStory.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldStory.getEstimate(), mEstimate);
		}
		if (mStatus != oldStory.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldStory.getStatus(), mStatus);
		}
		if (mSprintId != oldStory.getSprintId()) {
			if (mSprintId == DEFAULT_VALUE) {
				// Remove this story from sprint
				addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()));				
			} else if (mSprintId != DEFAULT_VALUE) {
				// Append this story to sprint
				if (oldStory.getSprintId() != DEFAULT_VALUE) {
					addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()));
				}
				addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mSprintId));
			}
		}
	}
	
	private void doUpdate(long specificTime) {
		StoryObject oldStory = StoryDAO.getInstance().get(mId);
		StoryDAO.getInstance().update(this);
		
		if (!mName.equals(oldStory.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldStory.getName(), mName, specificTime);
		}
		if (!mNotes.equals(oldStory.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldStory.getNotes(), mNotes, specificTime);
		}
		if (!mHowToDemo.equals(oldStory.getHowToDemo())) {
			addHistory(HistoryObject.TYPE_HOWTODEMO, oldStory.getHowToDemo(), mHowToDemo, specificTime);
		}
		if (mImportance != oldStory.getImportance()) {
			addHistory(HistoryObject.TYPE_IMPORTANCE, oldStory.getImportance(), mImportance, specificTime);
		}
		if (mValue != oldStory.getValue()) {
			addHistory(HistoryObject.TYPE_VALUE, oldStory.getValue(), mValue, specificTime);
		}
		if (mEstimate != oldStory.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldStory.getEstimate(), mEstimate, specificTime);
		}
		if (mStatus != oldStory.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldStory.getStatus(), mStatus, specificTime);
		}
		if (mSprintId != oldStory.getSprintId()) {
			if (mSprintId == DEFAULT_VALUE) {
				// Remove this story from sprint
				addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()), specificTime);				
			} else if (mSprintId != DEFAULT_VALUE) {
				// Append this story to sprint
				if (oldStory.getSprintId() != DEFAULT_VALUE) {
					addHistory(HistoryObject.TYPE_REMOVE, "", String.valueOf(oldStory.getSprintId()), specificTime);
				}
				addHistory(HistoryObject.TYPE_APPEND, "", String.valueOf(mSprintId), specificTime);
			}
		}
	}
	
	private void addHistory(int type, int oldValue, int newValue) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue));
	}
	
	private void addHistory(int type, int oldValue, int newValue, long specificTime) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue), specificTime);
	}
	
	private void addHistory(int type, String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_STORY,
				type, oldValue, newValue, System.currentTimeMillis());
		HistoryDAO.getInstance().create(history);
	}
	
	private void addHistory(int type, String oldValue, String newValue, long specificTime) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_STORY,
				type, oldValue, newValue, specificTime);
		HistoryDAO.getInstance().create(history);
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
}
