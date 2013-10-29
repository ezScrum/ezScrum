package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxMoveStorySprint extends PermissionAction {
	// Variables
	private static Log log = LogFactory.getLog(AjaxMoveStorySprint.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog() && super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		/*-----------------------------------------------------------
		*	從Request中取得要變更的資訊
		-------------------------------------------------------------*/
		//Project
		IProject project = (IProject) SessionManager.getProject(request);
		//Issue ID
		long issueID = Long.parseLong(request.getParameter("issueID"));
		String moveID = request.getParameter("moveID");	//	Sprint ID
		String type = request.getParameter("type");		//	取得要移動Release或者是Sprint
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		ProductBacklogHelper helper = new ProductBacklogHelper(session, project);
		helper.moveStory(issueID, moveID, type);
		return new StringBuilder("");
	}
}
