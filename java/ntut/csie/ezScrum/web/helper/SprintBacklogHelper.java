package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SprintBacklogTreeStructure;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;

import org.codehaus.jettison.json.JSONArray;

import com.google.appengine.repackaged.com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;

public class SprintBacklogHelper {
	private ProjectObject mProject;
	private SprintBacklogLogic mSprintBacklogLogic;
	private SprintBacklogMapper mSprintBacklogMapper;

	public SprintBacklogHelper(ProjectObject project) {
		mProject = project;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, -1);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogHelper(ProjectObject project, long sprintId) {
		mProject = project;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, sprintId);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	/**
	 * ----- Story -----
	 */

	public StoryObject getStory(long storyId) {
		return mSprintBacklogMapper.getStory(storyId);
	}

	public ArrayList<StoryObject> getStoriesSortedByIdInSprint() {
		return mSprintBacklogLogic.getStoriesSortedByIdInSprint();
	}

	public ArrayList<StoryObject> getStoriesSortedByImpInSprint() {
		return mSprintBacklogLogic.getStoriesSortedByImpInSprint();
	}

	/**
	 * Add exist story to sprint
	 * 
	 * @param serialStoriesId
	 */
	public void addExistingStory(ArrayList<Long> serialStoriesId) {
		long sprintId = mSprintBacklogMapper.getSprintId();
		if (sprintId > 0) {
			// Add story to sprint
			mSprintBacklogLogic.addStoriesToSprint(serialStoriesId, sprintId);
		}
	}

	/**
	 * Get existing stories by release id
	 * 
	 * @return IStory list
	 */
	public ArrayList<StoryObject> getExistingStories() {
		ArrayList<StoryObject> stories = null;
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(
				mProject);
		stories = productBacklogLogic.getExistingStories();
		return stories;
	}

	/**
	 * ----- Task -----
	 */
	public TaskObject addTask(long projectId, TaskInfo taskInfo) {
		long taskId = mSprintBacklogMapper.addTask(projectId, taskInfo);
		return TaskObject.get(taskId);
	}

	public void addExistingTasksToStory(String[] selectedTaskIds, long storyId) {
		// check story is existed
		StoryObject story = mSprintBacklogMapper.getStory(storyId);
		if (story == null) {
			throw new RuntimeException("Story#" + storyId + " is not existed.");
		}

		ArrayList<Long> serialTasksId = new ArrayList<Long>();
		for (String serialTaskId : selectedTaskIds) {
			serialTasksId.add(Long.parseLong(serialTaskId));
		}
		mSprintBacklogMapper.addExistingTasksToStory(serialTasksId, storyId);
	}

	public TaskObject getTask(long taskId) {
		return mSprintBacklogMapper.getTask(taskId);
	}
	
	public TaskObject getTask(long projectId, long serialTaskId) {
		return mSprintBacklogMapper.getTask(projectId, serialTaskId);
	}

	public ArrayList<TaskObject> getTasksByStoryId(long storyId) {
		return mSprintBacklogMapper.getTasksByStoryId(storyId);
	}
	
	public ArrayList<TaskObject> getTaskBySprintId(long sprintId){
		return mSprintBacklogMapper.getTasksInSprint();
	}

	public ArrayList<TaskObject> getDroppedTasks(long projectId) {
		return mSprintBacklogMapper.getDroppedTasks(projectId);
	}

	public void updateTask(TaskInfo taskInfo, String handlerUsername,
			String partnersUsername) {
		AccountObject handler = AccountObject.get(handlerUsername);
		long handlerId = -1;
		if (handler != null) {
			handlerId = handler.getId();
		}

		ArrayList<Long> partnersId = new ArrayList<Long>();
		for (String partnerUsername : partnersUsername.split(";")) {
			AccountObject partner = AccountObject.get(partnerUsername);
			if (partner != null) {
				partnersId.add(partner.getId());
			}
		}

		taskInfo.handlerId = handlerId;
		taskInfo.partnersId = partnersId;

		mSprintBacklogMapper.updateTask(taskInfo.id, taskInfo);
	}

	public void deleteTask(long taskId) {
		mSprintBacklogMapper.deleteTask(taskId);
	}

	public void dropTask(long taskId) {
		TaskObject task = TaskObject.get(taskId);
		// reset status, handler
		resetTask(taskId, task.getName(), task.getNotes(), null);
		// remove relation
		mSprintBacklogMapper.dropTask(taskId);
	}

	public void closeStory(long id, String name, String notes, String changeDate) {
		mSprintBacklogLogic.closeStory(id, name, notes, changeDate);
	}

	public void reopenStory(long id, String name, String notes,
			String changeDate) {
		mSprintBacklogLogic.reopenStory(id, name, notes, changeDate);
	}

	/**
	 * Task 從 Not Check Out -> Check Out path: CheckOutTask.do class:
	 * CheckOutTaskAction Test class: CheckOutTaskActionTest
	 */
	public void checkOutTask(long taskId, String name, String handler,
			String partners, String notes, String changeDate) {
		mSprintBacklogLogic.checkOutTask(taskId, name, handler, partners,
				notes, changeDate);
	}

	public void closeTask(long id, String name, String notes, int actual,
			String changeDate) {
		mSprintBacklogLogic.closeTask(id, name, notes, actual, changeDate);
	}

	public void reopenTask(long taskId, String name, String notes,
			String changeDate) {
		mSprintBacklogLogic.reopenTask(taskId, name, notes, changeDate);
	}

	/**
	 * Task 從 Check Out -> Not Check Out path: ResetTask.do class:
	 * ResetTaskAction Test class: ResetTaskActionTest
	 */
	public void resetTask(long id, String name, String notes, String changeDate) {
		mSprintBacklogLogic.resetTask(id, name, notes, changeDate);
	}

	/**
	 * ------ 處理 Action 的 response information ------
	 */

	public String getSprintBacklogListInfoText() {
		ArrayList<SprintBacklogTreeStructure> SBtree = new ArrayList<SprintBacklogTreeStructure>();
		if (mSprintBacklogMapper != null) {
			long sprintId = mSprintBacklogMapper.getSprintId();
			// 取得工作天數
			int availableDays = mSprintBacklogLogic.getSprintAvailableDays(sprintId);

			if (sprintId > 0) {
				ArrayList<StoryObject> stories = getStoriesSortedByImpInSprint();
				// 取得 Sprint 日期的 Column
				if (mSprintBacklogLogic.getCurrentDateColumns() == null)
					mSprintBacklogLogic.getSprintBacklogDates(mSprintBacklogMapper.getSprintStartDate(), availableDays);
				else
					mSprintBacklogLogic.getCurrentDateColumns();

				for (StoryObject story : stories) {
					SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(story, story.getTasks(),
							mSprintBacklogLogic.getCurrentDateList());
					SBtree.add(tree);
				}
			} else {
				return new JSONArray().toString();
			}
		}

		return (new Gson()).toJson(SBtree);
	}

	/**
	 * path: showSprintBacklog2.do class:
	 * ntut.csie.ezScrum.web.action.backlog.ShowSprintBacklogAction
	 */
	public String getShowSprintBacklogText() {
		String result;
		// 建立 this Sprint Store 的資料
		ArrayList<StoryObject> stories = null;
		long currentSprintId = 0;
		long releaseId = 0;
		double totalStoryPoints = 0.0d;
		double limitedPoint = 0.0d;
		double totalTaskPoints = 0.0d;
		String sprintGoal = "";
		if ((mSprintBacklogMapper != null)
				&& (mSprintBacklogMapper.getSprintId() > 0)) {
			// 存在一 current sprint
			stories = getStoriesSortedByImpInSprint();
			currentSprintId = mSprintBacklogMapper.getSprintId();
			SprintObject sprint = mSprintBacklogMapper.getSprint();
			totalStoryPoints = sprint.getTotalStoryPoints();
			limitedPoint = sprint.getLimitedPoint();
			totalTaskPoints = sprint.getTotalTaskPoints();

			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(
					mProject);
			releaseId = releasePlanHelper.getReleaseIdBySprintId(currentSprintId);

			sprintGoal = mSprintBacklogMapper.getSprintGoal();
			
			result = Translation.translateSprintBacklogToJson(stories,
					sprint.getSerialId(), totalStoryPoints, limitedPoint,
					totalTaskPoints, releaseId, sprintGoal);
		} else {
			stories = new ArrayList<StoryObject>();
			result = Translation.translateSprintBacklogToJson(stories,
					currentSprintId, totalStoryPoints, limitedPoint,
					totalTaskPoints, releaseId, sprintGoal);
		}
		return result;
	}

	/**
	 * path: AjaxGetSprintBacklogDateInfo.do class:
	 * ntut.csie.ezScrum.web.action.backlog.AjaxGetSprintBacklogDateInfoAction
	 */
	public String getAjaxGetSprintBacklogDateInfo() {
		String result = "";
		// 建立 DateColumnStore 的資料
		if ((mSprintBacklogMapper != null)
				&& (mSprintBacklogMapper.getSprintId() > 0)) {
			Date StartDate = mSprintBacklogMapper.getSprintStartDate();
			long sprintId = mSprintBacklogMapper.getSprintId();
			// 取得工作天數
			int availableDays = mSprintBacklogLogic
					.getSprintAvailableDays(sprintId);

			List<SprintBacklogDateColumn> cols = mSprintBacklogLogic
					.getSprintBacklogDates(StartDate, availableDays);

			result = (new Gson()).toJson(cols);
			result = "{\"Dates\":" + result + "}";
		} else {
			// default data for null sprint backlog
			result = "";
		}
		return result;
	}
	/**
	 * path: showSelectableTask.do class ShowSelectableTaskAction
	 */
	public StringBuffer getTasksInSprintResponseText(ArrayList<TaskObject> tasks){
		StringBuffer sb = new StringBuffer();
		sb.append("<SelectingTasks>");
		
		for(TaskObject task : tasks){
			sb.append("<Task>");
			sb.append("<Id>" + task.getSerialId() + "</Id>");
			sb.append("<Link></Link>");
			sb.append("<Name>" + TranslateSpecialChar.TranslateXMLChar(task.getName())+ "</Name>");
			//System.out.println(task.getStoryId());
			sb.append("<StoryId>" + task.getStoryId() + "</StoryId>");
			sb.append("<Estimate>" + task.getEstimate() + "</Estimate>");
			sb.append("<Status>" + task.getStatusString() + "</Status>");
			sb.append("</Task>");
		}
		sb.append("</SelectingTasks>");
		return sb;
	}
	
	/**
	 * path: AjaxShowStoryfromSprint.do class: AjaxShowStoryFromSprintAction
	 */
	public StringBuilder getStoriesInSprintResponseText(
			ArrayList<StoryObject> stories) {
		StringBuilder sb = new StringBuilder();
		sb.append("<ExistingStories>");

		for (StoryObject story : stories) {
			long sprintId = story.getSprintId();
			sb.append("<Story>");
			sb.append("<Id>" + story.getSerialId() + "</Id>");
			sb.append("<Link></Link>");
			sb.append("<Name>" + TranslateSpecialChar.TranslateXMLChar(story.getName())
					+ "</Name>");
			sb.append("<Value>" + story.getValue() + "</Value>");
			sb.append("<Importance>" + story.getImportance() + "</Importance>");
			sb.append("<Estimate>" + story.getEstimate() + "</Estimate>");
			sb.append("<Status>" + story.getStatusString() + "</Status>");
			sb.append("<Notes>" + TranslateSpecialChar.TranslateXMLChar(story.getNotes())
					+ "</Notes>");
			sb.append("<HowToDemo>"
					+ TranslateSpecialChar.TranslateXMLChar(story.getHowToDemo())
					+ "</HowToDemo>");
			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(
					mProject);
			long releaseId = releasePlanHelper.getReleaseIdBySprintId(sprintId);
			ReleaseObject release = ReleaseObject.get(releaseId);
			long serialReleaseId = -1;
			if (release != null) {
				serialReleaseId = release.getSerialId();
			}
			sb.append("<Release>" + serialReleaseId + "</Release>");
			SprintObject sprint = SprintObject.get(sprintId);
			long serialSprintId = -1;
			if (sprint != null) {
				serialSprintId = sprint.getSerialId();
			}
			sb.append("<Tag>"
					+ TranslateSpecialChar.TranslateXMLChar(Translation.Join(story.getTags(),
							",")) + "</Tag>");
			sb.append("</Story>");
		}
		sb.append("</ExistingStories>");

		return sb;
	}

}
