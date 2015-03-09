package ntut.csie.ezScrum.web.dataObject;

import java.util.HashMap;

import ntut.csie.ezScrum.web.databasEnum.AttachFileEnum;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AttachFileObject {
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
	private AttachFileObject(AttachFileObject.Builder builder) {
		this.mId = builder.mId;
		this.mName = builder.mName;
		this.mPath = builder.mPath;
		this.mCreateTime = builder.mCreateTime;
		this.mIssueId = builder.mIssueId;
		this.mIssueType = builder.mIssueType;
		this.mContentType = builder.mContentType;
	}

	public long getId() {
		return mId;
	}

	public void setId(long attachFileId) {
		mId = attachFileId;
	}

	public long getIssueId() {
		return mIssueId;
	}

	public void setIssueId(long issueId) {
		mIssueId = issueId;
	}

	public int getIssueType() {
		return mIssueType;
	}
	
	public String getIssueTypeStr() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(IssueTypeEnum.TYPE_STORY, "Story");
		map.put(IssueTypeEnum.TYPE_TASK, "Task");
		
		return map.get(mIssueType);
	}

	public void setIssueType(int issueType) {
		mIssueType = issueType;
	}

	public String getName() {
		return mName;
	}

	public void setName(String fileName) {
		mName = fileName;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String filePath) {
		mPath = filePath;
	}

	public long getCreateTime() {
		return mCreateTime;
	}
	
	public String getContentType() {
		return mContentType;
	}

	public void setCreateTime(long createTime) {
		mCreateTime = createTime;
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
		JSONObject object = new JSONObject();
		object
			.put(AttachFileEnum.ID, mId)
			.put(AttachFileEnum.NAME, mName)
//			.put(AttachFileEnum.PATH, mPath)
			.put(AttachFileEnum.ISSUE_ID, mIssueId)
			.put(AttachFileEnum.ISSUE_TYPE, mIssueType)
			.put(AttachFileEnum.CREATE_TIME, mCreateTime);
		return object;
	}

	public static class Builder {
		private long mId = -1;
		private long mIssueId = -1;
		private int mIssueType;
		private String mName;
		private String mPath;
		private String mContentType;
		private long mCreateTime;
		
		public AttachFileObject build() {
			return new AttachFileObject(this);
		}

		public Builder setAttachFileId(long attachFileId) {
			mId = attachFileId;
			return this;
		}

		public Builder setIssueId(long issueId) {
			mIssueId = issueId;
			return this;
		}
		
		public Builder setIssueType(int issueType) {
			mIssueType = issueType;
			return this;
		}
		
		public Builder setName(String fileName) {
			mName = fileName;
			return this;
		}
		
		public Builder setPath(String filePath) {
			mPath = filePath;
			return this;
		}
		
		public Builder setContentType(String contentType) {
			mContentType = contentType;
			return this;
		}
		
		public Builder setCreateTime(long createTime) {
			mCreateTime = createTime;
			return this;
		}
	}
}