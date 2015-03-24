package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class IssueDescFilter extends AProductBacklogFilter {
	public <E> IssueDescFilter(ArrayList<E> issues, String info) {
		super(issues, info);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = super.mStories;
		ArrayList<StoryObject> fileredStories = new ArrayList<StoryObject>();
		for (StoryObject story : stories) {
			// story description contains info
			String name = story.getName(); // story has no description
			if (compareDesc(name)) {
				fileredStories.add(story);
			}
		}
		return stories;
	}

	@Override
	protected ArrayList<TaskObject> FilterTasks() {
		ArrayList<TaskObject> Tasks = super.mTasks;
		ArrayList<TaskObject> filerStories = new ArrayList<TaskObject>();
		for (TaskObject task : Tasks) {
			// task description contains info
			String name = task.getName();
			if ( compareDesc(name) ) {
				filerStories.add(task);
			}
		}
		return filerStories;
	}
	
	private boolean compareDesc(String desc) {
		if ( (desc != null) && (desc.length() > 0) ) { 
			if (desc.contains(this.mCompareInfo)) {
				return true;
			}
		}
		return false;
	}
}
