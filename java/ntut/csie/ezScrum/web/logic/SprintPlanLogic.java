package ntut.csie.ezScrum.web.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class SprintPlanLogic {
	
	private SprintPlanMapper sprintPlanMapper;
	private ProjectObject mProject;
	private IProject mIProject;

	public SprintPlanLogic(ProjectObject project) {
		sprintPlanMapper = new SprintPlanMapper(project);
		mProject = project;
	}
	
	public SprintPlanLogic(IProject project) {
		sprintPlanMapper = new SprintPlanMapper(project);
		mIProject = project;
	}
	
	/**
	 * @return sprint array, sort descent by ID
	 */
	// ori name: load()
	public List<ISprintPlanDesc> getSprintPlanListAndSortById() {
		List<ISprintPlanDesc> list = this.sprintPlanMapper.getSprintPlanList();
		
		list = this.sortById(list);
		return list;
	}
	
	/**
	 * @return sprint list, sort by StartDate
	 */
	// ori name: ListLoad()
	public List<ISprintPlanDesc> getSprintPlanListAndSortByStartDate() {
		List<ISprintPlanDesc> list = this.sprintPlanMapper.getSprintPlanList();
		
		list = this.sortByStartDate(list);
		return list;
	}
	
	//load the last plan, so perhaps the return is not the current plan.
	public ISprintPlanDesc loadCurrentPlan(){
		if (!String.valueOf(this.getCurrentSprintID()).equals("-1"))
			return this.sprintPlanMapper.getSprintPlan(String.valueOf(this.getCurrentSprintID()));
		else {
			List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById();
			if (descs.size() == 0)
				return null;
			Date current = new Date();
			for (ISprintPlanDesc desc:descs){
				if (DateUtil.dayFilter(desc.getEndDate()).getTime() > current.getTime())
					return desc;
			}
			return descs.get(descs.size()-1);			
		}
	} 
	
	public int getCurrentSprintID(){
		Date current = new Date();
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById();
		for (ISprintPlanDesc desc:descs){
			if (desc.isInSprint(current))
				return Integer.parseInt(desc.getID());
		}
		return -1;
	}
	
	/**
	 * sort by StartDate
	 * @param list
	 * @return
	 */
	private List<ISprintPlanDesc> sortByStartDate(List<ISprintPlanDesc> list) {
		List<ISprintPlanDesc> newList = new ArrayList<ISprintPlanDesc>();
		for (ISprintPlanDesc source : list) {
			Date addDate = DateUtil.dayFilter(source.getStartDate());		// 要新增的 Date
			int index = 0;
			for (ISprintPlanDesc target : newList) {
				Date cmpDate = DateUtil.dayFilter(target.getStartDate());	// 要被比對的 Date
				if ( addDate.compareTo(cmpDate) < 0 ) {
					break;
				}
				index++;
			}
			newList.add(index, source);
		}		
		
		return newList;
	}

	// sort descent by ID
	private List<ISprintPlanDesc> sortById(List<ISprintPlanDesc> list) {
		List<ISprintPlanDesc> newList = new ArrayList<ISprintPlanDesc>();
		for (ISprintPlanDesc source : list) {
			int index = 0;
			for (ISprintPlanDesc target : newList) {
				// 遞增排序
				if (Integer.parseInt(target.getID()) > Integer.parseInt(source.getID()))
					break;
				index++;
			}
			newList.add(index, source);
		}
		
		return newList;
	}
}
