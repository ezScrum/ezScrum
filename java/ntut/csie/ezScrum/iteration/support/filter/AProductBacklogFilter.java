package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public abstract class AProductBacklogFilter {
	protected ArrayList<StoryObject> mStories = new ArrayList<StoryObject>();
	protected ArrayList<TaskObject> mTasks = new ArrayList<TaskObject>();
	protected String mCompareInfo = null;
	
	protected abstract ArrayList<StoryObject> FilterStories();		// 過濾 Stories 的方法
	protected abstract ArrayList<TaskObject> FilterTasks();			// 過濾 Tasks 的方法
	
	public ArrayList<StoryObject> getStories() { return mStories; }
	public ArrayList<TaskObject> getTasks() { return mTasks; }
	
	public <E> AProductBacklogFilter(ArrayList<E> issues){
		for(Object object : issues){
			if (object instanceof StoryObject) {
				StoryObject story = (StoryObject) object;
				mStories.add(story);
			} else if(object instanceof TaskObject){
				TaskObject task = (TaskObject) object;
				mTasks.add(task);
			}
		}
	}
	
	public <E> AProductBacklogFilter(ArrayList<E> issues, String compareinfo) {
		mCompareInfo = compareinfo;
		
		for(Object object : issues){
			if (object instanceof StoryObject) {
				StoryObject story = (StoryObject) object;
				mStories.add(story);
			} else if(object instanceof TaskObject){
				TaskObject task = (TaskObject) object;
				mTasks.add(task);
			}
		}
		
		if(issues.get(0) instanceof StoryObject){
			mStories = FilterStories();
		} else if(issues.get(0) instanceof TaskObject){
			mTasks = FilterTasks();
		}
	}
}