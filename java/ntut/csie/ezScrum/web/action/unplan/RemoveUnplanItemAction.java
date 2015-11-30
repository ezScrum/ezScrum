package ntut.csie.ezScrum.web.action.unplan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.UnplanItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RemoveUnplanItemAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessUnplanItem();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		// get parameter info
		long unplanId = Long.parseLong(request.getParameter("issueID"));

		UnplanItemHelper unplanHelper = new UnplanItemHelper(project);
		unplanHelper.deleteUnplan(unplanId);

		StringBuilder result = new StringBuilder();
		result.append("<DeleteUnplanItem><Result>true</Result><UnplanItem><Id>")
		        .append(unplanId)
		        .append("</Id></UnplanItem></DeleteUnplanItem>");

		return result;
	}
}
