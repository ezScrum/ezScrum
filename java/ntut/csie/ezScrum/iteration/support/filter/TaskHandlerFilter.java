package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class TaskHandlerFilter extends AProductBacklogFilter {
	public TaskHandlerFilter(ITask[] tasks, String info) {
		super(tasks, info);
	}

	@Override
	protected IStory[] FilterStories() {
		return null;
	}

	@Override
	protected ITask[] FilterTasks() {
		ITask[] Tasks = super.mTasks;
		
		List<ITask> filerStories = new ArrayList<ITask>();
		
		if (this.mCompareInfo.equals("ALL")) {
			filerStories.addAll(Arrays.asList(Tasks));
		} else {
			for (ITask task : Tasks) {
				// task handler contains info
				if ( (task.getAssignto() != null) && (task.getAssignto().equals(this.mCompareInfo)) ) {
					filerStories.add(task);
				}
			}
		}
			
		return filerStories.toArray(new ITask[filerStories.size()]);
	}
}
