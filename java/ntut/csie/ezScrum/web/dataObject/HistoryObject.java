package ntut.csie.ezScrum.web.dataObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.web.databasEnum.HistoryEnum;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class HistoryObject {
	public final static int TYPE_CREATE = 1;
	public final static int TYPE_NAME = 2;
	public final static int TYPE_ESTIMATE = 3;
	public final static int TYPE_REMAIMS = 4;
	public final static int TYPE_ACTUAL = 5;
	public final static int TYPE_IMPORTANCE = 6;
	public final static int TYPE_VALUE = 7;
	public final static int TYPE_ATTACH_FILE = 11;
	public final static int TYPE_STATUS = 12;
	public final static int TYPE_HANDLER = 13;
	public final static int TYPE_SPECIFIC_TIME = 14;
	public final static int TYPE_DROP = 15; // Drop from parent
	public final static int TYPE_APPEND = 16; // Append to parent
	public final static int TYPE_ADD = 17;	// Add Child
	public final static int TYPE_REMOVE = 18;	// Remove Child
	public final static int TYPE_NOTE = 19;
	public final static int TYPE_HOWTODEMO = 20;
	public final static int TYPE_PARTNERS = 21;

	private long mId;
	private long mIssueId;
	private int mIssueType;
	private int mHistoryType;
	private long mModifiedTime;
	private String mOldValue = "";
	private String mNewValue = "";

	public HistoryObject() {
	}

	public HistoryObject(long issueId, int issueType, int historyType,
			String oldValue, String newValue, long modifiedTime) {
		setIssueId(issueId);
		setIssueType(issueType);
		setHistoryType(historyType);
		setOldValue(oldValue);
		setNewValue(newValue);
		setModifiedTime(modifiedTime);
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

	public HistoryObject setModifiedTime(long modifiedTime) {
		mModifiedTime = modifiedTime;
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
		// TODO 判斷 type 來輸出成特定的 desc
		switch (mHistoryType) {
		case TYPE_CREATE:
			return getCreateDesc();
		case TYPE_NAME:
			return getQuoteDesc();
		case TYPE_ESTIMATE:
			return getNormalDesc();
		case TYPE_REMAIMS:
			return getNormalDesc();
		case TYPE_ACTUAL:
			return getNormalDesc();
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
		case TYPE_NOTE:
			return getQuoteDesc();
		case TYPE_ADD:
			return getAddChildDesc();
		case TYPE_APPEND:
			return getAppendParentDesc();
		case TYPE_DROP:
			return getDropChildDesc();
		case TYPE_REMOVE:
			return getRemoveParentDesc();
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
		case TYPE_ACTUAL:
			return "Actual Hour";
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
		case TYPE_NOTE:
			return "Note";
		case TYPE_HOWTODEMO:
			return "How To Demo";
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
		Configuration config = new Configuration();
		MySQLService service = new MySQLService(config);
		service.openConnect();
		
		if (mOldValue.equals("") && !mNewValue.equals("")) {
			String newAccount = service.getAccount(mNewValue).getUsername();
			service.closeConnect();
			return newAccount;
		}
		
		if (!mOldValue.equals("") && !mNewValue.equals("")) {
			String oldAccount = "";
			String newAccount = "";
			oldAccount = service.getAccount(mOldValue).getUsername();
			newAccount = service.getAccount(mNewValue).getUsername();
			service.closeConnect();
			return oldAccount + " => " + newAccount;
		}
		service.closeConnect();
		return "";
	}
	
	private String getStatusDesc() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("10", "Not Check Out");
		map.put("50", "Check Out");
		map.put("90", "Done");
		return map.get(mOldValue) + " => " + map.get(mNewValue);
	}

	private String getCreateDesc() {
		String desc = "";
		switch (mIssueType) {
		case IssueTypeEnum.TYPE_STORY:
			desc = "Create Story #" + mIssueId;
			break;
		case IssueTypeEnum.TYPE_TASK:
			desc = "Create Task #" + mIssueId;
			break;
		case IssueTypeEnum.TYPE_UNPLANNED:
			desc = "Create Unplanned #" + mIssueId;
			break;
		case IssueTypeEnum.TYPE_RETROSPECTIVE:
			desc = "Create Retrospective #" + mIssueId;
			break;
		}
		return desc;
	}
	
	private String getAppendParentDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_TASK) {
			return "Append to Story #" + mNewValue;
		} else if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			return "Append to Sprint #" + mNewValue;
		} else if (mIssueType == IssueTypeEnum.TYPE_UNPLANNED) {
			return "Append to Sprint #" + mNewValue;
		}
		return "";
	}
	
	private String getRemoveParentDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_TASK) {
			return "Remove from Story #" + mNewValue;
		} else if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			return "Remove from Sprint #" + mNewValue;
		}
		return "";
	}
	
	private String getAddChildDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			return "Add Task #" + mNewValue;
		}
		return "";
	}
	
	private String getDropChildDesc() {
		if (mIssueType == IssueTypeEnum.TYPE_STORY) {
			return "Drop Task #" + mNewValue;
		}
		return "";
	}
	
	public long getModifiedTime() {
		return mModifiedTime;
	}
	
	public String getFormattedModifiedTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		return sdf.format(new Date(mModifiedTime));
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object.put(HistoryEnum.ID, getId())
				.put(HistoryEnum.ISSUE_ID, getIssueId())
				.put(HistoryEnum.ISSUE_TYPE, getIssueType())
				.put(HistoryEnum.HISTORY_TYPE, getHistoryType())
				.put(HistoryEnum.DESCRIPTION, getDescription())
				.put(HistoryEnum.MODIFIED_TIME, getModifiedTime());
		return object;
	}
}