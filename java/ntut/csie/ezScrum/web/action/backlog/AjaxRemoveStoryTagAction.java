package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

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

import edu.emory.mathcs.backport.java.util.Arrays;

public class AjaxRemoveStoryTagAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxRemoveStoryTagAction.class);

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
		log.info("Remove Story Tag in AjaxRemoveStoryTagAction.");
		ProjectObject project = SessionManager.getProject(request);

		// get parameter info
		long storyId = Long.parseLong(request.getParameter("storyId"));
		
		long tagId;
		StringBuilder result;
		if (request.getParameterMap().containsKey("tagId")) {
			tagId = Long.parseLong(request.getParameter("tagId"));
			result = new ProductBacklogHelper(project)
					.getRemoveStoryTagResponseText(storyId, tagId);
			return result;
		}
		//System.out.println("RemoveTag = " + result);
		String[] tagsIdArray = request.getParameterValues("tagsId");
		ArrayList<Long> tagsId = new ArrayList<Long> ();
		for (int i = 0; i < tagsIdArray.length; i++){
			String temp = tagsIdArray[i];
			if(!temp.equals("")){
				tagsId.add(Long.parseLong(temp));
			}
		}
		result = new ProductBacklogHelper(project)
				.getRemoveStoryTagResponseText(storyId, tagsId);
		
		return result;
	}
}