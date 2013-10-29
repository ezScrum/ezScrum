package ntut.csie.ezScrum.iteration.iternal;

import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

public class ReleasePlanDesc implements IReleasePlanDesc {
	private String ID;
	private String Name;
	private String StartDate;
	private String EndDate;
	private String Description;
	private List<ISprintPlanDesc> SprintList;
	
	public ReleasePlanDesc() {
		this.ID = "0";
		this.Name = "";
		this.StartDate = "";
		this.EndDate = "";
		this.Description = "";
		this.SprintList = new LinkedList<ISprintPlanDesc>();
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
	public List<ISprintPlanDesc> getSprintDescList() {
		return this.SprintList;
	}

	@Override
	public void setSprintDescList(List<ISprintPlanDesc> SprintList) {
		this.SprintList = SprintList;
	}
}
