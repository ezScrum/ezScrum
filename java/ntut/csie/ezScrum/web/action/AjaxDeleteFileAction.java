package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxDeleteFileAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxDeleteFileAction.class);
	
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
		log.info("Delete File.");
		
		// 取得刪除file前需要的資料
		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);

		// get parameter info
		long issueId = Integer.parseInt(request.getParameter("issueId"));
		long fileId = Long.parseLong(request.getParameter("fileId"));
		String issueType = request.getParameter("issueType");
		
		StringBuilder result = new StringBuilder("{\"success\":false}");
		
		// 透過file的 id 刪除attach file
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(project);
		PBHelper.deleteAttachFile(fileId);
		
		// 如果是在CustomIssue的頁面attach file的話，則translate custom issue的json
		if (issueType.equals("Story")){
			StoryObject story = PBHelper.getStory(issueId);
			result = new StringBuilder(Translation.translateStoryToJson(story));
		} else if (issueType.equals("Task")) {
			TaskObject task = TaskObject.get(issueId);
			result = new StringBuilder(Translation.translateTaskToJson(task));
		}
		return result;
	}
}