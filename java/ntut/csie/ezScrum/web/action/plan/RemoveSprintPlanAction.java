package ntut.csie.ezScrum.web.action.plan;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RemoveSprintPlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(RemoveSprintPlanAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info(" Remove SprintPlan. ");
		
		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintId = request.getParameter("sprintID");

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, sprintId);
		List<IIssue> issues = sprintBacklogLogic.getStories();
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(project);	
		for(IIssue issue:issues){
			productBacklogLogic.dropStoryFromSprint(issue.getIssueID());
			
			if(!(issue.getReleaseID().equals(ScrumEnum.DIGITAL_BLANK_VALUE) || 
				 issue.getReleaseID().equals("-1"))){
				productBacklogLogic.removeReleaseTagFromIssue(issue.getIssueID());
			}
		}
		
		//刪除sprint資訊
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		SPhelper.deleteSprint(sprintId);
		StringBuilder result = new StringBuilder("{\"success\":true}");
		return result;
	}
}
