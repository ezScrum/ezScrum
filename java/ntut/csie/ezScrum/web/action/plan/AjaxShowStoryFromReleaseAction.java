package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxShowStoryFromReleaseAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxShowStoryFromReleaseAction.class);
	
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
		log.info(" Show Story From Release. ");
		
		// get session info
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		IProject iProject = new ProjectMapper().getProjectByID(project.getName());
		
		ReleasePlanHelper planHelper = new ReleasePlanHelper(project);
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(project);
		
		// get parameter info
		String releaseId = request.getParameter("Rid");
		
		// 取得 ReleasePlan
		IReleasePlanDesc plan = planHelper.getReleasePlan(releaseId);
		
		ArrayList<StoryObject> storyList = PBHelper.getStoriesByRelease(plan);
		return planHelper.showStoryFromRelease(iProject, releaseId, storyList);
	}
}
