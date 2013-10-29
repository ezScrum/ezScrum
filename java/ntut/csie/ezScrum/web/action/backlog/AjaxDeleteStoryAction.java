package ntut.csie.ezScrum.web.action.backlog;

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

public class AjaxDeleteStoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxDeleteStoryAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Delete Story in AjaxDeleteStoryAction.");
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		long ID = Long.parseLong(request.getParameter("issueID"));
		
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(session, project);
		StringBuilder result = productBacklogHelper.deleteStory(Long.toString(ID));	
		
		return result;
//		ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
//		helper.productBacklogHelper.deleteStory(helper, Long.toString(ID));	
//
//		StringBuilder result = new StringBuilder("");
//		result.append("{\"success\":true, \"Total\":1, \"Stories\":[{\"Id\":"+ID+"}]}");
//		
//		return result;
	}
}
