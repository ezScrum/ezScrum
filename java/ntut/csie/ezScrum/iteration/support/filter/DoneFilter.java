package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class DoneFilter extends AProductBacklogFilter {

	public DoneFilter(IStory[] issues) {
		super(issues);
	}

	@Override
	protected IStory[] FilterStories() {
		IStory[] Stories = super.Stories;
		
		List<IStory> filerStories = new ArrayList<IStory>();
		
		for (IStory story : Stories) {
			// status is closed
			if ( (story.getStatus() != null) && (story.getStatus().equals(ITSEnum.S_CLOSED_STATUS)) ) {
				filerStories.add(story);
			}
		}
			
		return filerStories.toArray(new IStory[filerStories.size()]);
	}

	@Override
	protected ITask[] FilterTasks() {
		// empty function
		return null;
	}
}
