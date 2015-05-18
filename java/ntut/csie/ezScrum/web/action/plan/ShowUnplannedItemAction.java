package ntut.csie.ezScrum.web.action.plan;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowUnplannedItemAction extends PermissionAction {
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessUnplannedItem();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

    	// get parameter info
    	String sprintID = request.getParameter("SprintID");
    	
    	SprintPlanHelper spHelper = new SprintPlanHelper(project);
    	UnplannedItemHelper uiHelper = new UnplannedItemHelper(project, session);
    	
    	int currentSprintID = spHelper.getCurrentSprintID();
		
		//如果沒有指定sprint的ID，則以目前的sprintID為準，如果也沒有的話則以最後一個sprint id為準
    	if( (sprintID == null) || (sprintID.isEmpty()) ) {
    		if(currentSprintID == -1) {
    			sprintID = Integer.toString(spHelper.getLastSprintId());
    		} else {
        		sprintID = Integer.toString(currentSprintID);
    		}
    	}
    	
    	try {
			return uiHelper.getListXML(sprintID);
		} catch (SQLException e) {
			return new StringBuilder("error");
		}
	}
}