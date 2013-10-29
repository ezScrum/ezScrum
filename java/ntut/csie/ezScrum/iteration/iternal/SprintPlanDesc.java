package ntut.csie.ezScrum.iteration.iternal;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintPlanDesc implements ISprintPlanDesc {
	private int m_id = -1;
	private String m_goal = "";
	private int m_interval = 0;
	private int m_memberNumber = 0;
	private int m_factor = 0;
	private int m_availableDays = 0;
	private Date m_startDate = null;
	private Date m_demoDate = null;
	private String m_demoPlace = "";
	private String m_notes = "";
	private double m_actualCost = 0;
	private Map<Integer,String> m_taskBoardStageMap = null;

	@Override
	public String getInterval() {
		if (m_interval == 0)
			return "";
		return Integer.toString(m_interval);
	}

	@Override
	public String getMemberNumber() {
		if (m_memberNumber == 0)
			return "0";
		return Integer.toString(m_memberNumber);
	}

	
	@Override
	public String getStartDate() {
		if (this.m_startDate == null)
			return "";
		return DateUtil.formatBySlashForm(m_startDate);
	}

	@Override
	public void setInterval(String days) {
		m_interval = Integer.parseInt(days);
	}

	@Override
	public void setMemberNumber(String number) {
		m_memberNumber = Integer.parseInt(number);
	}


	@Override
	public void setStartDate(String date) {
		m_startDate = DateUtil.dayFilter(date);
	}

	@Override
	public String getID() {
		return Integer.toString(m_id);
	}
	
	public void setID(String id){
		m_id = Integer.parseInt(id);
	}

	@Override
	public String getEndDate() {
		if (m_startDate==null)
			return "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(m_startDate);
		calendar.add(Calendar.WEEK_OF_YEAR, m_interval);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return DateUtil.formatBySlashForm(calendar.getTime());
	}

	@Override
	public boolean isInSprint(Date date) {
		if (m_startDate == null)
			return false;
		if (date.getTime() < m_startDate.getTime())
			return false;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(m_startDate);
		calendar.add(Calendar.WEEK_OF_YEAR, m_interval);
		if (date.getTime() > calendar.getTimeInMillis())
			return false;
		return true;
	}

	@Override
	public String getFocusFactor() {
//		if (m_factor==0)
//			return "";
		return Integer.toString(m_factor);
	}

	@Override
	public void setFocusFactor(String factor) {
//		if(factor.isEmpty())
//			m_factor = 0;
//		else
			m_factor = Integer.parseInt(factor);		
	}

	@Override
	public String getGoal() {
		return m_goal;
	}

	@Override
	public void setGoal(String goal) {
		m_goal = goal;
	}

	@Override
	public String getAvailableDays() {
		return Integer.toString(m_availableDays);
	}

	@Override
	public String getDemoDate() {
		if (m_demoDate == null)
			return "";
		
		return DateUtil.formatBySlashForm(m_demoDate);
	}

	@Override
	public String getNotes() {
		return m_notes;
	}

	@Override
	public void setAvailableDays(String days) {
		if (days == null || days.equals(""))
			return;
		
		m_availableDays = Integer.parseInt(days);
	}

	@Override
	public void setDemoDate(String date) {
		if (date == null || date.equals(""))
			return;
		
		m_demoDate = DateUtil.dayFilter(date);
	}

	@Override
	public void setNotes(String notes) {
		m_notes = notes;
	}

	@Override
	public String getDemoPlace() {
		return m_demoPlace;
	}

	@Override
	public void setDemoPlace(String place) {
		m_demoPlace = place;
	}
	
	@Override
	public String getActualCost() {
		return String.valueOf(m_actualCost);
	}
	
	@Override
	public void setActualCost(String actualCost) {
		if (actualCost == null || actualCost.equals("")){
			return;
		}
		m_actualCost = Double.parseDouble(actualCost);
	}

	@Override
	public void setTaskBoardStageMap(Map<Integer, String> taskBoardStageMap) {
		m_taskBoardStageMap = taskBoardStageMap;
		
	}

	@Override
	public Map<Integer, String> getTaskBoardStageMap() {
		return m_taskBoardStageMap;
	}
	
}
