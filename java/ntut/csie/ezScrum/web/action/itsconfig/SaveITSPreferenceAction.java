package ntut.csie.ezScrum.web.action.itsconfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SaveITSPreferenceAction extends PermissionAction {
    private static Log log = LogFactory.getLog(SaveITSPreferenceAction.class);
    
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getEditProject();
	}

	@Override
	public boolean isXML() {
		// hteml
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		//IProject project = (IProject) request.getSession().getAttribute("Project");
    	IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		ITSPrefsStorage prefs = new ITSPrefsStorage(project,session);
		
		String ServerUrl = request.getParameter("ServerUrl");
//		String ServicePath = request.getParameter("ServicePath");	// 不需要此參數
		String Account = request.getParameter("ITSAccount");
		String Password = request.getParameter("ITSPassword");

		prefs.setServerUrl(ServerUrl);
//		prefs.setServicePath(ServicePath);
		prefs.setDBAccount(Account);
		prefs.setDBPassword(Password);
		prefs.save();
		
		return new StringBuilder("success");
    }
}
