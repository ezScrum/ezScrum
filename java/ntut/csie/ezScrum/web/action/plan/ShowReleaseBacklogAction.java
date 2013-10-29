package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBoard;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowReleaseBacklogAction extends Action {
	private static Log log = LogFactory.getLog(ShowReleaseBacklogAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// ger parameter info
		String R_ID = request.getParameter("releaseID");
		//取得ReleasePlan
		
		ReleasePlanHelper planHelper = new ReleasePlanHelper(project);
		ProductBacklogHelper productHelper = new ProductBacklogHelper(project, session);
		IReleasePlanDesc plan = planHelper.getReleasePlan(R_ID);
		
		ReleaseBacklog releaseBacklog;
		try {
			releaseBacklog = new ReleaseBacklog(project, plan, 
					productHelper.getStoriesByRelease(plan));
		}
		catch (Exception e) {
			// TODO: handle exception
			releaseBacklog = null;
		}
	
//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, session.getAccount());
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, session.getAccount());
		
		if (R_ID != null && sr.getAccessReleasePlan()) {			
			request.setAttribute("releaseID", R_ID);		// return release ID
			request.setAttribute("releaseName", plan.getName());		// return release Name

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
