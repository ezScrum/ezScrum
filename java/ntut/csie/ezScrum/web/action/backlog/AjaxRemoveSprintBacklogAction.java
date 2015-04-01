package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
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
		log.info(" Remove Sprint Backlog. ");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		// get parameter info
		long issueId = Long.parseLong(request.getParameter("issueID"));
		String result = "";
		
		try{
			ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);
			
			// 將 Story 自 Sprint 移除
			PBHelper.removeStoryFromSprint(issueId);
			
			// 移除 Sprint 下的 Story 與 Release 的關係
			if(!(PBHelper.getStory(issueId).getReleaseID().equals(ScrumEnum.DIGITAL_BLANK_VALUE) ||
				 PBHelper.getStory(issueId).getReleaseID().equals("-1"))){
				PBHelper.removeReleaseTagFromIssue(issueId);
			}
			
			result = "<DropStory><Result>true</Result><Story><Id>" + issueId + "</Id></Story></DropStory>";
		}catch (Exception e) {
			result = "<DropStory><Result>false</Result></DropStory>";
		}
		
		return new StringBuilder(result);
	}
}