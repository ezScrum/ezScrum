package ntut.csie.ezScrum.web.action.export;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetStoryCountAction extends PermissionAction {
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		
		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		SprintBacklogHelper SBhelper = new SprintBacklogHelper(project);
		
		// get selected ReleasePlan list
		String releases = request.getParameter("releases");
		
		// 取得ReleasePlans
		List<IReleasePlanDesc> releaseDescs = RPhelper.getReleasePlansByIDs(releases);
		StringBuilder result = new StringBuilder(RPhelper.getStoryCountChartJSon(releaseDescs, SBhelper));
		return result;
	}
}
