package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

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

		// get project info form
		IProject project = (IProject) SessionManager.getProject(request);
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectInfoForm projectform = projectMapper.getProjectInfoForm(project);
		//		ProjectLogic projectLogic = new ProjectLogic();
		//		ProjectInfoForm projectform = projectLogic.getProjectInfoForm(project);

		projectform.setDisplayName(projectDisplayName);
		projectform.setAttachFileSize(attachFileSize);
		projectform.setComment(comment);
		projectform.setProjectManager(projectManager);

		// save back to project info form
		//		ProjectMapper projectMapper = new ProjectMapper();
		project = projectMapper.updateProject(projectform);

		// 設定 session
		request.getSession().removeAttribute(project.getName());
		request.getSession().setAttribute(project.getName(), project);

		// ezScrum v1.8
		ProjectObject projectObject = SessionManager.getProjectObject(request);
		projectObject.setDisplayName(projectDisplayName);
		projectObject.setAttachFileSize(attachFileSize);
		projectObject.setComment(comment);
		projectObject.setManager(projectManager);
		SessionManager.setProjectObject(request, new ProjectHelper().updateProject(projectObject));
		
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
