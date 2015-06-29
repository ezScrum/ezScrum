package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class DoneFilter extends AProductBacklogFilter {

	public DoneFilter(ArrayList<StoryObject> issues) {
		super(issues);
		mStories = FilterStories();
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = mStories;
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
	protected ArrayList<TaskObject> FilterTasks() {
		// empty function
		return null;
	}
}
