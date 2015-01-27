package ntut.csie.ezScrum.web.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SprintBacklogTreeStructure;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;

public class SprintBacklogHelper {
	private IProject mProject;
	private IUserSession mUserSession;
	private SprintBacklogLogic mSprintBacklogLogic;
	private SprintBacklogMapper mSprintBacklogMapper;
	private String mSprintId;

	/**
	 * for web service
	 */
	public SprintBacklogHelper(IProject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession,
				null);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogHelper(IProject project, IUserSession userSession,
			String sprintId) {
		mProject = project;
		mUserSession = userSession;
		mSprintId = sprintId;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession,
				mSprintId);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public IIssue getIssue(long issueId) {
		return mSprintBacklogMapper.getStory(issueId);
	}

	public IIssue[] getStoryInSprint(String sprintId) {
		return mSprintBacklogMapper.getStoriesBySprintId(Long.parseLong(sprintId));
	}

	public IIssue[] getTaskInStory(String storyId) {
		if (mSprintBacklogMapper != null) {
			return mSprintBacklogMapper.getTasksByStoryId(Long.parseLong(storyId));
		}
		return null;
	}
	
	public ArrayList<TaskObject> getWildTasks(long projectId)
			throws SQLException {
		return mSprintBacklogMapper.getTasksWithNoParent(projectId);
	}

	public void addExistedTask(String storyId, String[] selectedTaskIds) {
		long[] taskIds = new long[selectedTaskIds.length];
		for (int i = 0; i < selectedTaskIds.length; i++) {
			taskIds[i] = Long.parseLong(selectedTaskIds[i]);
		}
		if (mSprintBacklogMapper != null) {
			Date date = DateUtil.dayFillter(DateUtil.getToday(),
					DateUtil._8DIGIT_DATE_2);
			mSprintBacklogMapper.addExistingTask(taskIds,
					Long.parseLong(storyId), date);
		}
	}

	public TaskObject createTaskInStory(TaskInfo taskInfo) {
		TaskObject task = null;
		if (mSprintBacklogMapper != null) {
			long taskId = mSprintBacklogLogic.addTask(taskInfo.projectId,
					taskInfo.name, taskInfo.notes, taskInfo.estiamte, taskInfo.handlerId, null,
					taskInfo.storyId, new Date());
			task = TaskObject.get(taskId);
		}
		return task;
	}

	/**
	 * 根據 id 取得 task
	 */
	public IIssue getTaskById(long id) {
		List<IIssue> tasks = mSprintBacklogMapper.getAllTasks();
		for (IIssue task : tasks) {
			if (task.getIssueID() == id) {
				return task;
			}
		}
		return null;
	}

	public void deleteTask(String taskId, String storyId) {
		if (mSprintBacklogMapper != null) {
			mSprintBacklogMapper.deleteTask(Long.parseLong(taskId),
					Long.parseLong(storyId));
		}
	}

	public void dropTask(String taskId, String storyId) {
		if (mSprintBacklogMapper != null) {
			mSprintBacklogMapper.dropTask(Long.parseLong(taskId),
					Long.parseLong(storyId));
		}
	}

	public boolean editTask(TaskObject task) {
		if (mSprintBacklogMapper != null) {
			Date date = DateUtil.dayFillter(DateUtil.getToday(),
					DateUtil._8DIGIT_DATE_2);
			// partner 暫時沒寫
			return mSprintBacklogLogic.editTask(Long.parseLong(task.id),
					task.name, task.estimation, task.remains, task.handler, "",
					task.actual, task.notes, date);
		}
		return false;
	}

	public List<IIssue> getStoriesByImportance() {
		return mSprintBacklogLogic.getStoriesByImp();
	}

	/**
	 * 取得尚未被施工的所有 Story
	 */
	public List<IStory> getExistedStories(String releaseId) throws SQLException {
		List<IStory> stories = null;
		ProductBacklogLogic productBacklogHelper = new ProductBacklogLogic(
				mUserSession, mProject);
		if ((mSprintId != null) && (!mSprintId.isEmpty())
				&& (!mSprintId.equals("-1"))) {
			if (Integer.parseInt(mSprintId) > 0) {
				// get release ID by sprint ID
				ReleasePlanHelper rphelper = new ReleasePlanHelper(mProject);
				releaseId = rphelper.getReleaseID(mSprintId);
				// get stories that exist in
				stories = productBacklogHelper.getAddableStories(mSprintId,
						releaseId);
			}
		} else if ((releaseId != null) && (!releaseId.isEmpty())
				&& (!releaseId.equals("-1"))) {
			// Select from Release Plan
			// get stories that exist in
			if (Integer.parseInt(releaseId) > 0) {
				stories = productBacklogHelper.getAddableStories();
			}
		} else {
			stories = null;
		}
		return stories;
	}

	/**
	 * 新增以存在的 Story 至專案中
	 */
	public void addExistedStory(List<Long> list, String releaseId) {
		ProductBacklogLogic helper = new ProductBacklogLogic(mUserSession,
				mProject);

		if ((mSprintId != null) && (!mSprintId.isEmpty())
				&& (!mSprintId.equals("-1"))) {
			// 將 Story 加入 Sprint 當中
			helper.addIssueToSprint(list, mSprintId);

			// 檢查 Sprint 是否有存在於某個 Release 中
			ReleasePlanHelper releasePlan = new ReleasePlanHelper(mProject);
			String sprintReleaseId = releasePlan.getReleaseID(mSprintId);

			// 如果有的話，將所有 Story 加入 Release
			if (!(sprintReleaseId.equals("0"))) {
				helper.addReleaseTagToIssue(list, sprintReleaseId);
			}
		} else {
			helper.addReleaseTagToIssue(list, releaseId);
		}
	}

	/**
	 * ------ 處理Action的response information ------
	 */

	/**
	 * path: showSprintBacklogTreeListInfo.do class:
	 * ntut.csie.ezScrum.web.action.backlog.ShowSprintBacklogListInfoAction
	 * 
	 * @return
	 */
	public String getSprintBacklogListInfoText() {
		List<SprintBacklogTreeStructure> SBtree = new ArrayList<SprintBacklogTreeStructure>();
		if (mSprintBacklogMapper != null) {
			// 取得工作天數
			int availableDays = mSprintBacklogLogic
					.getSprintAvailableDays(mSprintId);

			if (this.mSprintBacklogMapper.getSprintPlanId() > 0) {
				List<IIssue> stories = getStoriesByImportance();
				Map<Long, IIssue[]> map = mSprintBacklogMapper.getTasksMap();

				// 取得 Sprint 日期的 Column
				List<SprintBacklogDateColumn> cols = null;
				if (this.mSprintBacklogLogic.getCurrentDateColumns() == null)
					cols = mSprintBacklogLogic.calculateSprintBacklogDateList(
							this.mSprintBacklogMapper.getSprintStartDate(),
							availableDays);
				else
					cols = mSprintBacklogLogic.getCurrentDateColumns();

				for (IIssue story : stories) {
					SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
							story, map.get(Long.valueOf(story.getIssueID())),
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
		List<IIssue> issues = null;
		int currentSprintId = 0;
		int releaseId = 0;
		double currentPoint = 0.0d;
		double limitedPoint = 0.0d;
		double taskPoint = 0.0d;
		String sprintGoal = "";
		if ((mSprintBacklogMapper != null)
				&& (mSprintBacklogMapper.getSprintPlanId() > 0)) {
			// 存在一 current sprint
			issues = getStoriesByImportance();
			currentSprintId = mSprintBacklogMapper.getSprintPlanId();
			currentPoint = mSprintBacklogLogic
					.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
			limitedPoint = mSprintBacklogMapper.getLimitedPoint();
			taskPoint = mSprintBacklogLogic
					.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);

			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(
					mProject);
			releaseId = Integer.parseInt(releasePlanHelper.getReleaseID(Integer
					.toString(currentSprintId)));

			sprintGoal = mSprintBacklogMapper.getSprintGoal();

			result = new Translation().translateStoryToJson(issues,
					currentSprintId, currentPoint, limitedPoint, taskPoint,
					releaseId, sprintGoal);
		} else {
			issues = new ArrayList<IIssue>();
			result = new Translation().translateStoryToJson(issues,
					currentSprintId, currentPoint, limitedPoint, taskPoint,
					releaseId, sprintGoal);
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
				&& (mSprintBacklogMapper.getSprintPlanId() > 0)) {
			Date StartDate = mSprintBacklogMapper.getSprintStartDate();
			// 取得工作天數
			int availableDays = mSprintBacklogLogic
					.getSprintAvailableDays(mSprintId);

			List<SprintBacklogDateColumn> cols = mSprintBacklogLogic
					.calculateSprintBacklogDateList(StartDate, availableDays);

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
			List<? extends IIssue> stories) {
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		sb.append("<ExistingStories>");

		for (IIssue issue : stories) {
			String releaseId = issue.getReleaseID();
			if (releaseId.equals("") || releaseId.equals("0")
					|| releaseId.equals("-1"))
				releaseId = "None";

			String sprintId = issue.getSprintID();
			if (sprintId.equals("") || sprintId.equals("0")
					|| sprintId.equals("-1"))
				sprintId = "None";
			sb.append("<Story>");
			sb.append("<Id>" + issue.getIssueID() + "</Id>");
			sb.append("<Link>" + tsc.TranslateXMLChar(issue.getIssueLink())
					+ "</Link>");
			sb.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary())
					+ "</Name>");
			sb.append("<Value>" + issue.getValue() + "</Value>");
			sb.append("<Importance>" + issue.getImportance() + "</Importance>");
			sb.append("<Estimate>" + issue.getEstimated() + "</Estimate>");
			sb.append("<Status>" + issue.getStatus() + "</Status>");
			sb.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes())
					+ "</Notes>");
			sb.append("<HowToDemo>"
					+ tsc.TranslateXMLChar(issue.getHowToDemo())
					+ "</HowToDemo>");
			sb.append("<Release>" + releaseId + "</Release>");
			sb.append("<Sprint>" + sprintId + "</Sprint>");
			sb.append("<Tag>"
					+ tsc.TranslateXMLChar(new Translation().Join(
							issue.getTags(), ",")) + "</Tag>");
			sb.append("</Story>");
		}
		sb.append("</ExistingStories>");

		return sb;
	}

	/**
	 * path: AjaxRemoveSprintTask.do class: AjaxRemoveSprintTaskAction Test
	 * class: AjaxRemoveSprintTaskTest
	 */
	public void removeTask(long issueId, long parentId) {
		String name = getIssue(issueId).getSummary();

		// reset status, handler
		resetTask(issueId, name, null, "");
		// remove relation
		dropTask(String.valueOf(issueId), String.valueOf(parentId));
	}

	/**
	 * Task 從 Not Check Out -> Check Out path: CheckOutTask.do class:
	 * CheckOutTaskAction Test class: CheckOutTaskActionTest
	 */
	public void checkOutTask(long issueId, String name, String handler,
			String partners, String notes, String changeDate) {
		mSprintBacklogLogic.checkOutTask(issueId, name, handler, partners,
				notes, changeDate);
	}

	/**
	 * Task 從 Check Out -> Not Check Out path: ResetTask.do class:
	 * ResetTaskAction Test class: ResetTaskActionTest
	 */
	public void resetTask(long id, String name, String bugNote,
			String changeDate) {
		if (mSprintBacklogMapper != null) {
			mSprintBacklogMapper.resetTask(id, name, bugNote, changeDate);
		}
		TaskObject taskObj = new TaskObject(getIssue(id));
		editTask(taskObj);
	}

	/**
	 * Story 從 Not Check Out -> Done 或是 Task 從 Check Out -> Done path:
	 * DoneIssue.do class: DoneIssueAction Test class: DoneIssueActionTest
	 */
	public void doneIssue(long issueId, int issueType, String name, String bugNote,
			String changeDate, String ActualHour) {
		mSprintBacklogLogic.doneIssue(issueId, issueType, name, bugNote, changeDate,
				ActualHour);
	}

	/**
	 * Story 從 Done -> Not Check Out 或是 Task 從 Done -> Check Out path:
	 * ReopenIssue.do class: ReopenIssueAction Test class: ReopenIssueActionTest
	 */
	public void reopenIssue(long issueId, String name, String bugNote,
			String changeDate) {
		mSprintBacklogMapper.reopenIssue(issueId, name, bugNote, changeDate);
	}
}
