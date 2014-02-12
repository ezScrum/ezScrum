package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataObject.ReleaseDocxObject;
import ntut.csie.ezScrum.web.dataObject.ReleasePlanObject;
import ntut.csie.ezScrum.web.dataObject.SprintPlanObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;

public class ReleasePlanWebService extends ProjectWebService {
	private UserSession mUserSession;
	private IProject mProject;
	private ReleasePlanHelper mReleasePlanHelper;
	
	public ReleasePlanWebService(UserObject user, String projectID) throws LogonException {
		super(user, projectID);
		initialize();
	}

	public ReleasePlanWebService(String username, String userpwd, String projectID) throws LogonException {
		super(username, userpwd, projectID);
		initialize();
	}

	private void initialize() {
		mUserSession = new UserSession(super.getAccount());
		mProject = super.getProjectList().get(0);
		mReleasePlanHelper = new ReleasePlanHelper(mProject);
	}
	
	/**
	 * 取得 ReleasePlan
	 * 
	 * @param releaseId
	 * @return
	 */
	public String getReleasePlan(String releaseId) {
		HashMap<String, Float> totalStoryPoints = new HashMap<String, Float>();
		HashMap<String, List<StoryObject>> stories = new HashMap<String, List<StoryObject>>();
		LinkedHashMap<Long, List<TaskObject>> taskMap = new LinkedHashMap<Long, List<TaskObject>>();
		// get sprints information of the release(release id)
		ReleasePlanObject releasePlan = new ReleasePlanObject(mReleasePlanHelper.getReleasePlan(releaseId));
		List<SprintPlanObject> sprintPlanList = releasePlan.getSprintPlan();
		return new Gson().toJson(getReleaseDocObject(releasePlan, sprintPlanList, stories, taskMap, totalStoryPoints));
	}

	private ReleaseDocxObject getReleaseDocObject(ReleasePlanObject releasePlan, List<SprintPlanObject> sprintPlanList, HashMap<String, List<StoryObject>> stories, LinkedHashMap<Long, List<TaskObject>> taskMap, HashMap<String, Float> totalStoryPoints) {
		ReleaseDocxObject releaseObject = new ReleaseDocxObject();
		releaseObject.setReleasePlanDesc(releasePlan);
		releaseObject.setSprintDescList(sprintPlanList);
		if (sprintPlanList != null) {
			for (SprintPlanObject desc : sprintPlanList) {	// set story, task, story points to release object
				String sprintId = desc.getId();
				SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession, sprintId);
				SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
				List<IIssue> issues = sprintBacklogLogic.getStoriesByImp();
				List<StoryObject> storyList = new ArrayList<StoryObject>();
				float total = 0;	// the sum of story points
				for (IIssue issue : issues) {
					total = total + Float.parseFloat(issue.getEstimated());
					storyList.add(new StoryObject(issue));
					IIssue[] taskIssueList = sprintBacklogMapper.getTaskInStory(issue.getIssueID());
					List<TaskObject> taskList = new LinkedList<TaskObject>();
					for (IIssue taskIssue : taskIssueList) taskList.add(new TaskObject(taskIssue));
					taskMap.put(issue.getIssueID(), taskList);
				}
				stories.put(sprintId, storyList);
				totalStoryPoints.put(sprintId, total);
			}
			releaseObject.setTaskMap(taskMap);
			releaseObject.setStories(stories);
			releaseObject.setTotalStoryPoints(totalStoryPoints);
		}
		return releaseObject;
    }
}
