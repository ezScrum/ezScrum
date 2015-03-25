package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class DetailedFilter extends AProductBacklogFilter {

	public DetailedFilter(ArrayList<StoryObject> issues) {
		super(issues);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = super.Stories;
		ArrayList<StoryObject> fileredStories = new ArrayList<StoryObject>();
		for (StoryObject story : stories) {
			// business value 存在  & estimate 存在 & importance 存在
			if (story.getValue() > 0 && story.getEstimate() > 0 && story.getImportance() > 0) {
				// status 為 new
				if (story.getStatus() == StoryObject.STATUS_UNCHECK) {
					fileredStories.add(story);
				}
			}
		}
		return fileredStories;
	}

	@Override
	protected ITask[] FilterTasks() {
		// empty function
		return null;
	}
}
