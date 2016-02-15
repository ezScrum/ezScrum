package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

public class AjaxGetHandlerListWithAllAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetHandlerListWithAllAction.class);
	
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
		log.info("Get Handler List With All in AjaxGetHandlerListWithAllAction.java");
		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager.getProject(request);
		
		ArrayList<AccountObject> members = project.getProjectMembers();
		ArrayList<String> memberUsernames = new ArrayList<>();
		for (AccountObject member : members) {
			memberUsernames.add(member.getUsername());
		}
		StringBuilder result = new StringBuilder();
		result.append("<Handlers><Result>success</Result>");
		
		result.append("<Handler>");
		result.append("<Name>ALL</Name>");
		result.append("</Handler>");
		
		for(int i = 1; i < memberUsernames.size(); i++) {
			result.append("<Handler>");
			result.append("<Name>").append(memberUsernames.get(i)).append("</Name>");
			result.append("</Handler>");
		}
		
		result.append("</Handlers>");
		
		return result;
	}
}
