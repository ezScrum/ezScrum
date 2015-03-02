package ntut.csie.ezScrum.web.action.backlog;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetHandlerListAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetHandlerListAction.class);

	@Override
	public boolean isValidAction() {
		return true;	// 不知道怎麼分類，SprintBacklog、TaskBoard都會使用到
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Handler List in the Summary Page.");

		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		List<AccountObject> users = (new ProjectHelper()).getProjectScrumWorkersForDb(userSession, project);

		StringBuilder result = new StringBuilder();
		result.append("<Handlers><Result>success</Result>");

		for (int i = 0, size = users.size(); i < size; i++) {
			result.append("<Handler>")
			      .append("<Name>").append(users.get(i).getUsername()).append("</Name>")
			      .append("</Handler>");
		}
		result.append("</Handlers>");

		return result;
	}
}
