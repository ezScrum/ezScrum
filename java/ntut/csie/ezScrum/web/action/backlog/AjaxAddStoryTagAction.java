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

public class AjaxAddStoryTagAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxAddStoryTagAction.class);
	
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
		log.info("Ajax Add Story Tag in AjaxAddStoryTagAction.");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		String storyId = request.getParameter("storyId");
		String tagId = request.getParameter("tagId");
		
		StringBuilder result = new ProductBacklogHelper(session, project).getAddStoryTagResponseText(storyId, tagId);
		
		return result;
		
////		ITSPrefsStorage prefs = new ITSPrefsStorage(project, session);
////		String mantisUrl = prefs.getServerUrl();
//		ProductBacklogHelper PBHelper = new ProductBacklogHelper(project, session);
//		
//		PBHelper.addStoryTag(storyId, tagId);
//		
//		IIssue issue = PBHelper.getIssue(Long.parseLong(storyId));
//		
//		StringBuilder result = new StringBuilder("");
//		
////		result.append(new Translation(mantisUrl).translateStoryToJson(issue));
//		result.append(new Translation().translateStoryToJson(issue));
//
//		return result;
	}
}
