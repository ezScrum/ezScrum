package ntut.csie.ezScrum.iteration.core;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.SprintObject;

public interface IReleasePlanDesc {
	public void setID(String id);
	public String getID();
	public void setName(String Name);
	public String getName();
	public void setStartDate(String StartDate);
	public String getStartDate();
	public void setEndDate(String EndDate);
	public String getEndDate();
	public void setDescription(String Description);
	public String getDescription();
	
	public void setSprintDescList(ArrayList<SprintObject> SprintList);
	public ArrayList<SprintObject> getSprintDescList();
}
