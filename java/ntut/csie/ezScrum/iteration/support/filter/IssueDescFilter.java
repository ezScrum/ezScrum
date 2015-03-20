package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class IssueDescFilter extends AProductBacklogFilter {
	public IssueDescFilter(ArrayList<StoryObject> stories, String info) {
		super(stories, info);
	}
	
	public IssueDescFilter(ITask[] tasks, String info) {
		super(tasks, info);
	}

	@Override
	protected ArrayList<StoryObject> FilterStories() {
		ArrayList<StoryObject> stories = super.Stories;
//		ArrayList<StoryObject> fileredStories = new ArrayList<StoryObject>();
//		for (StoryObject story : stories) {
//			// story description contains info
//			String desc = story.getDescription(); // story has no description
//			if (compareDesc(desc)) {
//				fileredStories.add(story);
//			}
//		}
		return stories;
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
