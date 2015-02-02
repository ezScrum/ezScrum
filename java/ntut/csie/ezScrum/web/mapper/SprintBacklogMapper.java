package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintPlanLogic;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SprintBacklogMapper {
	private static Log log = LogFactory.getLog(SprintBacklogMapper.class);
	private int mSprintPlanId = 0;
	private IProject mIProject;
	private ProjectObject mProject;
	private ISprintPlanDesc mIterPlanDesc;
	private Date mStartDate;
	private Date mEndDate;
	private IUserSession mUserSession;
	private Configuration mConfig;
	private MantisService mMantisService;
	private double mLimitedPoint = 0;

	private ArrayList<IIssue> mStories = null;
	private ArrayList<TaskObject> mTasks = null;
	private ArrayList<IIssue> mDropedStories = null;

	// 用於紀錄Story與Task之間的Mapping
	LinkedHashMap<Long, ArrayList<TaskObject>> mMapStoryTasks = null;
	LinkedHashMap<Long, ArrayList<TaskObject>> mMapDropedStoryTasks = null;
	// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	private boolean mUpdateFlag = true;

	/**
	 * 若沒有指定的話,自動取得目前的 sprint#
	 * 
	 * @param project
	 * @param userSession
	 */
	public SprintBacklogMapper(IProject project, IUserSession userSession) {
		mIProject = project;
		mUserSession = userSession;
		SprintPlanLogic sprintPlanLogic = new SprintPlanLogic(project);
		mIterPlanDesc = sprintPlanLogic.loadCurrentPlan();
		if (mIterPlanDesc != null) {
			mSprintPlanId = Integer.parseInt(mIterPlanDesc.getID());
		}
		initSprintInformation();
		initConfig();
	}

	/**
	 * 取得指定的 sprint backlog
	 * 
	 * @param project
	 * @param userSession
	 * @param sprintId
	 */
	public SprintBacklogMapper(IProject project, IUserSession userSession,
			long sprintId) {
		mIProject = project;
		mUserSession = userSession;
		SprintPlanMapper mapper = new SprintPlanMapper(project);
		mIterPlanDesc = mapper.getSprintPlan(Long.toString(sprintId));
		mSprintPlanId = Integer.parseInt(mIterPlanDesc.getID());
		initSprintInformation();
		initConfig();
	}

	/**
	 * 初始化 Sprint 的資訊l
	 */
	private void initSprintInformation() {
		try {
			if (mIterPlanDesc.getStartDate().equals("")) {
				throw new RuntimeException();
			}
			mStartDate = DateUtil.dayFilter(mIterPlanDesc.getStartDate());
			mEndDate = DateUtil.dayFilter(mIterPlanDesc.getEndDate());
			String aDays = mIterPlanDesc.getAvailableDays();
			// 將判斷 aDay:hours can commit 為 0 時, 計算 sprint 天數 * focus factor
			// 的機制移除
			// 改為只計算 aDay:hours can commit * focus factor
			if (aDays != null && !aDays.equals("")) {
				mLimitedPoint = Integer.parseInt(aDays)
						* Integer.parseInt(mIterPlanDesc.getFocusFactor())
						/ 100;
			}
		} catch (NumberFormatException e) {
			log.info("non-exist sprint");
		}
	}

	/**
	 * 初始 mMantisService 的設定
	 */
	private void initConfig() {
		mConfig = new Configuration(mUserSession);
		mMantisService = new MantisService(mConfig);
	}

	/**
	 * 測試用
	 */
	public void forceRefresh() {
		synchronizeDataInSprintInDB();
		mUpdateFlag = false;
	}

	/************************************************************
	 * =============== Sprint Backlog的操作 =========
	 *************************************************************/
	// StoryObject 時需要修改
	public Map<Long, ArrayList<TaskObject>> getTasksMap() {
		refresh();
		return mMapStoryTasks;
	}

	/**
	 * 如果沒有指定時間的話，預設就回傳目前最新的Task表給他
	 * 
	 * @return
	 */
	public ArrayList<TaskObject> getAllTasks() {
		refresh();
		return mTasks;
	}

	public Map<Long, ArrayList<TaskObject>> getDroppedTasksMap() {
		if (mMapDropedStoryTasks == null) {
			IIssue[] droppedStories = getDroppedStories();
			// 取得這些被 Dropped Story 的 Task
			mMapDropedStoryTasks = new LinkedHashMap<Long, ArrayList<TaskObject>>();
			for (IIssue dropedStory : droppedStories) {
				ArrayList<TaskObject> droppedTasks = new ArrayList<TaskObject>();
				List<Long> droppedTasksId = dropedStory.getChildrenId();
				for (Long droppedTaskId : droppedTasksId) {
					TaskObject droppedTask = TaskObject.get(droppedTaskId);
					if (droppedTask != null) {
						droppedTasks.add(droppedTask);
					}
				}
				mMapDropedStoryTasks
						.put(dropedStory.getIssueID(), droppedTasks);
			}
		}
		return mMapDropedStoryTasks;
	}

	// 上層如果 call 到這個 function, 要判斷傳入引數，如果是 Task 的話要改用 getAllTasks()
	public ArrayList<IIssue> getAllStories(String category) {
		refresh();
		return mStories;
	}

	/**
	 * 取得這個Sprint內stories
	 * 
	 * @param sprintId
	 * @return
	 */
	public IIssue[] getStoriesBySprintId(long sprintId) {
		mMantisService.openConnect();
		IIssue[] stories = mMantisService
				.getIssues(mIProject.getName(), ScrumEnum.STORY_ISSUE_TYPE,
						null, Long.toString(sprintId), null);
		mMantisService.closeConnect();
		return stories;
	}

	/**
	 * 取得 story 內的 tasks 與 sprint 無關 for ezScrum 1.8
	 * 
	 * @param storyId
	 */
	public ArrayList<TaskObject> getTasksByStoryId(long storyId) {
		IIssue story = getStory(storyId);
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		if (story != null) {
			List<Long> taskIds = story.getChildrenId();
			for (long taskId : taskIds) {
				TaskObject task = TaskObject.get(taskId);
				if (task != null) {
					tasks.add(task);
				}
			}
		}
		return tasks;
	}

	/**
	 * 取得在這個 Sprint 中被 Drop 掉的 Story
	 */
	public IIssue[] getDroppedStories() {
		if (mDropedStories == null) {
			mDropedStories = new ArrayList<IIssue>();
		} else {
			return mDropedStories.toArray(new IIssue[mDropedStories.size()]);
		}
		String sprintIdString = Integer.toString(mSprintPlanId);
		Date startDate = getSprintStartDate();
		Date endDate = getSprintEndDate();
		mMantisService.openConnect();
		// 找出這個 Sprint 期間，所有可能出現的 issue，下面再進行過濾
		IIssue[] sprintStories = mMantisService.getIssues(mIProject.getName(),
				ScrumEnum.STORY_ISSUE_TYPE, null, "*", startDate, endDate);
		// 確認這些這期間被 Drop 掉的 Story 是否曾經有在此 Sprint 過
		if (sprintStories != null) {
			for (IIssue story : sprintStories) {
				if (!story.getSprintID().equals(sprintIdString)) {
					mDropedStories.add(story);
				}
			}
		}
		mMantisService.closeConnect();
		return mDropedStories.toArray(new IIssue[mDropedStories.size()]);
	}

	public IIssue getStory(long storyId) {
		mMantisService.openConnect();
		IIssue story = mMantisService.getIssue(storyId);
		mMantisService.closeConnect();
		return story;
	}

	public TaskObject getTask(long taskId) {
		TaskObject task = TaskObject.get(taskId);
		if (task != null) {
			return task;
		}
		return null;
	}

	// for ezScrum 1.8
	// TaskInfo should include task id
	public void updateTask(TaskInfo taskInfo) {
		TaskObject task = TaskObject.get(taskInfo.taskId);
		if (task != null) {
			task.setName(taskInfo.name).setHandlerId(taskInfo.handlerId)
					.setEstimate(taskInfo.estimate)
					.setRemains(taskInfo.remains).setActual(taskInfo.actual)
					.setNotes(taskInfo.notes)
					.setPartnersId(taskInfo.partnersId).save();
		}
	}

	// for ezScrum 1.8
	public long addTask(long projectId, TaskInfo taskInfo) {
		TaskObject task = new TaskObject(projectId);
		task.setName(taskInfo.name).setNotes(taskInfo.notes)
				.setStoryId(taskInfo.storyId).setHandlerId(taskInfo.handlerId)
				.setEstimate(taskInfo.estimate).setRemains(taskInfo.estimate)
				.setActual(0).setCreateTime(taskInfo.specificTime).save();

		for (long partnerId : taskInfo.partnersId) {
			task.addPartner(partnerId);
		}

		mUpdateFlag = true;
		return task.getId();
	}

	// for ezScrum 1.8
	public void addExistingTasks(ArrayList<Long> taskIds, long storyId) {
		for (long taskId : taskIds) {
			TaskObject task = TaskObject.get(taskId);
			if (task != null) {
				task.setStoryId(storyId);
				task.save();
			}
		}
		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		mUpdateFlag = true;
	}

	public ArrayList<TaskObject> getTasksWithNoParent(long projectId) {
		ProjectObject project = ProjectObject.get(projectId);
		return project.getTasksWithNoParent();
	}

	// for ezScrum 1.8
	public void deleteExistingTask(long[] taskIds) {
		for (long taskId : taskIds) {
			TaskObject task = TaskObject.get(taskId);
			if (task != null) {
				task.delete();
			}
		}
		mUpdateFlag = true;
	}

	public void dropTask(long taskId) {
		TaskObject task = TaskObject.get(taskId);
		if (task != null) {
			task.setStoryId(TaskObject.NO_PARENT).save();
		}
		mUpdateFlag = true;
	}

	/************************************************************
	 * ===================== 取得Iteration的描述 ====================
	 *************************************************************/

	public int getSprintPlanId() {
		return mSprintPlanId;
	}

	public IProject getProject() {
		return mIProject;
	}

	public Date getSprintStartDate() {
		return mStartDate;
	}

	public Date getSprintEndDate() {
		return mEndDate;
	}

	public double getLimitedPoint() {
		return mLimitedPoint;
	}

	public String getSprintGoal() {
		return mIterPlanDesc.getGoal();
	}

	public void closeStory(long id, String notes, String changeDate) {
		Date closeDate = null;

		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate,
					DateUtil._16DIGIT_DATE_TIME);
		}

		mMantisService.openConnect();
		mMantisService.changeStatusToClosed(id, ITSEnum.FIXED_RESOLUTION,
				notes, closeDate);
		mMantisService.closeConnect();
	}

	public void reopenStory(long id, String name, String bugNote,
			String changeDate) {
		Date closeDate = null;

		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate,
					DateUtil._16DIGIT_DATE_TIME);
		} else {
			closeDate = new Date();
		}

		mMantisService.openConnect();
		mMantisService.resetStatusToNew(id, name, bugNote, closeDate);
		mMantisService.closeConnect();
	}

	/************************************************************
	 * ================== TaskBoard 中有關於 task 操作 ================
	 *************************************************************/
	/**
	 * From Not Checked Out to Checked Out
	 * 
	 * @param id
	 * @param name
	 * @param handlerId
	 * @param partners
	 * @param notes
	 * @param specificDate
	 */
	public void closeTask(long id, String name, String notes, int actual,
			Date specificDate) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.setName(name).setNotes(notes).setActual(actual)
					.setStatus(TaskObject.STATUS_DONE).setRemains(0)
					.setUpdateTime(specificDate.getTime())
					.save(specificDate.getTime());
		}
		mUpdateFlag = true;
	}
	
	/**
	 * From Checked Out to Done
	 * 
	 * @param id
	 * @param name
	 * @param notes
	 * @param specificDate
	 */
	public void closeTask(long id, String name, String notes, Date specificDate) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.setName(name).setNotes(notes)
					.setStatus(TaskObject.STATUS_DONE).setRemains(0)
					.setUpdateTime(specificDate.getTime())
					.save(specificDate.getTime());
		}
	}
	
	/**
	 * From Done to Checked Out
	 * 
	 * @param id
	 * @param name
	 * @param notes
	 * @param specificDate
	 */
	public void reopenTask(long id, String name, String notes, Date specificDate) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.setName(name).setNotes(notes)
					.setStatus(TaskObject.STATUS_CHECK)
					.save(specificDate.getTime());
		}
	}

	/**
	 * From Checked Out to Not Checked Out
	 * 
	 * @param id
	 * @param name
	 * @param notes
	 * @param specificDate
	 */
	public void resetTask(long id, String name, String notes, Date specificDate) {
		long noHandler = -1;
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.setName(name).setNotes(notes).setHandlerId(noHandler)
					.setStatus(TaskObject.STATUS_UNCHECK)
					.save(specificDate.getTime());
		}
	}

	// for ezScrum 1.8
	public void deleteTask(long id) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.delete();
		}
	}

	/************************************************************
	 * private methods
	 *************************************************************/

	/**
	 * Refresh 動作
	 */
	private void refresh() {
		if (mStories == null || mTasks == null || mMapStoryTasks == null
				|| mUpdateFlag) {
			synchronizeDataInSprintInDB();
			mUpdateFlag = false;
		}
	}

	/**
	 * 取得目前所有在此 Sprint 的 Story 與 Task
	 * 
	 * @return
	 */
	private void synchronizeDataInSprintInDB() {
		if (mStories == null || mTasks == null || mMapStoryTasks == null) {
			mStories = new ArrayList<IIssue>();
			mTasks = new ArrayList<TaskObject>();
			mMapStoryTasks = new LinkedHashMap<Long, ArrayList<TaskObject>>();
		} else {
			mStories.clear();
			mTasks.clear();
			mMapStoryTasks.clear();
		}
		IIssue[] stories = getStoriesBySprintId(mSprintPlanId);
		for (IIssue story : stories) {
			mStories.add(story);
			ArrayList<TaskObject> tasks = getTasksByStoryId(story.getIssueID());
			for (TaskObject task : tasks) {
				mTasks.add(task);
			}
			if (tasks.size() > 0) {
				mMapStoryTasks.put(story.getIssueID(), tasks);
			}
		}
		mUpdateFlag = false;
	}
}
