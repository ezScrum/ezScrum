package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class NullFilter extends AProductBacklogFilter {

	public NullFilter(ArrayList<StoryObject> stories) {
		super(stories);
	}
	
	public NullFilter(ITask[] tasks) {
		super(tasks);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		return super.Stories;
	}

	@Override
	protected ITask[] FilterTasks() {
		return super.Tasks;
	}
}
