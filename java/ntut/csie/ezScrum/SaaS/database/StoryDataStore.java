package ntut.csie.ezScrum.SaaS.database;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class StoryDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	@Persistent
    private Long id;
	@Persistent
    private String name;
	@Persistent
    private int statusValue;
	@Persistent
    private String value;
	@Persistent
    private String importance;
	@Persistent
    private String estimation;
	@Persistent
    private String howToDemo;
	@Persistent
    private String notes;
	@Persistent
	private String projectId;
	@Persistent
	private String sprintId;
	@Persistent
	private String releaseId;
	@Persistent
	private List<String> Historylist;
	
	//	tag 過度作法
//	@Persistent
//	private List<TagDataStore> tags;
	@Persistent
	private List<String> tagNames;
	
	public StoryDataStore(Key key) {
		this.key = key;
		this.Historylist = new ArrayList<String>();
		this.tagNames = new ArrayList<String>();
    }
	
//	public StoryDataStore() {
//		this.Historylist = new ArrayList<String>();
//		this.tagNameList = new ArrayList<String>();
//	}

	public Key getKey() {
		return this.key;
	}

	public void setStoryId(String storyId) {
		this.id = Long.valueOf(storyId);
	}

	public String getStoryId() {
		return String.valueOf(this.id);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setStatusValue(int statusValue) {
		this.statusValue = statusValue;
	}

	public int getStatusValue() {
		return this.statusValue;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public String getImportance() {
		return this.importance;
	}

	public void setEstimation(String estimation) {
		this.estimation = estimation;
	}

	public String getEstimation() {
		return this.estimation;
	}

	public void setHowToDemo(String howToDemo) {
		this.howToDemo = howToDemo;
	}

	public String getHowToDemo() {
		return this.howToDemo;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;
	}

	public String getSprintId() {
		return this.sprintId;
	}

	public List<String> getHistorylist() {
		return this.Historylist;
	}	
	
	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
	
	public List<String> getTagsList() {
		return tagNames;
	}

	public void setTagsList(List<String> tagNamesList) {
		this.tagNames = tagNamesList;
	}
	
	public String getProjectId() {
		return projectId;
	}
	
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
