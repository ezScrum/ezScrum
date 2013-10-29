package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class DetailedFilter extends AProductBacklogFilter {

	public DetailedFilter(IStory[] issues) {
		super(issues);
	}

	@Override
	protected IStory[] FilterStories() {
		IStory[] Stories = super.Stories;
		
		List<IStory> filerStories = new ArrayList<IStory>();
		
		for (IStory story : Stories) {
			// business value 存在  & estimate 存在 & importance 存在
			if ( ( (story.getValue()!=null) && (! story.getValue().equals("0")) ) && 
				 ( (story.getEstimated()!=null) && (! story.getEstimated().equals("0")) ) &&
				 ( (story.getImportance()!=null) && (! story.getImportance().equals("0")) ) ) {
				
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
