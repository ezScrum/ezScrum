package ntut.csie.ezScrum.web.dataObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.web.databaseEnum.HistoryEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.StatusEnum;

public class HistoryObject implements IBaseObject,Comparable<HistoryObject> {
	public final static int TYPE_CREATE = 1;
	public final static int TYPE_NAME = 2;
	public final static int TYPE_ESTIMATE = 3;
	public final static int TYPE_REMAIMS = 4;
	//public final static int TYPE_ACTUAL = 5;
	public final static int TYPE_IMPORTANCE = 6;
	public final static int TYPE_VALUE = 7;
	public final static int TYPE_ATTACH_FILE = 11;
	public final static int TYPE_STATUS = 12;
	public final static int TYPE_HANDLER = 13;
	public final static int TYPE_SPECIFIC_TIME = 14;
	public final static int TYPE_DROP = 15; // Drop from parent (for task history)
	public final static int TYPE_APPEND = 16; // Append to parent (for task history)
	public final static int TYPE_ADD = 17; // Add Child (for story history)
	public final static int TYPE_REMOVE = 18; // Remove Child (for story history)
	public final static int TYPE_NOTE = 19;
	public final static int TYPE_HOW_TO_DEMO = 20;
	public final static int TYPE_ADD_PARTNER = 21;
	public final static int TYPE_REMOVE_PARTNER = 22;
	public final static int TYPE_SPRINT_ID = 23;

	private long mId;
	private long mIssueId;
	private int mIssueType;
	private int mHistoryType;
	private long mCreateTime;
	private String mOldValue = "";
	private String mNewValue = "";

	public HistoryObject() {}

	public HistoryObject(long issueId, int issueType, int historyType,
	        String oldValue, String newValue, long createTime) {
		setIssueId(issueId);
		setIssueType(issueType);
		setHistoryType(historyType);
		setOldValue(oldValue);
		setNewValue(newValue);
		setCreateTime(createTime);
	}

	public HistoryObject setId(long id) {
		mId = id;
		return this;
	}

	public HistoryObject setIssueId(long issueId) {
		mIssueId = issueId;
		return this;
	}

	public HistoryObject setIssueType(int issueType) {
		mIssueType = issueType;
		return this;
	}

	public HistoryObject setHistoryType(int historyType) {
		mHistoryType = historyType;
		return this;
	}

	public HistoryObject setOldValue(String oldValue) {
		mOldValue = oldValue;
		return this;
	}

	public HistoryObject setNewValue(String newValue) {
		mNewValue = newValue;
		return this;
	}

	public HistoryObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public long getId() {
		return mId;
	}

	public long getIssueId() {
		return mIssueId;
	}

	public int getIssueType() {
		return mIssueType;
	}

	public int getHistoryType() {
		return mHistoryType;
	}

	public String getOldValue() {
		return mOldValue;
	}

	public String getNewValue() {
		return mNewValue;
	}

	public String getDescription() {
		switch (mHistoryType) {
			case TYPE_CREATE:
				return getCreateDesc();
			case TYPE_NAME:
				return getQuoteDesc();
			case TYPE_ESTIMATE:
				return getNormalDesc();
			case TYPE_REMAIMS:
				return getNormalDesc();
			/*case TYPE_ACTUAL:
				return getNormalDesc();*/
			case TYPE_IMPORTANCE:
				return getNormalDesc();
			case TYPE_VALUE:
				return getNormalDesc();
			case TYPE_ATTACH_FILE:
				return getAttachFileDesc();
			case TYPE_STATUS:
				return getStatusDesc();
			case TYPE_HANDLER:
				return getHandlerDesc();
			case TYPE_SPECIFIC_TIME:
				return getNormalDesc();
			case TYPE_DROP:
				return getDropChildDesc();
			case TYPE_APPEND:
				return getAppendParentDesc();
			case TYPE_ADD:
				return getAddChildDesc();
			case TYPE_REMOVE:
				return getRemoveParentDesc();
			case TYPE_NOTE:
				return getQuoteDesc();
			case TYPE_HOW_TO_DEMO:
				return getQuoteDesc();
			case TYPE_ADD_PARTNER:
				return getPartnerDesc();
			case TYPE_REMOVE_PARTNER:
				return getPartnerDesc();
			case TYPE_SPRINT_ID:
				return getSprintDesc();
		}
		return "";
	}

	public String getHistoryTypeString() {
		switch (mHistoryType) {
			case TYPE_NAME:
				return "Name";
			case TYPE_ESTIMATE:
				return "Estimate";
			case TYPE_REMAIMS:
				return "Remains";
			/*case TYPE_ACTUAL:
				return "Actual Hour";*/
			case TYPE_IMPORTANCE:
				return "Importance";
			case TYPE_VALUE:
				return "Value";
			case TYPE_ATTACH_FILE:
				return "Attach File";
			case TYPE_STATUS:
				return "Status";
			case TYPE_HANDLER:
				return "Handler";
			case TYPE_SPECIFIC_TIME:
				return "Specific Time";
			case TYPE_HOW_TO_DEMO:
				return "How To Demo";
			case TYPE_NOTE:
				return "Note";
			case TYPE_ADD_PARTNER:
				return "Add Partner";
			case TYPE_REMOVE_PARTNER:
				return "Remove Partner";
			
		}
		return "";
	}

	private String getAttachFileDesc() {
		return "Attach a file: " + mNewValue;
	}

	private String getNormalDesc() {
		return String.format("%s => %s", mOldValue, mNewValue);
	}

	private String getQuoteDesc() {
		return String.format("\"%s\" => \"%s\"", mOldValue, mNewValue);
	}

	private String getHandlerDesc() {
		if (mOldValue.equals("-1") && !mNewValue.equals("-1")) {
			String newUsername = AccountDAO.getInstance()
			        .get(Long.parseLong(mNewValue)).getUsername();
			return newUsername;
		}

		if (!mOldValue.equals("-1") && mNewValue.equals("-1")) {
			String newUsername = AccountDAO.getInstance()
			        .get(Long.parseLong(mOldValue)).getUsername();
			return "Remove handler " + newUsername;
		}

		if (!mOldValue.equals("-1") && !mNewValue.equals("-1")) {
			AccountObject oldUser = AccountDAO.getInstance().get(Long.parseLong(mOldValue));
			AccountObject newUser = AccountDAO.getInstance().get(Long.parseLong(mNewValue));
			String oldUsername = "";
			String newUsername = "";
			if (oldUser != null) {
				oldUsername = oldUser.getUsername();
			}

			if (newUser != null) {
				newUsername = newUser.getUsername();
			}
			return oldUsername + " => " + newUsername;
		}
		return "";
	}

	private String getStatusDesc() {
		String oldStatusString = getStatusString(Integer.parseInt(mOldValue));
		String newStatusString = getStatusString(Integer.parseInt(mNewValue));
		return oldStatusString + " => " + newStatusString;
	}
	
	private String getStatusString(int status) {
		final String NOT_CHECK_OUT = "Not Check Out";
		final String CHECK_OUT = "Check Out";
		final String DONE = "Done";
		String statusString = "";
		if (status == StatusEnum.NEW) {
			statusString = NOT_CHECK_OUT;
		} else if (status == StatusEnum.ASSIGNED) {
			statusString = CHECK_OUT;
		} else if (status == StatusEnum.CLOSED) {
			statusString = DONE;
		}
		return statusString;
	}

	private String getCreateDesc() {
		String desc = "";
		switch (mIssueType) {
			case IssueTypeEnum.TYPE_STORY:
				StoryObject story = StoryObject.get(mIssueId);
				desc = "Create Story #" + story.getSerialId();
				break;
			case IssueTypeEnum.TYPE_TASK:
				TaskObject task = TaskObject.get(mIssueId);
				desc = "Create Task #" + task.getSerialId();
				break;
			case IssueTypeEnum.TYPE_UNPLAN:
				UnplanObject unplan = UnplanObject.get(mIssueId);
				desc = "Create Unplan #" + unplan.getSerialId();
				break;
		}
		return desc;
	}

	private String getAppendParentDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_TASK) {
			long storyId = Long.parseLong(mNewValue);
			StoryObject story = StoryObject.get(storyId);
			return "Append to Story #" + story.getSerialId();
		} else if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			long sprintId = Long.parseLong(mNewValue);
			SprintObject sprint = SprintObject.get(sprintId);
			if(sprint == null) {
				return "";
			} else {
				return "Append to Sprint #" + sprint.getSerialId();
			}
		}
		return "";
	}

	private String getRemoveParentDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_TASK) {
			long storyId = Long.parseLong(mNewValue);
			StoryObject story = StoryObject.get(storyId);
			return "Remove from Story #" + story.getSerialId();
		} else if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			long sprintId = Long.parseLong(mNewValue);
			SprintObject sprint = SprintObject.get(sprintId);
			if(sprint == null) {
				return "";
			} else {
				return "Remove from Sprint #" + sprint.getSerialId();
			}
		}
		return "";
	}

	private String getAddChildDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			long taskId = Long.parseLong(mNewValue);
			TaskObject task = TaskObject.get(taskId);
			return "Add Task #" + task.getSerialId();
		}
		return "";
	}

	private String getDropChildDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			long taskId = Long.parseLong(mNewValue);
			TaskObject task = TaskObject.get(taskId);
			return "Drop Task #" + task.getSerialId();
		}
		return "";
	}

	private String getSprintDesc() {
		long oldSprintId = Long.parseLong(mOldValue);
		SprintObject oldSprint = SprintObject.get(oldSprintId);
		long newSprintId = Long.parseLong(mNewValue);
		SprintObject newSprint = SprintObject.get(newSprintId);
		return String.format("Sprint #%s => Sprint #%s", oldSprint.getSerialId(), newSprint.getSerialId());
	}
	
	private String getPartnerDesc() {
		if (mNewValue != null && !mNewValue.equals("") && !mNewValue.equals("0") && !mNewValue.equals("-1")) {
			String partnerUsername = AccountDAO.getInstance()
			        .get(Long.parseLong(mNewValue)).getUsername();
			return partnerUsername;
		}
		return "";
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public String getFormattedModifiedTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		return sdf.format(new Date(mCreateTime));
	}
	
	public int compareTo(HistoryObject compareHO){
		if(this.getId() > compareHO.getId()){
			return 1;
		}
		else{
			return -1;
		}
		
	}

	@Override
	public boolean exists() {
		HistoryObject history = HistoryDAO.getInstance().get(mId);
		return history != null;
	}

	private void doCreate() {
		mId = HistoryDAO.getInstance().create(this);
		reload();
	}

	@Override
	public void save() {
		if (!exists()) {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			HistoryObject history = HistoryDAO.getInstance().get(mId);
			resetData(history);
		}
	}
	
	private void resetData(HistoryObject history) {
		mId = history.getId();
		mIssueId = history.getIssueId();
		mIssueType = history.getIssueType();
		mHistoryType = history.getHistoryType();
		mOldValue = history.getOldValue();
		mNewValue = history.getNewValue();
		setCreateTime(history.getCreateTime());
	}

	@Override
	public boolean delete() {
		return false;
	}
	
	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject historyJson = new JSONObject();
		historyJson.put(HistoryEnum.ID, getId())
		        .put(HistoryEnum.ISSUE_ID, getIssueId())
		        .put(HistoryEnum.ISSUE_TYPE, getIssueType())
		        .put(HistoryEnum.HISTORY_TYPE, getHistoryType())
		        .put(HistoryEnum.DESCRIPTION, getDescription())
		        .put(HistoryEnum.CREATE_TIME, getCreateTime());
		return historyJson;
	}
}