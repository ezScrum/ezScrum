package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

// this action is for top title user info
public class GetTopTitleInfoAction extends Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		
		String userName = account.getUsername();
		String nickName = account.getNickName();
		String userInfo = userName + "(" + nickName + ")";
		String projectName = "";
		
		if (project != null) {
			projectName = project.getName();
		}
		
		TopTitleInfoUI ttiui = new TopTitleInfoUI(userInfo, projectName);
		Gson gson = new Gson();
		
		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().write(gson.toJson(ttiui));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class TopTitleInfoUI {
		private String UserName = "";
		private String ProjectName = "";
		
		public TopTitleInfoUI(String username, String projectname) {
			this.UserName = username;
			this.ProjectName = projectname;
		}
	}
}
