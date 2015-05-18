package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

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
		ProjectObject project = SessionManager.getProjectObject(request);
		
		long storyId = Long.parseLong(request.getParameter("storyId"));
		long tagId = Long.parseLong(request.getParameter("tagId"));
		
		StringBuilder result = new ProductBacklogHelper(project).getAddStoryTagResponseText(storyId, tagId);
		
		return result;
	}
}
