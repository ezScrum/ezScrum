package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class DoneFilter extends AProductBacklogFilter {

	public DoneFilter(ArrayList<StoryObject> issues) {
		super(issues);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = super.Stories;
		ArrayList<StoryObject> filererStories = new ArrayList<StoryObject>();
		for (StoryObject story : stories) {
			// status is closed
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				filererStories.add(story);
			}
		}
		return filererStories;
	}

	@Override
	protected ITask[] FilterTasks() {
		// empty function
		return null;
	}
}
