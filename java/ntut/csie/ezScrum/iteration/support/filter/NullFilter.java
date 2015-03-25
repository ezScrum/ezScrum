package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class NullFilter extends AProductBacklogFilter {

	public <E> NullFilter(ArrayList<E> issues) {
		super(issues);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		return super.mStories;
	}

	@Override
	protected ArrayList<TaskObject> FilterTasks() {
		return super.mTasks;
	}
}
