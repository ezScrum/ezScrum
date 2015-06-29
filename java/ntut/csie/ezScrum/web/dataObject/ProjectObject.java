package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

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
	private long mId = DEFAULT_VALUE;
	private String mName = "";
	private String mDisplayName = "";
	private String mComment = "";
	private String mManager = "";
	private long mAttachFileSize = DEFAULT_VALUE;
	private long mCreateTime = DEFAULT_VALUE;
	private long mUpdateTime = DEFAULT_VALUE;
	
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
	
	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	/**
	 * Get project by id
	 * 
	 * @param id projectId
	 * @return ProjectObject
	 */
	public static ProjectObject get(long id) {
		return ProjectDAO.getInstance().get(id);
	}

	/**
	 * Get project by name
	 * 
	 * @param name projectName
	 * @return ProjectObject
	 */
	public static ProjectObject get(String name) {
		return ProjectDAO.getInstance().get(name);
	}

	public static ArrayList<ProjectObject> getAllProjects() {
		return ProjectDAO.getInstance().getAllProjects();
	}
	
	public ArrayList<AccountObject> getProjectMembers() {
		ArrayList<AccountObject> projectMembers = AccountDAO.getInstance().getProjectMembers(mId);
		return projectMembers;
	}
	
	public ArrayList<AccountObject> getProjectWorkers() {
		ArrayList<AccountObject> projectWorkers = AccountDAO.getInstance().getProjectWorkers(mId);
		return projectWorkers;
	}
	
	public ArrayList<StoryObject> getStoriesWithNoParent() {
		return StoryDAO.getInstance().getStoriesWithNoParent(mId);
	}
	
	// get all stories
	public ArrayList<StoryObject> getStories() {
		return StoryDAO.getInstance().getStoriesByProjectId(mId);
	}

	public ArrayList<TaskObject> getTasksWithNoParent() {
		return TaskDAO.getInstance().getTasksWithNoParent(mId);
	}
	
	public ScrumRole getScrumRole(RoleEnum role) {
		return ProjectDAO.getInstance().getScrumRole(mId, mName, role);
	}
	
	public void updateScrumRole(ScrumRole scrumRole) {
		ProjectDAO.getInstance().updateScrumRole(mId, 
				RoleEnum.valueOf(scrumRole.getRoleName()), scrumRole);
	}
	
	/**
	 * 透過 Tag Name 取得 Tag
	 * @param name
	 * @param ProjectId
	 * @return
	 */
	public TagObject getTagByName(String name){
		return TagDAO.getInstance().getTagInProjectByName(mId, name);
	}
	
	public ArrayList<TagObject> getTags() {
		return TagDAO.getInstance().getTagsByProjectId(mId);
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
			ProjectObject project = ProjectDAO.getInstance().get(mId);
			resetData(project);
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
	
	private boolean exists() {
		ProjectObject project = ProjectDAO.getInstance().get(mId);
		return project != null;
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
        reload();
	}
	
	private void doUpdate() {
		ProjectDAO.getInstance().update(this);
	}
}
