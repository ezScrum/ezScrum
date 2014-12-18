package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RemoveReleasePlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(RemoveReleasePlanAction.class);
	
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
		log.info(" Remove Release Plan. ");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String ReleaseId = request.getParameter("releaseID");

		ReleasePlanHelper helper = new ReleasePlanHelper(project);		
		IReleasePlanDesc desc = helper.getReleasePlan(ReleaseId);
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);
		IStory[] stories = PBHelper.getStoriesByRelease(desc);
		
		// 移除 Release 與底下 Story 的關係
		for(int index = 0; index < stories.length; index++){
			if(!(stories[index].getSprintID().equals(ScrumEnum.DIGITAL_BLANK_VALUE) ||
				 stories[index].getSprintID().equals("-1")) ) {
				PBHelper.removeReleaseTagFromIssue(stories[index].getIssueID());
			} else {
				PBHelper.removeReleaseSprint(stories[index].getIssueID());
			}
		}
		// 刪除Release
		helper.deleteReleasePlan(ReleaseId);
		
		return new StringBuilder("true");
	}
}
