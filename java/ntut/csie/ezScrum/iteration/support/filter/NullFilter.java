package ntut.csie.ezScrum.iteration.support.filter;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class NullFilter extends AProductBacklogFilter {

	public NullFilter(IStory[] stories) {
		super(stories);
	}
	
	public NullFilter(ITask[] tasks) {
		super(tasks);
	}

	@Override
	protected IStory[] FilterStories() {
		return super.Stories;
	}

	@Override
	protected ITask[] FilterTasks() {
		return super.Tasks;
	}
}
