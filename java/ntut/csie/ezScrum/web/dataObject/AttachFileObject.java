package ntut.csie.ezScrum.web.dataObject;

import java.util.HashMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.web.databaseEnum.AttachFileEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

public class AttachFileObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;
	private long mId = -1;
	private long mIssueId = -1;
	private int mIssueType;
	private String mName;
	private String mPath;
	private long mCreateTime;
	private String mContentType;

	/**
	 * @param attachFileId
	 * @param issueId : issue ID
	 * @param issueType : issue類型 Story:1, Task:2
	 * @param fileName : 檔案原始名稱
	 * @param diskfileName : 經過MD5編碼後的檔名
	 * @param path : 完整的檔案路徑
	 * @param createTime : 檔案上傳的時間 (Milliseconds)
	 */
	public AttachFileObject() {
	}
	
	public AttachFileObject(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
	}

	public AttachFileObject setId(long attachFileId) {
		mId = attachFileId;
		return this;
	}

	public long getIssueId() {
		return mIssueId;
	}

	public AttachFileObject setIssueId(long issueId) {
		mIssueId = issueId;
		return this;
	}

	public int getIssueType() {
		return mIssueType;
	}
	
	public String getIssueTypeString() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(IssueTypeEnum.TYPE_STORY, "Story");
		map.put(IssueTypeEnum.TYPE_TASK, "Task");
		
		return map.get(mIssueType);
	}

	public AttachFileObject setIssueType(int issueType) {
		mIssueType = issueType;
		return this;
	}

	public String getName() {
		return mName;
	}

	public AttachFileObject setName(String fileName) {
		mName = fileName;
		return this;
	}

	public String getPath() {
		return mPath;
	}

	public AttachFileObject setPath(String filePath) {
		mPath = filePath;
		return this;
	}

	public long getCreateTime() {
		return mCreateTime;
	}
	
	public String getContentType() {
		return mContentType;
	}
	
	public AttachFileObject setContentType(String contentType) {
		mContentType = contentType;
		return this;
	}

	public void setCreateTime(long createTime) {
		mCreateTime = createTime;
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
			AttachFileObject attachFile = AttachFileDAO.getInstance().get(mId);
			resetData(attachFile);
		}
	}

	@Override
	public boolean delete() {
		boolean success = AttachFileDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mIssueId = DEFAULT_VALUE;
			mIssueType = DEFAULT_VALUE;
		}
		return success;
	}	
	
	@Override
	public boolean exists() {
		AttachFileObject attachFile = AttachFileDAO.getInstance().get(mId);
		return attachFile != null;
	}
	
	private void resetData(AttachFileObject attachFile) {
		mId = attachFile.getId();
		mIssueId = attachFile.getIssueId();
		mIssueType = attachFile.getIssueType();
		mName = attachFile.getName();
		mPath = attachFile.getPath();
		mContentType = attachFile.getContentType();
		setCreateTime(attachFile.getCreateTime());
	}
	
	private void doCreate() {
		mId = AttachFileDAO.getInstance().create(this);
		reload();
	}
	
	public String toString() {
		return AttachFileEnum.ID + "=" + mId + "\n" +
				AttachFileEnum.NAME + "=" + mName + "\n" +
				AttachFileEnum.PATH + "=" + mPath + "\n" +
				AttachFileEnum.ISSUE_ID + "=" + mIssueId + "\n" +
				AttachFileEnum.ISSUE_TYPE + "=" + mIssueType + "\n" +
				AttachFileEnum.CREATE_TIME + "=" + mCreateTime;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject attachFileJson = new JSONObject();
		attachFileJson
			.put(AttachFileEnum.ID, mId)
			.put(AttachFileEnum.NAME, mName)
			.put(AttachFileEnum.PATH, mPath)
			.put(AttachFileEnum.ISSUE_ID, mIssueId)
			.put(AttachFileEnum.ISSUE_TYPE, mIssueType)
			.put(AttachFileEnum.CREATE_TIME, mCreateTime);
		return attachFileJson;
	}
}