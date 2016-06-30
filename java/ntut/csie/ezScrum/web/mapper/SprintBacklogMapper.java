package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintBacklogMapper {
	private ProjectObject mProject;
	private SprintObject mSprint;

	/**
	 * 若沒有指定 sprint id 的話,自動取得最近的 sprint
	 * 
	 * @param project
	 */
	public SprintBacklogMapper(ProjectObject project) {
		mProject = project;
		mSprint = mProject.getCurrentSprint();
	}

	/**
	 * 取得指定的 sprint backlog
	 * 
	 * @param project
	 * @param sprintId
	 */
	public SprintBacklogMapper(ProjectObject project, long sprintId) {
		mProject = project;
		mSprint = SprintObject.get(sprintId);
		if (mSprint == null) {
			throw new RuntimeException("Sprint#" + sprintId + " is not existed.");
		}
	}

	/*************************************************************
	 * ===================== Sprint Backlog 的操作 =================
	 *************************************************************/
	
	public SprintObject getSprint() {
		return mSprint;
	}

	public StoryObject getStory(long storyId) {
		StoryObject story = StoryObject.get(storyId);
		return story;
	}

	/**
	 * 取得這個 Sprint 內 stories
	 * 
	 * @param sprintId
	 * @return ArrayList<StoryObject>
	 */
	public ArrayList<StoryObject> getStoriesInSprint() {
		if (mSprint != null) {
			return mSprint.getStories();
		}
		return new ArrayList<StoryObject>();
	}

	/**
	 * 取得被 Drop 掉的 Story
	 */
	public ArrayList<StoryObject> getDroppedStories() {
		return mProject.getDroppedStories();
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
		return task.getId();
	}
	
	public TaskObject getTask(long taskId) {
		return TaskObject.get(taskId);
	}
	
	public TaskObject getTask(long projectId, long serialTaskId) {
		return TaskObject.get(projectId, serialTaskId);
	}
	
	/**
	 * 如果沒有指定時間的話，預設就回傳目前最新的 Task 表給他
	 * 
	 * @return
	 */
	public ArrayList<TaskObject> getTasksInSprint() {
		ArrayList<TaskObject> tasks = new ArrayList<>();
		if (mSprint == null) {
			return tasks;
		}
		ArrayList<StoryObject> stories = mSprint.getStories();
		for (StoryObject story : stories) {
			tasks.addAll((ArrayList<TaskObject>)story.getTasks());
		}
		return tasks;
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
		ArrayList<TaskObject> tasks = new ArrayList<>();
		if (story != null) {
			tasks = story.getTasks();
		}
		return tasks;		
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
	public void addExistingTasksToStory(ArrayList<Long> serialTaskIds, long storyId) {
		for (long taskId : serialTaskIds) {
			TaskObject task = TaskObject.get(mProject.getId(), taskId);
			if (task != null) {
				task.setStoryId(storyId);
				task.save();
			} else {
				throw new RuntimeException("Task#" + taskId + " is not existed.");
			}
		}
	}

	public ArrayList<TaskObject> getDroppedTasks(long projectId) {
		return ProjectObject.get(projectId).getDroppedTasks();
	}

	// for ezScrum 1.8
	public void deleteExistingTasks(long[] taskIds) {
		for (long taskId : taskIds) {
			TaskObject task = TaskObject.get(taskId);
			if (task != null) {
				task.delete();
			}
		}
	}

	public void dropTask(long taskId) {
		TaskObject task = TaskObject.get(taskId);
		if (task != null) {
			task.setStoryId(TaskObject.NO_PARENT).save();
		}
	}

	public long getSprintId() {
		if (mSprint == null) {
			return -1;
		}
		return mSprint.getId();
	}

	public ProjectObject getProject() {
		return mProject;
	}

	public Date getSprintStartDate() {
		if (mSprint == null) {
			return null;
		}
		return DateUtil.dayFilter(mSprint.getStartDateString());
	}

	public Date getSprintDemoDate() {
		if (mSprint == null) {
			return null;
		}
		return DateUtil.dayFilter(mSprint.getDemoDateString());
	}
	
	public Date getSprintEndDate() {
		if (mSprint == null) {
			return null;
		}
		return DateUtil.dayFilter(mSprint.getEndDateString());
	}

	public String getSprintGoal() {
		if (mSprint == null) {
			return "";
		}
		return mSprint.getGoal();
	}
	
	public void updateStoryRelation(long serialStoryId, long sprintId, int estimate, int importance, Date date) {
		StoryObject story = StoryObject.get(mProject.getId(), serialStoryId);
		story.setSprintId(sprintId)
		     .setEstimate(estimate)
		     .setImportance(importance)
		     .save(date.getTime());
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
	 * @param partnersId
	 * @param notes
	 * @param specificDate
	 */
	public void checkOutTask(long id, String name, long handlerId,
			ArrayList<Long> partnersId, String notes, Date specificDate) {
		TaskObject task = TaskObject.get(id);
		if (task != null) {
			task.setName(name).setHandlerId(handlerId).setPartnersId(partnersId)
					.setNotes(notes).setStatus(TaskObject.STATUS_CHECK)
					.save(specificDate.getTime());
		}
	}
	
	/**
	 * From Checked Out to Done
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
					.setPartnersId(task.getPartnersId())
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
					.setPartnersId(new ArrayList<Long>())
					.setStatus(TaskObject.STATUS_UNCHECK)
					.save(specificDate.getTime());
		}
	}
}
