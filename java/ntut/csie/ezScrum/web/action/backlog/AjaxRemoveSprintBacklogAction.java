package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

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
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		long issueId = Long.parseLong(request.getParameter("issueID"));
		String result = "";
		
		try{
			ProductBacklogHelper PBHelper = new ProductBacklogHelper(project);
			
			// 將 Story 自 Sprint 移除
			PBHelper.dropStoryFromSprint(issueId);
			
			result = "<DropStory><Result>true</Result><Story><Id>" + issueId + "</Id></Story></DropStory>";
		}catch (Exception e) {
			result = "<DropStory><Result>false</Result></DropStory>";
		}
		
		return new StringBuilder(result);
	}
}