package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SaveReleasePlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(SaveReleasePlanAction.class);
	
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
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		
		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();

		// get parameter info
		String releaseIdString = request.getParameter("Id");
		String name = translateSpecialChar.TranslateXMLChar(request.getParameter("Name"));
		String startDate = request.getParameter("StartDate");
		String dueDate = request.getParameter("EndDate");
		String description = translateSpecialChar.TranslateXMLChar(request.getParameter("Description"));
		String action = request.getParameter("action");
		if (action == null || action.isEmpty()) {
			return null;
		}	
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.name = name;
		releaseInfo.startDate = startDate;
		releaseInfo.dueDate = dueDate;
		releaseInfo.description = description;
		long releaseId = -1;
		if (action.equals("save")) {
			releasePlanHelper.createRelease(releaseInfo);
		} else if (action.equals("edit")) {
			if (releaseIdString != null) {
				releaseId = Long.parseLong(releaseIdString);
			}
			releaseInfo.id = releaseId;
			releasePlanHelper.editRelease(releaseInfo);
		}
		return new StringBuilder("true");
	}
}