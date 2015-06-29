package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SprintBacklogTreeStructure;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;

import com.google.gson.Gson;

public class SprintBacklogHelper {
	private ProjectObject mProject;
	private SprintBacklogLogic mSprintBacklogLogic;
	private SprintBacklogMapper mSprintBacklogMapper;
	private long mSprintId;

	public SprintBacklogHelper(ProjectObject project) {
		mProject = project;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, -1);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogHelper(ProjectObject project, long sprintId) {
		mProject = project;
		try {
			mSprintId = sprintId;
			mSprintBacklogLogic = new SprintBacklogLogic(mProject, mSprintId);
		} catch (NumberFormatException e) {
			mSprintBacklogLogic = new SprintBacklogLogic(mProject, -1);
		}
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
		
		// check sprint is existed
		if (mSprintBacklogMapper == null && sprintId > 0) {
			throw new RuntimeException("Sprint#" + sprintId + " is not existed.");
		}
	}

	/**
	 * ----- Story -----
	 */

	public StoryObject getStory(long storyId) {
		return mSprintBacklogMapper.getStory(storyId);
	}

	public ArrayList<StoryObject> getStoryBySprintId(long sprintId) {
		return mSprintBacklogMapper.getStoriesBySprintId(sprintId);
	}

	public ArrayList<StoryObject> getStoriesByImportance() {
		return mSprintBacklogLogic.getStoriesByImp();
	}

	/**
	 * Add exist story to sprint
	 * 
	 * @param storiesId
	 */
	public void addExistingStory(ArrayList<Long> storiesId) {
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(
				mProject);

		if ((mSprintId != 0) && (mSprintId != -1)) {
			// 將 Story 加入 Sprint 當中
			productBacklogLogic.addStoriesToSprint(storiesId, mSprintId);
		}
	}

	/**
	 * Get existing stories by release id
	 * 
	 * @return IStory list
	 */
	public ArrayList<StoryObject> getExistingStories() {
		ArrayList<StoryObject> stories = null;
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mProject);
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
		
		ArrayList<Long> tasksId = new ArrayList<Long>();
		for (String taskId : selectedTaskIds) {
			tasksId.add(Long.parseLong(taskId));
		}
		mSprintBacklogMapper.addExistingTasksToStory(tasksId, storyId);
	}

	public TaskObject getTask(long taskId) {
		return mSprintBacklogMapper.getTask(taskId);
	}

	public ArrayList<TaskObject> getTasksByStoryId(long storyId) {
		return mSprintBacklogMapper.getTasksByStoryId(storyId);
	}

	public ArrayList<TaskObject> getTasksWithNoParent(long projectId) {
		return mSprintBacklogMapper.getTasksWithNoParent(projectId);
	}

	public void updateTask(TaskInfo taskInfo, String handlerUsername,
			String partnersUsername) {
		AccountObject handler = AccountObject.get(handlerUsername);
		long handlerId = -1;
		if (handler != null) {
			handlerId = handler.getId();
		}

		ArrayList<Long> partnersId = new ArrayList<Long>();
		for (String parnerUsername : partnersUsername.split(";")) {
			AccountObject partner = AccountObject.get(parnerUsername);
			if (partner != null) {
				partnersId.add(partner.getId());
			}
		}

		taskInfo.handlerId = handlerId;
		taskInfo.partnersId = partnersId;

		mSprintBacklogMapper.updateTask(taskInfo.taskId, taskInfo);
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

	public void reopenStory(long id, String name, String notes, String changeDate) {
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
			// 取得工作天數
			int availableDays = mSprintBacklogLogic
					.getSprintAvailableDays(mSprintId);

			if (mSprintBacklogMapper.getSprintId() > 0) {
				ArrayList<StoryObject> stories = getStoriesByImportance();
				// 取得 Sprint 日期的 Column
				if (mSprintBacklogLogic.getCurrentDateColumns() == null)
					mSprintBacklogLogic.calculateSprintBacklogDateList(
							mSprintBacklogMapper.getSprintStartDate(),
							availableDays);
				else
					mSprintBacklogLogic.getCurrentDateColumns();

				for (StoryObject story : stories) {
					SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
							story, story.getTasks(),
							mSprintBacklogLogic.getCurrentDateList());
					SBtree.add(tree);
				}
			} else {
				// null sprint backlog
				SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure();
				SBtree.add(tree);
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
		int releaseId = 0;
		double totalStoryPoints = 0.0d;
		double limitedPoint = 0.0d;
		double totalTaskPoints = 0.0d;
		String sprintGoal = "";
		if ((mSprintBacklogMapper != null)
				&& (mSprintBacklogMapper.getSprintId() > 0)) {
			// 存在一 current sprint
			stories = getStoriesByImportance();
			currentSprintId = mSprintBacklogMapper.getSprintId();
			totalStoryPoints = mSprintBacklogLogic.getTotalStoryPoints();
			limitedPoint = mSprintBacklogMapper.getLimitedPoint();
			totalTaskPoints = mSprintBacklogLogic.getTaskEstimatePoints();

			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
			releaseId = Integer.parseInt(releasePlanHelper.getReleaseID(currentSprintId));

			sprintGoal = mSprintBacklogMapper.getSprintGoal();

			result = Translation.translateSprintBacklogToJson(stories,
					currentSprintId, totalStoryPoints, limitedPoint,
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
			// 取得工作天數
			int availableDays = mSprintBacklogLogic.getSprintAvailableDays(mSprintId);

			List<SprintBacklogDateColumn> cols = mSprintBacklogLogic.calculateSprintBacklogDateList(StartDate, availableDays);

			result = (new Gson()).toJson(cols);
			result = "{\"Dates\":" + result + "}";
		} else {
			// default data for null sprint backlog
			result = "";
		}
		return result;
	}

	/**
	 * path: AjaxShowStoryfromSprint.do class: AjaxShowStoryFromSprintAction
	 */
	public StringBuilder getStoriesInSprintResponseText(
			ArrayList<StoryObject> stories) {
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		sb.append("<ExistingStories>");

		for (StoryObject story : stories) {
			long sprintId = story.getSprintId();
			sb.append("<Story>");
			sb.append("<Id>" + story.getId() + "</Id>");
			sb.append("<Link></Link>");
			sb.append("<Name>" + tsc.TranslateXMLChar(story.getName()) + "</Name>");
			sb.append("<Value>" + story.getValue() + "</Value>");
			sb.append("<Importance>" + story.getImportance() + "</Importance>");
			sb.append("<Estimate>" + story.getEstimate() + "</Estimate>");
			sb.append("<Status>" + story.getStatusString() + "</Status>");
			sb.append("<Notes>" + tsc.TranslateXMLChar(story.getNotes()) + "</Notes>");
			sb.append("<HowToDemo>" + tsc.TranslateXMLChar(story.getHowToDemo()) + "</HowToDemo>");
			sb.append("<Release></Release>");
			sb.append("<Sprint>" + sprintId + "</Sprint>");
			sb.append("<Tag>" + tsc.TranslateXMLChar(Translation.Join(story.getTags(), ",")) + "</Tag>");
			sb.append("</Story>");
		}
		sb.append("</ExistingStories>");

		return sb;
	}

}
