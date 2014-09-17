package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxAddNewStoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxAddNewStoryAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Add New Story in AjaxAddNewStoryAction.");
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
//		ITSPrefsStorage prefs = new ITSPrefsStorage(project, session);
		
//		String mantisUrl = prefs.getServerUrl();
		String name = request.getParameter("Name");
		String importance = request.getParameter("Importance");
		String estimate = request.getParameter("Estimate");
		String value = request.getParameter("Value");
		String howToDemo = request.getParameter("HowToDemo");
		String notes = request.getParameter("Notes");
		String description = "";
		String sprintID = request.getParameter("sprintId");
		String tagIDs = request.getParameter("TagIDs");
		String releaseID = "";
		
		StoryInformation storyInformation = new StoryInformation(name, importance, estimate, value, howToDemo, notes, description, sprintID, releaseID, tagIDs);
		releaseID = new ReleasePlanHelper(project).getReleaseID(storyInformation.getSprintID());
		if (!releaseID.equals("0")){
			storyInformation.setReleaseID(releaseID);
		}
		
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(session, project);
		IIssue issue = productBacklogHelper.addNewStory(storyInformation);
		StringBuilder result = productBacklogHelper.translateStoryToJson(issue);
		return result;

//		ProductBacklogHelper helper = new ProductBacklogHelper(project,	session);
//		Long issueID = helper.addStory(name, description,value,importance, estimation, howToDemo, notes);
//		// 將 Story 加入 Sprint
//		if (sprintID != null || sprintID.length() != 0) {
//			ArrayList<Long> list = new ArrayList<Long>();
//			list.add(issueID);
//			helper.add(list, sprintID);
//			
//			// 如果這個SprintID有Release資訊，那麼也將此Story加入Release
//			ReleasePlanHelper releaseHelper = new ReleasePlanHelper(project);
//			String releaseID = releaseHelper.getReleaseID(sprintID);
//			if (!(releaseID.equals("0")))
//				helper.addRelease(list, releaseID);
//		}
//		
//		// 如果這個SprintID有Release資訊，那麼也將此Story加入Release
//		ReleasePlanHelper releaseHelper = new ReleasePlanHelper(project);
//		String releaseID = releaseHelper.getReleaseID(sprintID);
//		if (!(releaseID.equals("0")))
//			helper.addRelease(list, releaseID);
//		
//		// 新增Tag至Story上面
//		String[] IDs = tagIDs.split(",");
//		if ( ! (tagIDs.isEmpty()) && IDs.length > 0) {
//			for (String tagId : IDs) {
//				helper.addStoryTag(Long.toString(issueID), tagId);
//			}
//		}
//		
//		IIssue issue = helper.getIssue(issueID);
//		
//		StringBuilder result = new StringBuilder("");
////		result.append(new Translation(mantisUrl).translateStoryToJson(issue));
//		result.append(new Translation().translateStoryToJson(issue));
	}
}
