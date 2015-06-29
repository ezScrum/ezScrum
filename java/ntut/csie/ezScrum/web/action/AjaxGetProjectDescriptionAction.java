package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.DateUtil;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class AjaxGetProjectDescriptionAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetProjectDescriptionAction.class);

	@Override
	public boolean isValidAction() {
		return true;
	}

	@Override
	public boolean isXML() {
		return false;	// html
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info(" Get Project Description. In Project Summary Page.");
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		ProjectUI pui = new ProjectUI(project);
		return new StringBuilder((new Gson()).toJson(pui));
	}

	private class ProjectUI {
		private String ID = "0";
		private String ProjectName = "";
		private String ProjectDisplayName = "";
		private String Commnet = "";
		private String ProjectManager = "";
		private String AttachFileSize = "";
		private String ProjectCreateDate = "";

		public ProjectUI(ProjectObject project) {
			if (project != null) {
				ID = String.valueOf(project.getId());
				ProjectName = project.getName();
				ProjectDisplayName = project.getDisplayName();
				Commnet = project.getComment();
				ProjectManager = project.getManager();
				AttachFileSize = String.valueOf(project.getAttachFileSize());
				ProjectCreateDate = DateUtil.format(project.getCreateTime(), DateUtil._16DIGIT_DATE_TIME);
			}
		}
	}
}
