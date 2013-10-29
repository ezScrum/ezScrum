package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.PerformanceIndexDataMaker;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowActualCostAction extends PermissionAction{
	
	
	@Override
	public boolean isValidAction() {
		return true; //TODO
	}

	@Override
	public boolean isXML() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		// get session info
		
		int sprintID = Integer.parseInt( request.getParameter( "sprintID" ) );
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		SprintPlanHelper sprintPlanHelper  = new SprintPlanHelper( project );
		List<ISprintPlanDesc> sprintPlanArray = sprintPlanHelper.loadListPlans();
		
//		List<SprintBacklogMapper> sprintBacklogArrayList = new ArrayList<SprintBacklogMapper>();
//		
//		for( ISprintPlanDesc sprintPlan : sprintPlanArray ){
//			SprintBacklogMapper tempSprintBacklog = new SprintBacklogMapper( project, session, Integer.parseInt( sprintPlan.getID() ) );
//			sprintBacklogArrayList.add( tempSprintBacklog );
//		}
//		
//		PerformanceIndexDataMaker performanceIndexDataMaker = new PerformanceIndexDataMaker(sprintPlanArray,sprintBacklogArrayList);
		
		List<SprintBacklogLogic> sprintBacklogArrayList = new ArrayList<SprintBacklogLogic>();
		
		for( ISprintPlanDesc sprintPlan : sprintPlanArray ){
			SprintBacklogLogic tempSprintBacklog = new SprintBacklogLogic( project, session, sprintPlan.getID() );
			sprintBacklogArrayList.add( tempSprintBacklog );
		}
		
		PerformanceIndexDataMaker performanceIndexDataMaker = new PerformanceIndexDataMaker(sprintPlanArray,sprintBacklogArrayList);
		
		double actualCost = performanceIndexDataMaker.getActualCostBySprintID( sprintID );
		StringBuilder sb = new StringBuilder();
		sb.append( actualCost );
		
		return sb;
	}

}
