package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.StoryObject;


public class ProductBacklogFilterFactory {
	private static ProductBacklogFilterFactory factory = null;
	
	private ProductBacklogFilterFactory() {
		// empty
	}
	
	public static ProductBacklogFilterFactory getInstance() {
		if (factory == null) {
			return new ProductBacklogFilterFactory();
		}
		
		return factory;		
	}
	
	public AProductBacklogFilter getPBFilterFilter(String type, ArrayList<StoryObject> stories) {
		if (type == null) {
			return new NullFilter(stories);
		}
		if (type.equals(ScrumEnum.BACKLOG)) {
			return new BacklogedFilter(stories);
		} else if (type.equals(ScrumEnum.DETAIL)) {
			return new DetailedFilter(stories);
		} else if (type.equals(ScrumEnum.DONE)) {
			return new DoneFilter(stories);
		} else {
			return new NullFilter(stories);
		}
	}
	
	public AProductBacklogFilter getStoryFilter_byInfo(String type, ArrayList<StoryObject> stories, String info) {
		if (type == null) {
			return new NullFilter(stories);
		}
		if (type.equals(ScrumEnum.FILTER_NANE)) {
			return new IssueNameFilter(stories, info);
		} else if (type.equals(ScrumEnum.FILTER_DESCRIPTION)) {
			return new IssueDescFilter(stories, info);
		} else {
			return new NullFilter(stories);
		}
	}
	
	public AProductBacklogFilter getTaskFilter_byInfo(String type, ITask[] tasks, String info) {
		if (type == null) {
			return new NullFilter(tasks);
		}
		
		if (type.equals(ScrumEnum.FILTER_NANE)) {
			return new IssueNameFilter(tasks, info);
		} else if (type.equals(ScrumEnum.FILTER_DESCRIPTION)) {
			return new IssueDescFilter(tasks, info);
		} else if (type.equals(ScrumEnum.FILTER_HANDLER)) {
			return new TaskHandlerFilter(tasks, info);
		} else {
			return new NullFilter(tasks);
		}
	}
}
