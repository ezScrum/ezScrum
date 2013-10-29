package ntut.csie.ezScrum.iteration.core;

import java.util.Date;
import java.util.Map;

public interface ISprintPlanDesc {
	public void setID(String id);
	public String getID();
	
	public void setStartDate(String date);
	public String getStartDate();
	
	public boolean isInSprint(Date date);
	
	public void setInterval(String days);
	public String getInterval();

	public String getMemberNumber();
	public void setMemberNumber(String number);
	
	public void setFocusFactor(String factor);
	public String getFocusFactor();
	
	public void setGoal(String goal);
	public String getGoal();
	
	public void setAvailableDays(String days);
	public String getAvailableDays();
	
	public void setDemoDate(String date);
	public String getDemoDate();
	
	public void setNotes(String notes);
	public String getNotes();
	
	public void setDemoPlace(String place);
	public String getDemoPlace();
	
	public String getEndDate();
	
	public void setActualCost(String actualCost);
	public String getActualCost();
	
	public void setTaskBoardStageMap( Map<Integer,String> taskBoardStageMap );
	public Map<Integer,String> getTaskBoardStageMap();//task board stage id map to name
	
}
