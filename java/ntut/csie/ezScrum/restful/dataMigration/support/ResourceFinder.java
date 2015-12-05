package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;

public class ResourceFinder {
	private ProjectObject mProject;
	private SprintObject mSprint;
	private StoryObject mStory;
	private TaskObject mTask;

	public ProjectObject findProject(long projectId) {
		mProject = ProjectObject.get(projectId);
		return mProject;
	}

	public SprintObject findSprint(long sprintId) {
		if (mProject == null) {
			return null;
		} else {
			SprintObject sprint = SprintObject.get(sprintId);
			if (mProject.getId() != sprint.getProjectId()) {
				return null;
			}
			mSprint = sprint;
		}
		return mSprint;
	}

	public StoryObject findStory(long storyId) {
		if (mProject == null || mSprint == null) {
			return null;
		} else {
			StoryObject story = StoryObject.get(storyId);
			if (story.getProjectId() != mProject.getId() ||
			    story.getSprintId() != mSprint.getId()) {
				return null;
			}
			mStory = story;
		}
		return mStory;
	}
	
	public TaskObject findTask(long taskId) {
		if (mProject == null || mSprint == null || mStory == null) {
			return null;
		} else {
			TaskObject task = TaskObject.get(taskId);
			StoryObject story = StoryObject.get(task.getStoryId());
			if (task.getProjectId() != mProject.getId() ||
			    task.getStoryId() != mStory.getId() || 
			    story.getSprintId() != mSprint.getId()) {
				return null;
			}
			mTask = task;
		}
		return mTask;
	}
	
	public TaskObject findTaskInDroppedStory(long taskId) {
		if (mProject == null || mStory == null) {
			return null;
		} else {
			TaskObject task = TaskObject.get(taskId);
			if (task.getProjectId() != mProject.getId() ||
			    task.getStoryId() != mStory.getId()) {
				return null;
			}
			mTask = task;
		}
		return mTask;
	}
	
	public UnplanObject findUnplan(long unplanId) {
		UnplanObject unplan;
		if (mProject == null || mSprint == null) {
			return null;
		} else {
			unplan = UnplanObject.get(unplanId);
			SprintObject sprint = SprintObject.get(unplan.getSprintId());
			if (sprint.getProjectId() != mProject.getId() ||
			        unplan.getProjectId() != mProject.getId() ||
			        unplan.getSprintId() != mSprint.getId()) {
				return null;
			}
		}
		return unplan;
	}
	
	public StoryObject findDroppedStory(long storyId) {
		if (mProject == null) {
			return null;
		} else {
			StoryObject story = StoryObject.get(storyId);
			if (story.getProjectId() != mProject.getId()) {
				return null;
			}
			mStory = story;
		}
		return mStory;
	}
	
	public TaskObject findDroppedTask(long taskId) {
		if (mProject == null) {
			return null;
		} else {
			TaskObject task = TaskObject.get(taskId);
			if (task.getProjectId() != mProject.getId()) {
				return null;
			}
			mTask = task;
		}
		return mTask;
	}
}
