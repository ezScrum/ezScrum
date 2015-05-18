package ntut.csie.ezScrum.web.action.export;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetReleasePlanAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);

		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		List<IReleasePlanDesc> releaseDescs = RPhelper.loadReleasePlansList();
		StringBuilder result = new StringBuilder(RPhelper.setReleaseListToJSon(releaseDescs));
		return result;
	}
}
