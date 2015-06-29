package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class BacklogedFilter extends AProductBacklogFilter {

	public BacklogedFilter(ArrayList<StoryObject> stories) {
		super(stories);
		mStories = FilterStories();
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = mStories;
		ArrayList<StoryObject> fileredStories = new ArrayList<StoryObject>();
		for (StoryObject story : stories) {
			// business value 不存在, 或 estimate 不存在, 或 importance 不存在
			if (story.getValue() == 0 || story.getEstimate() == 0 || story.getImportance() == 0) {
				// status 為 new
				if (story.getStatus() == StoryObject.STATUS_UNCHECK) {
					fileredStories.add(story);
				}
			}
		}
		return fileredStories;
	}

	@Override
	protected ArrayList<TaskObject> FilterTasks() {
		// empty function
		return null;
	}
}
