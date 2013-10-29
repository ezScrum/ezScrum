package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class IssueDescFilter extends AProductBacklogFilter {
	public IssueDescFilter(IStory[] stories, String info) {
		super(stories, info);
	}
	
	public IssueDescFilter(ITask[] tasks, String info) {
		super(tasks, info);
	}

	@Override
	protected IStory[] FilterStories() {
		IStory[] Stories = super.Stories;
		
		List<IStory> filerStories = new ArrayList<IStory>();
		
		for (IStory story : Stories) {
			// story description contains info
			String desc = story.getDescription();
			if ( compareDesc(desc) ) {
				filerStories.add(story);
			}
		}
			
		return filerStories.toArray(new IStory[filerStories.size()]);
	}

	@Override
	protected ITask[] FilterTasks() {
		ITask[] Tasks = super.Tasks;
		
		List<ITask> filerStories = new ArrayList<ITask>();
		
		for (ITask task : Tasks) {
			// task description contains info
			String desc = task.getDescription();
			if ( compareDesc(desc) ) {
				filerStories.add(task);
			}
		}
			
		return filerStories.toArray(new ITask[filerStories.size()]);
	}
	
	private boolean compareDesc(String desc) {
		if ( (desc != null) && (desc.length() > 0) ) { 
			if (desc.contains(this.compareInfo)) {
				return true;
			}
		}
		
		return false;
	}
}
