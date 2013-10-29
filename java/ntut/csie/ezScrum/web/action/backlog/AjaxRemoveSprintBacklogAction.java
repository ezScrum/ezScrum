package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class AjaxRemoveSprintBacklogAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxRemoveSprintBacklogAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
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
		long issueID = Long.parseLong(request.getParameter("issueID"));
		String sprintID = request.getParameter("sprintID");

		String result = "";
		try{
			ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
			ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(session, project);
			
			//將Story自Sprint移除, 
			productBacklogLogic.removeStoryFromSprint(issueID);
			
			//移除Sprint下的Story與Release的關係
			if(!(helper.getIssue(issueID).getReleaseID().equals(ScrumEnum.DIGITAL_BLANK_VALUE) ||
				 helper.getIssue(issueID).getReleaseID().equals("-1"))){
				productBacklogLogic.removeReleaseTagFromIssue(Long.toString(issueID));
			}
			
			result = "<DropStory><Result>true</Result><Story><Id>" + issueID + "</Id></Story></DropStory>";
		}catch (Exception e) {
			result = "<DropStory><Result>false</Result></DropStory>";
		}
		
		return new StringBuilder(result);
	}
}