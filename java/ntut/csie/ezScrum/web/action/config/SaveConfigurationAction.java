package ntut.csie.ezScrum.web.action.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;

public class SaveConfigurationAction extends PermissionAction{

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
		Configuration config = new Configuration(session);
		
		String ServerUrl = request.getParameter("ServerUrl");
		String Account = request.getParameter("DBAccount");
		String Password = request.getParameter("DBPassword");
		String DatabaseType = request.getParameter("DBType");
		String DatabaseName = request.getParameter("DBName");
		
		config.setServerUrl(ServerUrl);
		config.setDBAccount(Account);
		config.setDBPassword(Password);
		config.setDBType(DatabaseType);
		config.setDBName(DatabaseName);
		config.save();
		
		return new StringBuilder("success");
    }

}
