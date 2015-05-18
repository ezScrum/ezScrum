package ntut.csie.ezScrum.web.action.backlog;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
//		MantisAccountMapper helper = new MantisAccountMapper(project, session);
//		List<String> actors = helper.getScrumWorkerList();
		IProject iProject = new ProjectMapper().getProjectByID(project.getName());
		List<String> actors = (new ProjectMapper()).getProjectScrumWorkerList(userSession, iProject);

		StringBuilder result = new StringBuilder();
		result.append("<Handlers><Result>success</Result>");
		
		result.append("<Handler>");
		result.append("<Name>ALL</Name>");
		result.append("</Handler>");
		
		for(int i = 1; i < actors.size(); i++) {
			result.append("<Handler>");
			result.append("<Name>").append(actors.get(i)).append("</Name>");
			result.append("</Handler>");
		}
		
		result.append("</Handlers>");
		
		return result;
	}
}
