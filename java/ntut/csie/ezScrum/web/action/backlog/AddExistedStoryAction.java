package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddExistedStoryAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(AddExistedStoryAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String[] issueIDs = request.getParameterValues("selects");
		String sprintID = request.getParameter("sprintID");
		String releaseID = request.getParameter("releaseID");
		
		ArrayList<Long> list = new ArrayList<Long>();
		for (String issueID : issueIDs) {
			list.add(Long.parseLong(issueID));
		}
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, sprintID);
		sprintBacklogHelper.addExistedStory(list, releaseID);
		
		return new StringBuilder("");
		
//		ProductBacklogLogic helper = new ProductBacklogLogic(session, project);
//		
//		if ( (sprintID != null) && (! sprintID.isEmpty()) && (! sprintID.equals("-1")) ) {
//			//將Story加入Sprint當中
//			helper.addIssueToSprint(list, sprintID);
//			
//			//檢查Sprint是否有存在於某個Release中
//			ReleasePlanHelper releasePlan = new ReleasePlanHelper(project);
//			String sprintReleaseID = releasePlan.getReleaseID(sprintID);
//			
//			//如果有的話，將所有Story加入Release
//			if(!(sprintReleaseID.equals("0"))) {
//				helper.addReleaseTagToIssue(list, sprintReleaseID);
//			}
//		} else {
//			helper.addReleaseTagToIssue(list, releaseID);
//		}
//			
//		return new StringBuilder("");
//	
////		ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
////			
////		if ( (sprintID != null) && (! sprintID.isEmpty()) && (! sprintID.equals("-1")) ) {
////			//將Story加入Sprint當中
////			helper.add(list, sprintID);
////			
////			//檢查Sprint是否有存在於某個Release中
////			ReleasePlanHelper releasePlan = new ReleasePlanHelper(project);
////			String sprintReleaseID = releasePlan.getReleaseID(sprintID);
////			
////			//如果有的話，將所有Story加入Release
////			if(!(sprintReleaseID.equals("0"))) {
////				helper.addRelease(list, sprintReleaseID);
////			}
////		} else {
////			helper.addRelease(list, releaseID);
////		}
////			
////		return new StringBuilder("");
	}
}
