package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.web.databasEnum.TagEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TagObject implements IBaseObject{
	private final static int DEFAULT_VALUE = -1;
	private long mId = -1;
	private long mProjectId = -1;
	private long mCreateTime = -1;
	private long mUpdateTime = -1;
	private String mName = "";
	
	public TagObject(long id, String name, long projectId) {
		mId = id;
		mName = name;
		mProjectId = projectId;
	}

	public TagObject(String name, long projectId) {
		mName = name;
		mProjectId = projectId;
	}
	
	public long getId() {
		return mId;
	}
	
	public long getProjectId() {
		return mProjectId;
	}
	
	public TagObject setProjectId(long id) {
		mProjectId = id;
		return this;
	}
	
	public String getName() {
		return mName;
	}
	
	public TagObject setName(String tagName) {
		mName = tagName;
		return this;
	}
	
	public long getCreateTime() {
		return mCreateTime;
	}

	public TagObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}
	
	public long getUpdateTime() {
		return mUpdateTime;
	}

	public TagObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
	}
	
	/**
	 * get Tag by tagId
	 */
	public static TagObject get(long id){
		return TagDAO.getInstance().get(id);
	}
	
	/**
	 * 透過 Tag Name 取得 Tag
	 * @param name
	 * @param ProjectId
	 * @return
	 */
	public static TagObject getTagInProjectByName(String name, long projectId){
		return TagDAO.getInstance().getTagInProjectByName(projectId, name);
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
			TagObject tag = TagDAO.getInstance().get(mId);
			resetData(tag);
		}
	}

	@Override
	public boolean delete() {
		boolean success = TagDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
		}
		return success;
	}

	private boolean exists() {
		TagObject tag = TagDAO.getInstance().get(mId);
		return tag != null;
	}
	
	private void resetData(TagObject tag) {
		mId = tag.getId();
		mName = tag.getName();
		mProjectId = tag.getProjectId();
		mCreateTime = tag.getCreateTime();
	}
	
	private void doCreate() {
		mId = TagDAO.getInstance().create(this);
		reload();
	}

	private void doUpdate() {
		TagDAO.getInstance().update(this);
	}

	@Override
    public JSONObject toJSON() throws JSONException {
		JSONObject tagJsonObject = new JSONObject();
		tagJsonObject.put(TagEnum.ID, mId);
		tagJsonObject.put(TagEnum.NAME, mName);
		tagJsonObject.put(TagEnum.PROJECT_ID, mProjectId);
		tagJsonObject.put(TagEnum.CREATE_TIME, mCreateTime);
		tagJsonObject.put(TagEnum.UPDATE_TIME, mUpdateTime);
	    return tagJsonObject;
    }

	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}
}
