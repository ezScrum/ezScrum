package ntut.csie.ezScrum.web.dataObject;

import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

public class ReleasePlanObject {
	private String id;
	private String name;
	private String startDate;
	private String endDate;
	private String description;
	private List<SprintObject> sprintPlan;
	
	public ReleasePlanObject(IReleasePlanDesc releasePlanDesc) {
		setId(releasePlanDesc.getID());
		setName(releasePlanDesc.getName());
		setStartDate(releasePlanDesc.getStartDate());
		setEndDate(releasePlanDesc.getEndDate());
		setDescription(releasePlanDesc.getDescription());
		setSprintPlan(new LinkedList<SprintObject>());
		
		List<ISprintPlanDesc> sprintDescList = releasePlanDesc.getSprintDescList();
		for (ISprintPlanDesc sprintDesc : sprintDescList) {
			getSprintPlan().add(new SprintObject(sprintDesc));
		}
			
    }

	public String getId() {
	    return id;
    }

	public void setId(String id) {
	    this.id = id;
    }

	public String getName() {
	    return name;
    }

	public void setName(String name) {
	    this.name = name;
    }

	public String getStartDate() {
	    return startDate;
    }

	public void setStartDate(String startDate) {
	    this.startDate = startDate;
    }

	public String getEndDate() {
	    return endDate;
    }

	public void setEndDate(String endDate) {
	    this.endDate = endDate;
    }

	public String getDescription() {
	    return description;
    }

	public void setDescription(String description) {
	    this.description = description;
    }

	public List<SprintObject> getSprintPlan() {
	    return sprintPlan;
    }

	public void setSprintPlan(List<SprintObject> sprintPlan) {
	    this.sprintPlan = sprintPlan;
    }
}
