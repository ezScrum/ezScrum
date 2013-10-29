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

public class AjaxDeleteTagAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxDeleteTagAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Delete Tag in AjaxDeleteTagAction.");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

    	// get parameter info
    	String tagId = request.getParameter("tagId");
    	ProductBacklogHelper PBHelper = new ProductBacklogHelper(session,project);    	
    	StringBuilder result = PBHelper.getDeleteTagReponseText(tagId);

		return result;
//    	ProductBacklogHelper PBHelper = new ProductBacklogHelper(project,session);    	
//    	PBHelper.deleteTag(tagId);
//
//		StringBuilder result = new StringBuilder("");
//		result.append("<TagList><Result>success</Result>");
//		result.append("<IssueTag>");
//		result.append("<Id>" + tagId + "</Id>");
//		result.append("</IssueTag>");
//		result.append("</TagList>");
//
//		return result;
	}
}
