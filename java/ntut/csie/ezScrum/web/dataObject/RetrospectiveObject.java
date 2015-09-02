package ntut.csie.ezScrum.web.dataObject;

import java.util.Date;

public class RetrospectiveObject {
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;
	private long mSprintId = DEFAULT_VALUE;

	private String mName = "";
	private String mDescription = "";
	private String mType = "";
	private String mStatus = "";

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
	
	public RetrospectiveObject setName(String name) {
		mName = name;
		return this;
	}
	
	public RetrospectiveObject setDescription(String description) {
		mDescription = description;
		return this;
	}
	
}
