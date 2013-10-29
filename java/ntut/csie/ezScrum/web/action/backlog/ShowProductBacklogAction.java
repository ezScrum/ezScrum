package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowProductBacklogAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowProductBacklogAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.debug("Show Product Backlog in ShowProductBacklogAction.");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get paramter info
		String filterType = request.getParameter("FilterType");		// 取得過濾的條件
		
		StringBuilder result = (new ProductBacklogHelper(userSession, project)).getShowProductBacklogResponseText(filterType);
    	
		return result;
		
//    	ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, userSession);
//		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(filterType, pbHelper.getStories());
//    	IStory[] stories = filter.getStories();						// 回傳過濾後的 Stories
//
//		ITSPrefsStorage prefs = new ITSPrefsStorage(project, session);
//		String mantisUrl = prefs.getServerUrl();
//		StringBuilder result = new StringBuilder("");
//		result.append(new Translation(mantisUrl).translateStoryToJson(stories));
//		return result;
	}
}