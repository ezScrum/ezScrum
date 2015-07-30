package ntut.csie.ezScrum.web.action.retrospective;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.RetrospectiveHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowRetrospectiveAction extends PermissionAction {
	// private static Log log =
	// LogFactory.getLog(ShowRetrospectiveAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessRetrospective();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession()
				.getAttribute("UserSession");

		// ger parameter info
		String sprintId = request.getParameter("sprintID");

		// check sprintID
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		SprintObject currentSprint = sprintPlanHelper.getCurrentSprint();

		// 如果沒有指定 sprint 的 ID，則以目前的 sprint ID 為準，如果也沒有的話則以最後一個 sprint id為準
		if (sprintId == null || sprintId.isEmpty()) {
			if (currentSprint != null) {
				sprintId = Long.toString(currentSprint.getId());
			}
		}

		try {
			return (new RetrospectiveHelper(project, session))
					.getListXML(sprintId);
		} catch (SQLException e) {
			return new StringBuilder("error");
		}
	}
}