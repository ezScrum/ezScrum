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

public class AjaxAddNewTagAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxAddNewTagAction.class);
	
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
		log.info("Add New Tag in AjaxAddNewTagAction.");
		// get session info 
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		String newTagName = request.getParameter("newTagName");
		
		StringBuilder result = (new ProductBacklogHelper(project)).getAddNewTagResponsetext(newTagName);
		return result;
	}
}
