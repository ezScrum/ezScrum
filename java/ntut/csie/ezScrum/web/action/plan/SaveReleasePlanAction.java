package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

public class SaveReleasePlanAction extends PermissionAction {
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
		ProjectObject project = SessionManager.getProject(request);

		// get parameter info
		String serialReleaseIdString = request.getParameter("Id");
		String name = TranslateSpecialChar.TranslateXMLChar(request.getParameter("Name"));
		String startDate = request.getParameter("StartDate");
		String dueDate = request.getParameter("EndDate");
		String description = TranslateSpecialChar.TranslateXMLChar(request.getParameter("Description"));
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
		long serialReleaseId = -1;
		if (action.equals("save")) {
			releasePlanHelper.createRelease(releaseInfo);
		} else if (action.equals("edit")) {
			if (serialReleaseIdString != null) {
				serialReleaseId = Long.parseLong(serialReleaseIdString);
			}
			// Get release id
			long releaseId = -1;
			ReleaseObject release = ReleaseObject.get(project.getId(), serialReleaseId);
			if (release != null) {
				releaseId = release.getId();
			}
			releaseInfo.id = releaseId;
			releasePlanHelper.editRelease(releaseInfo);
		}
		return new StringBuilder("true");
	}
}