package ntut.csie.ezScrum.web.dataObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

public class ReleaseDocxObject {
	private ReleasePlanObject releasePlanDesc;
	private List<SprintPlanObject> sprintDescList;
	private HashMap<String, List<StoryObject>> stories;
	private LinkedHashMap<Long, List<TaskObject>> taskMap;
	private HashMap<String, Float> totalStoryPoints;
	
	public ReleaseDocxObject() {}

	public ReleasePlanObject getReleasePlanDesc() {
	    return releasePlanDesc;
    }

	public void setReleasePlanDesc(ReleasePlanObject releasePlanDesc) {
	    this.releasePlanDesc = releasePlanDesc;
    }

	public List<SprintPlanObject> getSprintDescList() {
	    return sprintDescList;
    }

	public void setSprintDescList(List<SprintPlanObject> sprintDescList) {
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

	public HashMap<String, Float> getTotalStoryPoints() {
	    return totalStoryPoints;
    }

	public void setTotalStoryPoints(HashMap<String, Float> totalStoryPoints) {
	    this.totalStoryPoints = totalStoryPoints;
    }
}
