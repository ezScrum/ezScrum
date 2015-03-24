package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class IssueNameFilter extends AProductBacklogFilter {
	public <E> IssueNameFilter(ArrayList<E> issues, String info) {
		super(issues, info);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = super.getStories();
		ArrayList<StoryObject> fileredStories = new ArrayList<StoryObject>();
		for (StoryObject story : stories) {
			// story name contains info
			String name = story.getName();
			if ( compareName(name) ) {
				fileredStories.add(story);
			}
		}
		return fileredStories;
	}

	@Override
	protected ArrayList<TaskObject> FilterTasks() {
		ArrayList<TaskObject> Tasks = super.getTasks();

		ArrayList<TaskObject> filerStories = new ArrayList<TaskObject>();

		for (TaskObject task : Tasks) {
			// task name contains info
			String name = task.getName();
			if (compareName(name)) {
				filerStories.add(task);
			}
		}

		return filerStories;
	}

	private boolean compareName(String name) {
		if ((name != null) && (name.length() > 0)) {
			if (name.contains(this.mCompareInfo)) {
				return true;
			}
		}
		return false;
	}
}
