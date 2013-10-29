package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IProject;

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
		IProject project = (IProject) SessionManager.getProject(request);
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

		public ProjectUI(IProject p) {
			if (p != null) {
				IProjectDescription desc = p.getProjectDesc();
				this.ProjectName = desc.getName();
				this.ProjectDisplayName = desc.getDisplayName();
				this.Commnet = desc.getComment();
				this.ProjectManager = desc.getProjectManager();
				this.AttachFileSize = desc.getAttachFileSize();
				this.ProjectCreateDate = DateUtil.format(desc.getCreateDate(), DateUtil._16DIGIT_DATE_TIME);
			}
		}
	}
}
