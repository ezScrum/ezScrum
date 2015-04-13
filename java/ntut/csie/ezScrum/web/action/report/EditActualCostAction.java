package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class EditActualCostAction extends PermissionAction{
	
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
		
		IProject project = SessionManager.getProject(request);
			
//		int sprintID = Integer.parseInt(request.getParameter("sprintID"));
//		Double actualCost = Double.valueOf(request.getParameter("actualCost"));
		String sprintId = request.getParameter("sprintID");
		String actualCost = request.getParameter("actualCost");
		
		SprintPlanHelper sprintPlanHelper  = new SprintPlanHelper( project );
		sprintPlanHelper.editSprintPlanForActualCost(sprintId, actualCost);
		
//		ISprintPlanDesc sprintPlan = sprintPlanHelper.loadPlan( sprintID );
//		sprintPlan.setActualCost( String.valueOf( actualCost ) );
		
//      SprintPlanDescSaver sprintPlanDescSaver = new SprintPlanDescSaver( project );
//      sprintPlanDescSaver.editSprintPlanForActualCost( sprintPlan );
		
		return new StringBuilder("ok");
	}

}
