package ntut.csie.ezScrum.web.action.unplanned;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RemoveUnplannedItemAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessUnplannedItem();
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
		long unplannedId = Long.parseLong(request.getParameter("issueID"));

		UnplannedItemHelper unplannedHelper = new UnplannedItemHelper(project);
		unplannedHelper.deleteUnplanned(unplannedId);

		StringBuilder result = new StringBuilder();
		result.append("<DeleteUnplannedItem><Result>true</Result><UnplannedItem><Id>")
		        .append(unplannedId)
		        .append("</Id></UnplannedItem></DeleteUnplannedItem>");

		return result;
	}
}
