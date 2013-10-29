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

import com.google.gson.Gson;

public class ShowITSPreferenceAction extends PermissionAction {
    private static Log log = LogFactory.getLog(ShowITSPreferenceAction.class);
    
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getEditProject();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
    	//IProject project = (IProject) request.getSession().getAttribute("Project");
    	IProject project = (IProject) SessionManager.getProject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	ITSPrefsStorage prefs = new ITSPrefsStorage(project, session);
	    	
    	ITSConfigUI itsui = new ITSConfigUI(prefs);
	    	
    	Gson gson = new Gson();
		return new StringBuilder(gson.toJson(itsui));
    }
	
	private class ITSConfigUI {
		private String ID = "0";
		private String ServerUrl = "";
		private String ITSAccount = "";
//		private String ITSPassword = "";	// 不顯示密碼
		
		public ITSConfigUI(ITSPrefsStorage prefs) {
			this.ServerUrl = prefs.getPerfsMap().get("ServerUrl");
			this.ITSAccount = prefs.getPerfsMap().get("Account");
//			this.ITSPassword = prefs.getPerfsMap().get("Password");
		}
	}
}