package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class IssueNameFilter extends AProductBacklogFilter {
	public IssueNameFilter(IStory[] stories, String info) {
		super(stories, info);
	}
	
	public IssueNameFilter(ITask[] tasks, String info) {
		super(tasks, info);
	}

	@Override
	protected IStory[] FilterStories() {
		IStory[] Stories = super.Stories;
		
		List<IStory> filerStories = new ArrayList<IStory>();
		
		for (IStory story : Stories) {
			// story name contains info
			String name = story.getName();
			if ( compareName(name) ) {
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
			// task name contains info
			String name = task.getName();
			if ( compareName(name) ) {
				filerStories.add(task);
			}
		}
			
		return filerStories.toArray(new ITask[filerStories.size()]);
	}
	
	private boolean compareName(String name) {
		if ( (name != null) && (name.length() > 0) ) { 
			if (name.contains(this.compareInfo)) {
				return true;
			}
		}
		
		return false;
	}
}
