package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class BacklogedFilter extends AProductBacklogFilter {

	public BacklogedFilter(IStory[] stories) {
		super(stories);
	}

	@Override
	protected IStory[] FilterStories() {
		IStory[] Stories = super.Stories;
		
		List<IStory> filerStories = new ArrayList<IStory>();
		
		for (IStory story : Stories) {
			// business value 不存在, 或 estimate 不存在, 或 importance 不存在
			if ( ! (( (story.getValue()!=null) && (! story.getValue().equals("0")) ) && 
					( (story.getEstimated()!=null) && (! story.getEstimated().equals("0")) ) &&
					( (story.getImportance()!=null) && (! story.getImportance().equals("0")) ) ) ) {
				
				// status 為 new
				if (story.getStatus().equals(ITSEnum.S_NEW_STATUS)) {
					filerStories.add(story);
				}
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
