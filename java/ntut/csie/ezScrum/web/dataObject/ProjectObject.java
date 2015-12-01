package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.dao.ReleaseDAO;
import ntut.csie.ezScrum.dao.SprintDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.dao.UnplanDAO;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;

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
	private final static int DEFAULT_FILE_SIZE = 2;
	private long mId = DEFAULT_VALUE;
	private String mName = "";
	private String mDisplayName = "";
	private String mComment = "";
	private String mManager = "";
	private long mAttachFileSize = DEFAULT_FILE_SIZE;
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
		JSONObject projectJson = new JSONObject();
		projectJson
		        .put(ProjectEnum.ID, mId)
		        .put(ProjectEnum.NAME, mName)
		        .put(ProjectEnum.DISPLAY_NAME, mDisplayName)
		        .put(ProjectEnum.COMMENT, mComment)
		        .put(ProjectEnum.PRODUCT_OWNER, mManager)
		        .put(ProjectEnum.ATTATCH_MAX_SIZE, mAttachFileSize)
		        .put(ProjectEnum.CREATE_TIME, mCreateTime)
		        .put(ProjectEnum.UPDATE_TIME, mUpdateTime);
		return projectJson;
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
	
	public ArrayList<StoryObject> getDroppedStories() {
		return StoryDAO.getInstance().getDroppedStories(mId);
	}
	
	// get all stories
	public ArrayList<StoryObject> getStories() {
		return StoryDAO.getInstance().getStoriesByProjectId(mId);
	}
	
	//get all unplans
	public ArrayList<UnplanObject> getUnplans() {
		return UnplanDAO.getInstance().getUnplansByProjectId(mId);
	}

	public ArrayList<TaskObject> getDroppedTasks() {
		return TaskDAO.getInstance().getDroppedTasks(mId);
	}

	public ArrayList<SprintObject> getSprints() {
		return SprintDAO.getInstance().getSprintsByProjectId(mId);
	}
	
	/**
	 * 取得目前時間所在的 sprint
	 * @return
	 */
	public SprintObject getCurrentSprint() {
		Date currentTime = new Date();
		ArrayList<SprintObject> sprints = SprintDAO.getInstance().getSprintsByProjectId(mId);
		if (sprints.isEmpty()) {
			return null;
		} else {
			for (SprintObject sprint : sprints) {
				if (sprint.contains(currentTime)) {
					return sprint;
				}
			}
			return getLatestSprint();
		}
	}
	
	/**
	 * 取得最新的 sprint (目前的時間點可能沒有 sprint)
	 * @return
	 */
	public SprintObject getLatestSprint() {
		ArrayList<SprintObject> sprints = SprintDAO.getInstance().getSprintsByProjectId(mId);
		if (sprints.isEmpty()) {
			return null;
		} else {
			// Sort Sprints by SprintId in descent
			Collections.sort(sprints, new Comparator<SprintObject>() {
				@Override
				public int compare(SprintObject o1, SprintObject o2) {
					return (int) (o2.getId() - o1.getId());
				}
			});
			return sprints.get(0);
		}
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
	
	public ArrayList<ReleaseObject> getReleases() {
		return ReleaseDAO.getInstance().getReleasesByProjectId(mId);
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
	
	@Override
	public boolean exists() {
		ProjectObject projectById = ProjectDAO.getInstance().get(mId);
		ProjectObject projectByName = ProjectDAO.getInstance().get(mName);
		return projectById != null || projectByName != null;
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
		SerialNumberObject serialNumber = new SerialNumberObject(mId);
		serialNumber.setReleaseId(0)
	    		    .setRetrospectiveId(0)
	    		    .setSprintId(0)
	    		    .setStoryId(0)
	    		    .setTaskId(0)
	    		    .setUnplanId(0);
		serialNumber.save();
        reload();
	}
	
	private void doUpdate() {
		mUpdateTime = System.currentTimeMillis();
		ProjectDAO.getInstance().update(this);
	}
}
