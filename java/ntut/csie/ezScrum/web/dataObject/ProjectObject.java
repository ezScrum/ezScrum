package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * 舊的 table 中的 pid 即為新的 table 的 name
 * 舊的 table 中的 name 即為新的 table 的 displayname
 * 
 * @author cutecool
 * 
 */
public class ProjectObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;

	// 取得所有專案的資訊
	private long mId = DEFAULT_VALUE;
	private String mName = "";
	private String mDisplayName = "";
	private String mComment = "";
	private String mManager = "";
	private long mAttachFileSize = DEFAULT_VALUE;
	private long mCreateTime = DEFAULT_VALUE;
	private long mUpdateTime = DEFAULT_VALUE;

//	public ProjectObject(long id, String name, String displayName, String comment,
//			String manager, String attachFileSize, long createDate, long updateTime) {
//		mId = id;
//		mName = name;
//		setDisplayName(displayName);
//		setComment(comment);
//		setManager(manager);
//		setAttachFileSize(attachFileSize);
//		setCreateTime(createDate);
//		setUpdateTime(updateTime);
//	}
//
//	public ProjectObject(String name, String displayName, String comment, String manager, String attachFileSize) {
//		mName = name;
//		setDisplayName(displayName);
//		setComment(comment);
//		setManager(manager);
//		setAttachFileSize(attachFileSize);
//	}
//
//	public ProjectObject() {
//	}
	
	public ProjectObject(String name) {
		mName = name;
	}
	
	public ProjectObject(long id, String name) {
		mId = id;
		mName = name;
	}
	
	public String getName() {
		return mName;
	}

	public ProjectObject setDisplayName(String displayName) {
		mDisplayName = displayName;
		return this;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public ProjectObject setComment(String comment) {
		mComment = comment;
		return this;
	}

	public String getComment() {
		return mComment;
	}

	public ProjectObject setManager(String manager) {
		mManager = manager;
		return this;
	}

	public String getManager() {
		return mManager;
	}

	public ProjectObject setAttachFileSize(long attachFileSize) {
		mAttachFileSize = attachFileSize;
		return this;
	}

	public long getAttachFileSize() {
		return mAttachFileSize;
	}

	public long getId() {
		return mId;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public ProjectObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	public ProjectObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object
		        .put(ProjectEnum.ID, mId)
		        .put(ProjectEnum.NAME, mName)
		        .put(ProjectEnum.DISPLAY_NAME, mDisplayName)
		        .put(ProjectEnum.COMMENT, mComment)
		        .put(ProjectEnum.PRODUCT_OWNER, mManager)
		        .put(ProjectEnum.ATTATCH_MAX_SIZE, mAttachFileSize)
		        .put(ProjectEnum.CREATE_TIME, mCreateTime)
		        .put(ProjectEnum.UPDATE_TIME, mUpdateTime);
		return object;
	}

	public static ProjectObject get(long id) {
		return ProjectDAO.getInstance().get(id);
	}

	public static ProjectObject getProjectByName(String name) {
		return ProjectDAO.getInstance().getProjectByName(name);
	}

	public static ArrayList<ProjectObject> getProjects() {
		return ProjectDAO.getInstance().getProjects();
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
    public void reload() {
		if (recordExists()) {
			ProjectObject project = ProjectDAO.getInstance().get(mId);
			if (project != null) {
				resetData(project);
			}
		}
    }

	@Override
    public boolean delete() {
		boolean success = ProjectDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
		}
		return success;
    }
	
	private boolean recordExists() {
		return mId > 0;
	}
	
	private void resetData(ProjectObject project) {
		mId = project.getId();
		mName = project.getName();
		mDisplayName = project.getDisplayName();
		mComment = project.getComment();
		mAttachFileSize = project.getAttachFileSize();
		mManager = project.getManager();
		mCreateTime = project.getCreateTime();
		mUpdateTime = project.getUpdateTime();
	}
	
	private void doCreate() {
		mId = ProjectDAO.getInstance().create(this);
		try {
	        reload();
        } catch (Exception e) {
        }
	}
	
	private void doUpdate() {
		ProjectDAO.getInstance().update(this);
	}
}
