package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RemoveReleaseBacklogAction extends Action{
	private static Log log = LogFactory.getLog(RemoveReleaseBacklogAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info(" Remove ReleaseBacklog. ");
		
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String issueId = request.getParameter("issueID");
		
		if (issueId != null) {
			ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);
			IIssue issue = PBHelper.getIssue(Long.parseLong(issueId));
			
			if(!(issue.getSprintID().equals(ScrumEnum.DIGITAL_BLANK_VALUE)) ||
				 issue.getSprintID().equals("-1")) {
				// remove release tag to mantis notes
				PBHelper.removeReleaseTagFromIssue(Long.parseLong(issueId));
			} else {
				// remove release, sprint tag to mantis notes
				PBHelper.removeReleaseSprint(Long.parseLong(issueId));
			}
			return mapping.findForward("success");
		} else {
			return mapping.findForward("error");
		}
	}
}
