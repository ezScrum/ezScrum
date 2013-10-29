package ntut.csie.ezScrum.iteration.iternal;

import java.util.Date;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class ReleaseBacklog {
	private IProject m_project;
	private IReleasePlanDesc m_planDesc;
	private IStory[] m_stories;
	private Date m_startDate;
	private Date m_endDate;

	final private long OneDay = ScrumEnum.DAY_MILLISECOND;

	public ReleaseBacklog(IProject project, IReleasePlanDesc plan, IStory[] stories) {
		m_project = project;
		m_planDesc = plan;
		//Release Plan下所有的Story
		m_stories = stories;

		init();
	}

	private void init() {
		m_startDate = DateUtil.dayFilter(m_planDesc.getStartDate());
		m_endDate = DateUtil.dayFilter(m_planDesc.getEndDate());
	}
	
	public IProject getProject(){
		return m_project;
	}
	
	public String getID(){
		return m_planDesc.getID();
	}
	
	public String getName(){
		return m_planDesc.getName();
	}
	
	public Date getStartDate() {
		return m_startDate;
	}
	
	public Date getEndDate() {
		return m_endDate;
	}
	
	public IStory[] getStory(){
		return m_stories;
	}
		
	public int getStoryCount(){
		if (m_stories != null)
			return m_stories.length;

		return 0;
	}
	
	public int getSprintPlanCounts() {
		return m_planDesc.getSprintDescList().size();
	}
	
	// 取得該日期前已完成的Story數量
	public double getDoneStoryByDate(Date date) {
		double count = 0;
		for (IStory item : m_stories) {
			// Story Close的時間
			Date closedDate = item.getStatusUpdated(new Date(date.getTime()	+ OneDay), ITSEnum.CLOSED_STATUS);
			if (closedDate != null){
				// 在該日期前Close的 Count就+1 (closedDate < date)
				if (closedDate.before(date))
					count++;
			}
		}
		return count;
	}
	
	// 所有story done後，不論何時把story拉到done都把real point設成0
	public double getReleaseAllStoryDone() {
		double count = m_stories.length;
		for (IStory item : m_stories) {
			// story狀態為done的 count就-1
			if (item.getStatus().equals(ITSEnum.S_CLOSED_STATUS)) {
				count--;
			}
		}
		return count;
	}
}