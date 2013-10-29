package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DeleteAttachFileAction extends PermissionAction {
	private static Log log = LogFactory.getLog(DeleteAttachFileAction.class);
	
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
		
		// 取得刪除file前需要的資料
		// get session info
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintID = request.getParameter("sprintID");
		long fileID = Long.parseLong(request.getParameter("fileID"));
		
		// 透過file的 id 刪除attach file
		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, sprintID)).getSprintBacklogMapper();
		backlog.deleteAttachFile(fileID);		

		StringBuilder result = new StringBuilder("success");
		return result;
	}
}