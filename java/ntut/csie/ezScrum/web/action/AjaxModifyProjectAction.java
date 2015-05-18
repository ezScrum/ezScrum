package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxModifyProjectAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxModifyProjectAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getEditProject();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("modify Project!");

		// get parameter
		String projectDisplayName = CheckStringValue(request.getParameter("ProjectDisplayName"));
		String attachFileSize = CheckStringValue(request.getParameter("AttachFileSize"));
		String comment = CheckStringValue(request.getParameter("Commnet"));
		String projectManager = CheckStringValue(request.getParameter("ProjectManager"));

		ProjectObject project = SessionManager.getProjectObject(request);
		project
			.setDisplayName(projectDisplayName)
			.setAttachFileSize(Long.parseLong(attachFileSize))
			.setComment(comment)
			.setManager(projectManager)
			.save();
		
		request.getSession().removeAttribute(project.getName());
		request.getSession().setAttribute(project.getName(), project);
		
		SessionManager sessionManager = new SessionManager(request);
		sessionManager.setProjectObject(request, project);
		
		return new StringBuilder("success");
	}

	private String CheckStringValue(String str) {
		if (str == null)
			return "";

		if (str.length() == 0)
			return "";

		return str;
	}
}
