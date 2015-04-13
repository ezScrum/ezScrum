package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

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
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get paramter info
		String filterType = request.getParameter("FilterType");		// 取得過濾的條件
		StringBuilder result = (new ProductBacklogHelper(project)).getShowProductBacklogResponseText(filterType);
		return result;
	}
}