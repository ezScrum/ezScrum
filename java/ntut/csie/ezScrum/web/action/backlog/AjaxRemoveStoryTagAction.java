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

public class AjaxRemoveStoryTagAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxRemoveStoryTagAction.class);
	
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
		log.info("Remove Story Tag in AjaxRemoveStoryTagAction.");
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String storyId = request.getParameter("storyId");
		long tagId = Long.parseLong(request.getParameter("tagId"));
		
		
		StringBuilder result = (new ProductBacklogHelper(session, project)).getRemoveStoryTagResponseText(storyId,tagId);
		return result;
		
////		ITSPrefsStorage prefs = new ITSPrefsStorage(project, session);
////		String mantisUrl = prefs.getServerUrl();
//		ProductBacklogHelper PBHelper = new ProductBacklogHelper(project, session);
//		
//		PBHelper.removeStoryTag(storyId,tagId);
//		IIssue issue = PBHelper.getIssue(Long.parseLong(storyId));
//		
//		StringBuilder result = new StringBuilder("");
////		result.append(new Translation(mantisUrl).translateStoryToJson(issue));
//		result.append(new Translation().translateStoryToJson(issue));
//
//		return result;
	}
}