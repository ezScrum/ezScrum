package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxMoveSprintAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxMoveSprintAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		String oldID = request.getParameter("OldID");
		String newID = request.getParameter("NewID");
		int oldID_int = Integer.parseInt(oldID);
		int newID_int = Integer.parseInt(newID);

		//移動iterPlan.xml的資訊
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		SPhelper.moveSprintPlan(project, session, oldID_int, newID_int);

		StringBuilder result = new StringBuilder("true");
		return result;
	}
}
