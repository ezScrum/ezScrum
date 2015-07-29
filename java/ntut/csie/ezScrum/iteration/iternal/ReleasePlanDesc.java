package ntut.csie.ezScrum.iteration.iternal;

import java.util.ArrayList;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

public class ReleasePlanDesc implements IReleasePlanDesc {
	private String ID;
	private String Name;
	private String StartDate;
	private String EndDate;
	private String Description;
	private ArrayList<SprintObject> SprintList;
	
	public ReleasePlanDesc() {
		this.ID = "0";
		this.Name = "";
		this.StartDate = "";
		this.EndDate = "";
		this.Description = "";
		this.SprintList = new ArrayList<SprintObject>();
	}

	@Override
	public void setID(String id) {
		this.ID = id;
	}
	
	@Override
	public void setName(String Name) {
		this.Name = Name;
	}
	
	@Override
	public void setStartDate(String StartDate) {
		this.StartDate = StartDate;		
	}
	
	@Override
	public void setEndDate(String EndDate) {
		this.EndDate = EndDate;
	}
	
	@Override
	public void setDescription(String Description) {
		this.Description = Description;
	}
	
	@Override
	public String getID() {
		return this.ID;
	}
	
	@Override
	public String getName() {
		return this.Name;
	}
	
	@Override
	public String getStartDate() {
		return this.StartDate;
	}
	
	@Override
	public String getEndDate() {
		return this.EndDate;
	}
	
	@Override
	public String getDescription() {
		return this.Description;
	}

	@Override
	public ArrayList<SprintObject> getSprints() {
		return this.SprintList;
	}

	@Override
	public void setSprintDescList(ArrayList<SprintObject> SprintList) {
		this.SprintList = SprintList;
	}
}
