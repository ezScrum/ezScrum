package ntut.csie.ezScrum.SaaS.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ProjectDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	@Persistent
	private String name;
	@Persistent
	private String DisplayName;
	@Persistent
	private String Comment;
	@Persistent	
	private String Manager;
	@Persistent	
	private Date CreateDate;	
	@Persistent
	private String newIssueID;
	@Persistent
	private String newTagID;
	
	// link with other JDO
	@Persistent
	private List<TagDataStore> tags;
	@Persistent
	private List<TaskDataStore> tasks;
	@Persistent	
	private List<StoryDataStore> stories;
	@Persistent(mappedBy = "project")
	private List<SprintDataStore> sprints;
	@Persistent(mappedBy = "project")
	private List<ReleaseDataStore> releases;	
	@Persistent(mappedBy = "project")
	private List<ScrumRoleDataStore> scrumRoles;	
	
	public ProjectDataStore(Key key) {
		this.key = key;
		this.stories = new ArrayList<StoryDataStore>();
		this.sprints = new ArrayList<SprintDataStore>();
		this.tasks = new ArrayList<TaskDataStore>();
		this.scrumRoles = new ArrayList<ScrumRoleDataStore>();
		this.releases = new ArrayList<ReleaseDataStore>();
		this.tags = new ArrayList<TagDataStore>();
	}

	public Key getKey() {
		return key;
	}
	
    public void setKey(Key key) {
        this.key = key;
    }
	
	public List<StoryDataStore> getStories() {
		return this.stories;
	}
	
	public List<SprintDataStore> getSprints() {
		return this.sprints;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setTasks(List<TaskDataStore> tasks) {
		this.tasks = tasks;
	}

	public List<TaskDataStore> getTasks() {
		return this.tasks;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public String getComment() {
		return Comment;
	}

	public void setManager(String manager) {
		Manager = manager;
	}

	public String getManager() {
		return Manager;
	}

	public void setDisplayName(String displayName) {
		DisplayName = displayName;
	}

	public String getDisplayName() {
		return DisplayName;
	}

	public void setCreateDate(Date createDate) {
		CreateDate = createDate;
	}

	public Date getCreateDate() {
		return CreateDate;
	}
	
	public List<ScrumRoleDataStore> getScrumRoles() {
		// 固定順序: PO -> ScrumMaster -> ScrumTeam -> Stakeholder -> Guest
		return scrumRoles;
	}
	
	public String getNewIssueID() {
		return newIssueID;
	}
	
	public void setNewIssueID(String newIssueID) {
		this.newIssueID = newIssueID;
	}

	public List<ReleaseDataStore> getReleases() {
		return releases;
	}
	
	public List<TagDataStore> getTags() {
		return tags;
	}

	public void setTags(List<TagDataStore> tags) {
		this.tags = tags;
	}

	public String getNewTagID() {
		return newTagID;
	}

	public void setNewTagID(String newTagID) {
		this.newTagID = newTagID;
	}
	
}
