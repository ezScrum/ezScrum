package ntut.csie.ezScrum.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

// this action is for top title user info
public class GetTopTitleInfoAction extends Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ProjectObject project = SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		
		String username = account.getUsername();
		String nickname = account.getNickName();
		String projectName = "";
		
		if (project != null) {
			projectName = project.getName();
		}		
		
		TopTitleInfoUI ttiui = new TopTitleInfoUI(username, nickname, projectName);
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
		private String Username = "";
		private String Nickname = "";
		private String ProjectName = "";
		private String NotificationStatus = "";
		public TopTitleInfoUI(String username, String nickname, String projectname) {
			this.Username = username;
			this.Nickname = nickname;
			this.ProjectName = projectname;
		}
	}

}
