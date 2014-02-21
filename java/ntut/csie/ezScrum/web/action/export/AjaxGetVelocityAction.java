package ntut.csie.ezScrum.web.action.export;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetVelocityAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		SprintBacklogHelper SBhelper = new SprintBacklogHelper(project, session);

		// get selected ReleasePlan list
		String releases = request.getParameter("releases");

		// 取得ReleasePlans
		List<IReleasePlanDesc> releaseDescs = RPhelper.getReleasePlansByIDs(releases);
		StringBuilder result = new StringBuilder(RPhelper.getSprintVelocityToJSon(releaseDescs, SBhelper));
		return result;
	}
}