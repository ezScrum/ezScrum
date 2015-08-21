package ntut.csie.ezScrum.iteration.iternal;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.jcis.core.util.DateUtil;

public class ReleaseBacklog {
	private ProjectObject mProject;
	private ReleaseObject mRelease;
	private ArrayList<StoryObject> mStories;

	final private long OneDay = ScrumEnum.DAY_MILLISECOND;
	
	public ReleaseBacklog(ProjectObject project, ReleaseObject release, ArrayList<StoryObject> stories) {
		mProject = project;
		mRelease = release;
		//Release Plan下所有的Story
		mStories = stories;
	}
	
	public ProjectObject getProject(){
		return mProject;
	}
	
	public long getReleaseId(){
		return mRelease.getId();
	}
	
	public String getReleaseName(){
		return mRelease.getName();
	}
	
	public Date getStartDate() {
		return DateUtil.dayFilter(mRelease.getStartDateString());
	}
	
	public Date getDueDate() {
		return DateUtil.dayFilter(mRelease.getDueDateString());
	}
	
	public ArrayList<StoryObject> getStories(){
		return mStories;
	}
		
	public int getStoryCount() {
		if (mStories != null) {
			return mStories.size();
		}
		return 0;
	}
	
	public int getSprintCounts() {
		return mRelease.getSprints().size();
	}
	
	// 取得該日期前已完成的Story數量
	public double getDoneStoryByDate(Date date) {
		double count = 0;
		for (StoryObject story : mStories) {
			// Story Close的時間
			
			int status = story.getStatus(date);
			if (status == StoryObject.STATUS_DONE) {
				count++;
			}
		}
		return count;
	}
	
	// 所有story done後，不論何時把story拉到done都把real point設成0
	public double getReleaseAllStoryDone() {
		double count = mStories.size();
		for (StoryObject story : mStories) {
			// story狀態為done的 count就-1
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				count--;
			}
		}
		return count;
	}
}