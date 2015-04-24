package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBoard;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowReleaseBacklogAction extends Action {
	private static Log log = LogFactory.getLog(ShowReleaseBacklogAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		log.info(" Show Release Backlog. ");
		
		// get session info
		ProjectObject project = (ProjectObject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String ReleaseId = request.getParameter("releaseID");
		
		//取得ReleasePlan
		ReleasePlanHelper planHelper = new ReleasePlanHelper(project);
		ProductBacklogHelper productHelper = new ProductBacklogHelper(project);
		IReleasePlanDesc plan = planHelper.getReleasePlan(ReleaseId);
		
		ReleaseBacklog releaseBacklog;
		try {
			releaseBacklog = new ReleaseBacklog(project, plan, productHelper.getStoriesByRelease(plan));
		} catch (Exception e) {
			releaseBacklog = null;
		}
	
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, session.getAccount());

		if (ReleaseId != null && scrumRole.getAccessReleasePlan()) {
			request.setAttribute("releaseID", ReleaseId);			// return release Id
			request.setAttribute("releaseName", plan.getName());	// return release Name

			request.setAttribute("Stories", releaseBacklog.getStory());

			// release burndown chart draw by ReleaseBoard
			ReleaseBoard rboard = new ReleaseBoard(releaseBacklog);
			request.setAttribute("ReleaseBoard", rboard);

			return mapping.findForward("success");
		} else {
			return mapping.findForward("GuestOnly");
		}
	}
}
