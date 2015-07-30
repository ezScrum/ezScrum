package ntut.csie.ezScrum.web.action.plan;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowUnplannedItemAction extends PermissionAction {

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
		IUserSession session = (IUserSession) request.getSession()
				.getAttribute("UserSession");

		// get parameter info
		String sprintId = request.getParameter("SprintID");

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		UnplannedItemHelper unplannedItemHelper = new UnplannedItemHelper(
				project, session);

		SprintObject currentSprint = sprintPlanHelper.getCurrentSprint();

		// 如果沒有指定 sprint 的 id，則以目前的 sprint id 為準，如果也沒有的話則以最後一個 sprint id 為準
		if ((sprintId == null) || (sprintId.isEmpty())) {
			if (currentSprint != null) {
				sprintId = Long.toString(currentSprint.getId());
			}
		}

		try {
			return unplannedItemHelper.getListXML(sprintId);
		} catch (SQLException e) {
			return new StringBuilder("error");
		}
	}
}