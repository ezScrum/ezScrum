package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class CreateDatabaseAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String result = "{\"success\":true}";
		// user session
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		// 取得所有 ITS 參數資料
		String serverURL = request.getParameter("ServerUrl");
		String serverPath = request.getParameter("ServicePath");
		String serverAcc = request.getParameter("DBAccount");
		String serverPwd = request.getParameter("DBPassword");
		String projectName = request.getParameter("Name");
		String dbName = request.getParameter("DBName");

		// 取得空的專案資訊
//		IProject projectTemp = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
		IProject projectTemp = (new ProjectMapper()).getProjectByID(projectName);

		// 設定ITS資訊
//		ITSPrefsStorage tmpPrefs = new ITSPrefsStorage(projectTemp, null);
//		tmpPrefs.setServerUrl(serverURL);
//		tmpPrefs.setServicePath(serverPath);
//		tmpPrefs.setDBAccount(serverAcc);
//		tmpPrefs.setDBPassword(serverPwd);
//		tmpPrefs.setDBName(dbName);
		
		// 設定 ITS資訊
		Configuration tmpConfig = new Configuration(null);
		tmpConfig.setServerUrl(serverURL);
		tmpConfig.setWebServicePath(serverPath);
		tmpConfig.setDBAccount(serverAcc);
		tmpConfig.setDBPassword(serverPwd);
		tmpConfig.setDBName(dbName);

		MantisService service = new MantisService(tmpConfig);
		service.createDB();
		service.initiateDB();

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
