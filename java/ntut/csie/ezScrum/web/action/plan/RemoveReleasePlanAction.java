package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

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
		ProjectObject project = SessionManager.getProjectObject(request);

		// get parameter info
		String releaseIdString = request.getParameter("releaseID");
		long releaseId = -1;
		try {
			releaseId = Long.parseLong(releaseIdString);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		ReleaseObject release = releasePlanHelper.getReleasePlan(releaseId);

		if (release == null) {
			return new StringBuilder("false");
		} else {
			// 刪除Release
			releasePlanHelper.deleteReleasePlan(releaseId);

			return new StringBuilder("true");
		}
	}
}
