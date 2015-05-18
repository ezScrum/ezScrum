package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintPlanLogic;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class SprintBacklogMapper {
	private long mSprintId = 0;
	private ProjectObject mProject;
	private ISprintPlanDesc mIterPlanDesc;
	private Date mStartDate;
	private Date mEndDate;
	private double mLimitedPoint = 0;

	private ArrayList<StoryObject> mStories = null;
	private ArrayList<TaskObject> mTasks = null;

	// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	private boolean mUpdateFlag = true;

	/**
	 * 若沒有指定的話,自動取得目前的 sprint#
	 * 
	 * @param project
	 */
	public SprintBacklogMapper(ProjectObject project) {
		mProject = project;
		SprintPlanLogic sprintPlanLogic = new SprintPlanLogic(project);
		mIterPlanDesc = sprintPlanLogic.loadCurrentPlan();
		if (mIterPlanDesc != null) {
			mSprintId = Integer.parseInt(mIterPlanDesc.getID());
		}
		initSprintInformation();
	}

	/**
	 * 取得指定的 sprint backlog
	 * 
	 * @param project
	 * @param sprintId
	 */
	public SprintBacklogMapper(ProjectObject project, long sprintId) {
		mProject = project;
		SprintPlanMapper mapper = new SprintPlanMapper(project);
		mIterPlanDesc = mapper.getSprintPlan(Long.toString(sprintId));
		mSprintId = Integer.parseInt(mIterPlanDesc.getID());
		if (mSprintId == -1) {
			throw new RuntimeException("Sprint#-1 is not existed.");
		}
		initSprintInformation();
	}

	/**
	 * 初始化 Sprint 的資訊
	 */
	private void initSprintInformation() {
		mStartDate = DateUtil.dayFilter(mIterPlanDesc.getStartDate());
		mEndDate = DateUtil.dayFilter(mIterPlanDesc.getEndDate());
		String aDays = mIterPlanDesc.getAvailableDays();
		// 將判斷 aDay:hours can commit 為 0 時, 計算 sprint 天數 * focus factor
		// 的機制移除改為只計算 aDay:hours can commit * focus factor
		if (aDays != null && !aDays.equals("")) {
			mLimitedPoint = Integer.parseInt(aDays)
					* Integer.parseInt(mIterPlanDesc.getFocusFactor()) / 100;
		}
	}

	/**
	 * 測試用
	 */
	public void forceRefresh() {
		synchronizeDataInSprintInDB();
		mUpdateFlag = false;
	}

	/*************************************************************
	 * ===================== Sprint Backlog 的操作 =================
	 *************************************************************/

	public StoryObject getStory(long storyId) {
		StoryObject story = StoryObject.get(storyId);
		return story;
	}

	public ArrayList<StoryObject> getAllStories() {
		mUpdateFlag = true;
		refresh();
		return mStories;
	}
	
	/**
	 * 取得這個 Sprint 內 stories
	 * 
	 * @param sprintId
	 * @return StoryObject list
	 */
	public ArrayList<StoryObject> getStoriesBySprintId(long sprintId) {
		return StoryObject.getStoriesBySprintId(sprintId);
	}

	/**
	 * 取得被 Drop 掉的 Story
	 */
	public ArrayList<StoryObject> getDroppedStories() {
		return mProject.getStoriesWithNoParent();
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
	
	public TaskObject getTask(long taskId) {
		TaskObject task = TaskObject.get(taskId);
		if (task != null) {
			return task;
		}
		return null;
	}
	
	/**
	 * 如果沒有指定時間的話，預設就回傳目前最新的 Task 表給他
	 * 
	 * @return
	 */
	public ArrayList<TaskObject> getAllTasks() {
		mUpdateFlag = true;
		refresh();
		return mTasks;
	}
	
	/**
	 * 取得 story 內的 tasks
	 * for ezScrum 1.8
	 * 
	 * @param storyId
	 * @return Tasks of Story list
	 */
	public ArrayList<TaskObject> getTasksByStoryId(long storyId) {
		StoryObject story = StoryObject.get(storyId);
		if (story != null) {
			return story.getTasks();
		}
		return new ArrayList<TaskObject>();		
	}
	
	// for ezScrum 1.8
	// TaskInfo should include task id
	public void updateTask(long taskId, TaskInfo taskInfo) {
		TaskObject task = TaskObject.get(taskId);
		if (task != null) {
			task.setName(taskInfo.name).setHandlerId(taskInfo.handlerId)
			.setEstimate(taskInfo.estimate)
			.setRemains(taskInfo.remains).setActual(taskInfo.actual)
			.setNotes(taskInfo.notes)
			.setPartnersId(taskInfo.partnersId).save();
		}
	}
	
	// for ezScrum 1.8
	public void deleteTask(long id) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.delete();
		}
	}

	// for ezScrum 1.8
	public void addExistingTasksToStory(ArrayList<Long> taskIds, long storyId) {
		for (long taskId : taskIds) {
			TaskObject task = TaskObject.get(taskId);
			if (task != null) {
				task.setStoryId(storyId);
				task.save();
			} else {
				throw new RuntimeException("Task#" + taskId + " is not existed.");
			}
		}
		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		mUpdateFlag = true;
	}

	public ArrayList<TaskObject> getTasksWithNoParent(long projectId) {
		return ProjectObject.get(projectId).getTasksWithNoParent();
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

	public long getSprintId() {
		return mSprintId;
	}

	public IProject getProject() {
		IProject iProject = new ProjectMapper().getProjectByID(mProject.getName());
		return iProject;
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

	/*************************************************************
	 * ================ TaskBoard 中有關於 story 操作 =================
	 *************************************************************/

	public void closeStory(long id, String name, String notes,
			Date specificDate) {
		StoryObject story = StoryObject.get(id);
		if (story != null) {
			story.setName(name).setNotes(notes)
					.setStatus(StoryObject.STATUS_DONE)
					.setUpdateTime(specificDate.getTime())
					.save(specificDate.getTime());
		}
		mUpdateFlag = true;
	}

	public void reopenStory(long id, String name, String notes,
			Date specificDate) {
		StoryObject story = StoryObject.get(id);
		if (story != null) {
			story.setName(name).setNotes(notes)
					.setStatus(StoryObject.STATUS_UNCHECK)
					.setUpdateTime(specificDate.getTime())
					.save(specificDate.getTime());
		}
		mUpdateFlag = true;
	}

	/*************************************************************
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
					.setPartnersId(new ArrayList<Long>())
					.setStatus(TaskObject.STATUS_UNCHECK)
					.save(specificDate.getTime());
		}
	}

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
	public void checkOutTask(long id, String name, long handlerId,
			ArrayList<Long> partners, String notes, Date specificDate) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.setName(name).setHandlerId(handlerId).setPartnersId(partners)
					.setNotes(notes).setStatus(TaskObject.STATUS_CHECK)
					.save(specificDate.getTime());
		}
	}

	/************************************************************
	 * private methods
	 *************************************************************/

	/**
	 * Refresh 動作
	 */
	private void refresh() {
		if (mStories == null || mTasks == null || mUpdateFlag) {
			synchronizeDataInSprintInDB();
			mUpdateFlag = false;
		}
	}

	/**
	 * 取得目前所有在此 Sprint 的 Story 與 Task
	 */
	private void synchronizeDataInSprintInDB() {
		if (mStories == null || mTasks == null) {
			mStories = new ArrayList<StoryObject>();
			mTasks = new ArrayList<TaskObject>();
		}
		
		mStories = getStoriesBySprintId(mSprintId);
		mTasks.clear();
		for (StoryObject story : mStories) {
			mTasks.addAll((ArrayList<TaskObject>)story.getTasks());
		}
		mUpdateFlag = false;
	}
	
	public Date parseToDate(String dateString) {
		Date closeDate = new Date();
		if (dateString != null && !dateString.equals("")) {
			closeDate = DateUtil.dayFillter(dateString,
					DateUtil._16DIGIT_DATE_TIME);
		}
		return closeDate;
	}
}
