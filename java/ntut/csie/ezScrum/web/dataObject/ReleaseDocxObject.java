package ntut.csie.ezScrum.web.dataObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ReleaseDocxObject {
	private ReleasePlanObject releasePlanDesc;
	private List<SprintObject> sprintDescList;
	private HashMap<String, List<StoryObject>> stories;
	private LinkedHashMap<Long, List<TaskObject>> taskMap;
	private HashMap<String, Double> totalStoryPoints;
	
	public ReleaseDocxObject() {}

	public ReleasePlanObject getReleasePlanDesc() {
	    return releasePlanDesc;
    }

	public void setReleasePlanDesc(ReleasePlanObject releasePlanDesc) {
	    this.releasePlanDesc = releasePlanDesc;
    }

	public List<SprintObject> getSprintDescList() {
	    return sprintDescList;
    }

	public void setSprintDescList(List<SprintObject> sprintDescList) {
	    this.sprintDescList = sprintDescList;
    }

	public HashMap<String, List<StoryObject>> getStories() {
	    return stories;
    }

	public void setStories(HashMap<String, List<StoryObject>> stories) {
	    this.stories = stories;
    }

	public LinkedHashMap<Long, List<TaskObject>> getTaskMap() {
	    return taskMap;
    }

	public void setTaskMap(LinkedHashMap<Long, List<TaskObject>> taskMap) {
	    this.taskMap = taskMap;
    }

	public HashMap<String, Double> getTotalStoryPoints() {
	    return totalStoryPoints;
    }

	public void setTotalStoryPoints(HashMap<String, Double> totalStoryPoints) {
	    this.totalStoryPoints = totalStoryPoints;
    }
}
