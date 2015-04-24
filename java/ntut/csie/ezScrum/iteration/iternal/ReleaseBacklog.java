package ntut.csie.ezScrum.iteration.iternal;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class ReleaseBacklog {
	private IProject mProject;
	private IReleasePlanDesc mPlanDesc;
	private ArrayList<StoryObject> mStories;
	private Date mStartDate;
	private Date mEndDate;

	final private long OneDay = ScrumEnum.DAY_MILLISECOND;
	
	public ReleaseBacklog(ProjectObject project, IReleasePlanDesc plan, ArrayList<StoryObject> storyList) {
		mProject = new ProjectMapper().getProjectByID(project.getName());
		mPlanDesc = plan;
		//Release Plan下所有的Story
		mStories = storyList;

		init();
	}

	public ReleaseBacklog(IProject project, IReleasePlanDesc plan, ArrayList<StoryObject> storyList) {
		mProject = project;
		mPlanDesc = plan;
		//Release Plan下所有的Story
		mStories = storyList;

		init();
	}

	private void init() {
		mStartDate = DateUtil.dayFilter(mPlanDesc.getStartDate());
		mEndDate = DateUtil.dayFilter(mPlanDesc.getEndDate());
	}
	
	public IProject getProject(){
		return mProject;
	}
	
	public String getID(){
		return mPlanDesc.getID();
	}
	
	public String getName(){
		return mPlanDesc.getName();
	}
	
	public Date getStartDate() {
		return mStartDate;
	}
	
	public Date getEndDate() {
		return mEndDate;
	}
	
	public ArrayList<StoryObject> getStory(){
		return mStories;
	}
		
	public int getStoryCount() {
		if (mStories != null)
			return mStories.size();

		return 0;
	}
	
	public int getSprintPlanCounts() {
		return mPlanDesc.getSprintDescList().size();
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