package ntut.csie.ezScrum.iteration.support.filter;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public abstract class AProductBacklogFilter {
	protected IStory[] Stories = null;
	protected ITask[] Tasks = null;
	protected String compareInfo = null;
	
	protected abstract IStory[] FilterStories();		// 過濾 Stories 的方法
	protected abstract ITask[] FilterTasks();			// 過濾 Tasks 的方法
	
	public IStory[] getStories() { return this.Stories; }
	public ITask[] getTasks() { return this.Tasks; }
	
	public AProductBacklogFilter(IStory[] stories) {
		this.Stories = stories;
		
		// 根據不同條件過濾 Stories
		this.Stories = FilterStories();
	}
	
	public AProductBacklogFilter(IStory[] stories, String compareinfo) {
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