package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBoard;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


public class GetReleaseBurndownChartDataAction extends PermissionAction {
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
	
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String releaseID = request.getParameter("ReleaseID");			// get release ID
		
		ReleasePlanHelper rpHelper = new ReleasePlanHelper(project);
		return rpHelper.getReleaseBurndownChartData(project, session, releaseID);
		
//		ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, session);
//		IReleasePlanDesc plan = rpHelper.getReleasePlan(releaseID);
//		
//		ReleaseBacklog releaseBacklog = null;
//		StringBuilder result = new StringBuilder("");
//		try {
//			releaseBacklog = new ReleaseBacklog(project, plan, pbHelper.getStoriesByRelease(plan));
//			ReleaseBoard board = new ReleaseBoard(releaseBacklog);
//			Translation tr = new Translation();
//			result.append(tr.translateBurndownChartDataToJson(board.getStoryIdealPointMap(), board.getStoryRealPointMap()));
//		} catch (Exception e) {
//			releaseBacklog = null;
//			result.append("{success: \"false\"}");
//		}
//		
//		return result;
	}
}
