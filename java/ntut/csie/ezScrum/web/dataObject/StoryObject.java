package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.iteration.core.IStory;

import org.apache.commons.lang.StringUtils;

public class StoryObject {
	public String id = "";
	public String name = "";
	public String notes = "";
	public String howToDemo = "";
	public String importance = "";
	public String value = "";
	public String estimation = "";
	public String status = "";
	public String sprint = "";
	public String release = "";
	public String description = "";
	public List<String> taskIDList = new ArrayList<String>();
	public List<TagObject> tagList = new ArrayList<TagObject>();
	public List<TaskObject> taskList = new ArrayList<TaskObject>();
	public List<HistoryObject> historyList = new ArrayList<HistoryObject>();
	
	public StoryObject() {
		
	}
	
	public StoryObject(IStory story) {
		id = Long.toString(story.getIssueID());
		name = story.getName();
		notes = story.getNotes();
		howToDemo = story.getHowToDemo();
		importance = story.getImportance();
		value = story.getValue();
		estimation = story.getEstimated();
		status = story.getStatus();
		sprint = story.getSprintID();
		release = story.getReleaseID();
		description = story.getDescription();
		tagList = story.getTags();
		
		for (long taskID : story.getChildrenID())
			taskIDList.add(Long.toString(taskID));
	}
	
	public StoryObject(IIssue story) {
		id = Long.toString(story.getIssueID());
		name = story.getSummary();
		notes = story.getNotes();
		howToDemo = story.getHowToDemo();
		importance = story.getImportance();
		value = story.getValue();
		estimation = story.getEstimated();
		status = story.getStatus();
		sprint = story.getSprintID();
		release = story.getReleaseID();
		description = story.getDescription();
		tagList = story.getTags();
		
		for (long taskID : story.getChildrenID())
			taskIDList.add(Long.toString(taskID));

		for (IIssueHistory history : story.getIssueHistories()) {
			historyList.add(new HistoryObject(history));
		}
	}
	
	public void addTask(TaskObject task) {
		taskList.add(task);
	}
	
	public StoryInformation toStoryInformation() {
		String tagIDs = StringUtils.join(tagList.toArray(), ",");
		return new StoryInformation(id, name, importance, estimation, value, howToDemo, notes, description, sprint, release, "");
	}
	
	public String toString() {
		String story = 
				"id: " + id + ", " + 
				 "name: " + name + ", " + 
				 "notes: " + notes + ", " + 
				 "howToDemo: " + howToDemo + ", " + 
				 "importance: " + importance + ", " + 
				 "value: " + value + ", " + 
				 "estimation: " + estimation + ", " + 
				 "status: " + status + ", " + 
				 "tagList: " + tagList.toString() + ", " +  
				 "taskIDList: " + taskIDList.toString() + ", " + 
				 "sprint: " + sprint;
		for (TaskObject task : taskList) {
			story += "\n" + task.toString();
		}
		return story;
	}
}
