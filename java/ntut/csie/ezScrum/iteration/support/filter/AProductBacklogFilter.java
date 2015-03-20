package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public abstract class AProductBacklogFilter {
	protected ArrayList<StoryObject> Stories = null;
	protected ITask[] Tasks = null;
	protected String compareInfo = null;
	
	protected abstract ArrayList<StoryObject> FilterStories();		// 過濾 Stories 的方法
	protected abstract ITask[] FilterTasks();			// 過濾 Tasks 的方法
	
	public ArrayList<StoryObject> getStories() { return this.Stories; }
	public ITask[] getTasks() { return this.Tasks; }
	
	public AProductBacklogFilter(ArrayList<StoryObject> stories) {
		this.Stories = stories;
		
		// 根據不同條件過濾 Stories
		this.Stories = FilterStories();
	}
	
	public AProductBacklogFilter(ArrayList<StoryObject> stories, String compareinfo) {
		this.Stories = stories;
		this.compareInfo = compareinfo;
		
		// 根據不同條件過濾 Stories
		this.Stories = FilterStories();
	}
	
	public AProductBacklogFilter(ITask[] tasks) {
		this.Tasks = tasks;
		
		// 根據不同條件過濾 Task
		this.Tasks = FilterTasks();
	}
	
	public AProductBacklogFilter(ITask[] tasks, String compareinfo) {
		this.Tasks = tasks;
		this.compareInfo = compareinfo;
		
		// 根據不同條件過濾 Task
		this.Tasks = FilterTasks();
	}
}