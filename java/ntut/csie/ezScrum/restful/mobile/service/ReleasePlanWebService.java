package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseDocxObject;
import ntut.csie.ezScrum.web.dataObject.ReleasePlanObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import com.google.gson.Gson;

public class ReleasePlanWebService extends ProjectWebService {
	private ReleasePlanHelper mReleasePlanHelper;
	private SprintPlanHelper mSprintPlanHelper;
	private ProjectObject mProject;
	
	public ReleasePlanWebService(AccountObject user, String projectName) throws LogonException {
		super(user, projectName);
		initialize();
	}

	public ReleasePlanWebService(String username, String userPwd, String projectName) throws LogonException {
		super(username, userPwd, projectName);
		initialize();
	}

	private void initialize() {
		mProject = getAllProjects().get(0);
		mReleasePlanHelper = new ReleasePlanHelper(mProject);
		mSprintPlanHelper = new SprintPlanHelper(mProject);
	}

	/**
	 * 取得專案底下所有的Release plan
	 * @return
	 */
	public String getAllReleasePlan() {
		List<IReleasePlanDesc> releaseDescs = mReleasePlanHelper.loadReleasePlansList();
		return new Gson().toJson(releaseDescs);
	}
	
	/**
	 * 取得專案底下所有的Release plan with all item
	 * @return
	 * @throws SQLException 
	 */
	public String getAllReleasePlanWithAllItem() throws SQLException {
		List<IReleasePlanDesc> releaseDescs = mReleasePlanHelper.loadReleasePlansList();
		List<ReleasePlanObject> releases = new ArrayList<ReleasePlanObject>();
		for (IReleasePlanDesc releaseDesc : releaseDescs) {
			ReleasePlanObject releaseObject = new ReleasePlanObject(releaseDesc);
			List<SprintObject> sprints = releaseObject.getSprintPlan();
			List<SprintObject> sprintsWithAllItem = new ArrayList<SprintObject>();
			for (SprintObject sprint : sprints) {
				sprintsWithAllItem.add(mSprintPlanHelper.getSprint(sprint.id));
			}
			releaseObject.setSprintPlan(sprintsWithAllItem);
			releases.add(releaseObject);
		}
		return new Gson().toJson(releases);
	}

	/**
	 * 取得 ReleasePlan
	 * 
	 * @param releaseId
	 * @return
	 * @throws SQLException 
	 */
	public String getReleasePlan(String releaseId) throws SQLException {
		HashMap<String, Double> totalStoryPoints = new HashMap<String, Double>();
		HashMap<String, List<StoryObject>> stories = new HashMap<String, List<StoryObject>>();
		LinkedHashMap<Long, List<TaskObject>> taskMap = new LinkedHashMap<Long, List<TaskObject>>();
		// get sprints information of the release(release id)
		ReleasePlanObject releasePlan = new ReleasePlanObject(mReleasePlanHelper.getReleasePlan(releaseId));
		List<SprintObject> sprintPlanList = releasePlan.getSprintPlan();
		return new Gson().toJson(getReleaseDocObject(releasePlan, sprintPlanList, stories, taskMap, totalStoryPoints));
	}

	/**
	 * 將  StoryObject, TaskObject 輸出成 ReleaseDocxObject
	 * @throws SQLException 
	 */
	private ReleaseDocxObject getReleaseDocObject(ReleasePlanObject releasePlan, List<SprintObject> sprintPlanList, HashMap<String, List<StoryObject>> stories,
	        LinkedHashMap<Long, List<TaskObject>> taskMap, HashMap<String, Double> totalStoryPoints) throws SQLException {
		ReleaseDocxObject releaseObject = new ReleaseDocxObject();
		releaseObject.setReleasePlanDesc(releasePlan);
		releaseObject.setSprintDescList(sprintPlanList);
		if (sprintPlanList != null) {
			for (SprintObject desc : sprintPlanList) {	// set story, task, story points to release object
				long sprintId = Long.parseLong(desc.id);
				SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, sprintId);
				ArrayList<StoryObject> storyList = sprintBacklogLogic.getStoriesByImp();
				double total = sprintBacklogLogic.getTotalStoryPoints();	// the sum of story points
				stories.put(String.valueOf(sprintId), storyList);
				totalStoryPoints.put(String.valueOf(sprintId), total);
			}
			releaseObject.setTaskMap(taskMap);
			releaseObject.setStories(stories);
			releaseObject.setTotalStoryPoints(totalStoryPoints);
		}
		return releaseObject;
	}
}
