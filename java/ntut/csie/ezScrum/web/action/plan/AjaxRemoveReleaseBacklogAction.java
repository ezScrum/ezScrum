package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxRemoveReleaseBacklogAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxRemoveReleaseBacklogAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		String issueID = request.getParameter("issueID");
		
		String result = "";
		try{
			if (issueID != null) {
				ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, session);
				
				IIssue issue = pbHelper.getIssue(Long.parseLong(issueID));
				if(!(issue.getSprintID().equals(ScrumEnum.DIGITAL_BLANK_VALUE) ||
					 issue.getSprintID().equals("-1")))
					pbHelper.removeReleaseSprint(issueID);	// remove release, sprint tag to mantis notes
				else
//					pbHelper.removeRelease(issueID);	// remove release tag to mantis notes
					(new ProductBacklogLogic(session, project)).removeReleaseTagFromIssue(issueID);	// remove release tag to mantis notes
				
				result = "<DropStory><Result>true</Result><Story><Id>" + issueID + "</Id></Story></DropStory>";
			} else {
				result = "<DropStory><Result>false</Result></DropStory>";
			}
		} catch (Exception e) {
			result = "<DropStory><Result>false</Result></DropStory>";
		}
		
		return new StringBuilder(result);
	}
}