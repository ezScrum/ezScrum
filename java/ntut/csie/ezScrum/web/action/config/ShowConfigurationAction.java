package ntut.csie.ezScrum.web.action.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;

public class ShowConfigurationAction extends PermissionAction{

	@Override
    public boolean isValidAction() {
	    return super.getScrumRole().getEditProject();
    }

	@Override
    public boolean isXML() {
	    return false;
    }

	@Override
    public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	
    	Configuration configuration = new Configuration(session);
    	ConfigUI configUi = new ConfigUI(configuration);
	    	
    	Gson gson = new Gson();
		return new StringBuilder(gson.toJson(configUi));
    }
	
	private class ConfigUI {
		private String ID = "0";
		private String ServerUrl = "";
		private String DBAccount = "";
		private String DBType = "";
		private String DBName = "";
		
		public ConfigUI(Configuration config) {
			ServerUrl = config.getServerUrl();
			DBAccount = config.getDBAccount();
			DBType = config.getDBType();
			DBName = config.getDBName();
		}
	}

}
