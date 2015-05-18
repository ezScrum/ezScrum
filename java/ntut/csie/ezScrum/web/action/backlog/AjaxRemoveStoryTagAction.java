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
		ProjectObject project = SessionManager.getProjectObject(request);

		// get parameter info
		long storyId = Long.parseLong(request.getParameter("storyId"));
		long tagId = Long.parseLong(request.getParameter("tagId"));

		StringBuilder result = new ProductBacklogHelper(project)
				.getRemoveStoryTagResponseText(storyId, tagId);
		return result;
	}
}