package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskHandlerFilter extends AProductBacklogFilter {
	public TaskHandlerFilter(ArrayList<TaskObject> tasks, String info) {
		super(tasks, info);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		return mStories;
	}

	@Override
	protected ArrayList<TaskObject> FilterTasks() {
		ArrayList<TaskObject> Tasks = mTasks;

		ArrayList<TaskObject> filteredTasks = new ArrayList<TaskObject>();

		if (mCompareInfo.equals("ALL")) {
			filteredTasks.addAll(Tasks);
		} else {
			for (TaskObject task : Tasks) {
				// task handler contains info
				if ((task.getHandler() != null) && (task.getHandler().getUsername().equals(mCompareInfo))) {
					filteredTasks.add(task);
				}
			}
		}

		return filteredTasks;
	}
}
