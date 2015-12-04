package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class ResourceFinder {
	private ProjectObject mProject;
	private SprintObject mSprint;
	private StoryObject mStory;

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
}
