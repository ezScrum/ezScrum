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
		String subscriptNotify = "";
		
		if (project != null) {
			projectName = project.getName();
		}
		
		try{
			String firebaseToken = request.getParameter("firebaseToken");
			subscriptNotify = NotifyLogonToNotificationService(firebaseToken,username);
		}catch(JSONException e){
			subscriptNotify = "Error";
			e.printStackTrace();
		}
		
		
		TopTitleInfoUI ttiui = new TopTitleInfoUI(username, nickname, projectName, subscriptNotify);
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
		private String SubscriptNotify;
		public TopTitleInfoUI(String username, String nickname, String projectname, String subscriptNotify) {
			this.Username = username;
			this.Nickname = nickname;
			this.ProjectName = projectname;
			this.SubscriptNotify = subscriptNotify;
		}
	}
	
	private String NotifyLogonToNotificationService(String firebaseToken, String username) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("token", firebaseToken);
		HttpURLConnection connection = null;
		try{
			URL url = new URL("http://localhost:5000/notify/notifyLogon");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            OutputStream wr = connection.getOutputStream();
            wr.write(json.toString().getBytes("UTF-8"));
            wr.close();
            
            StringBuilder sb = new StringBuilder();
            int HttpResult = connection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                sb.append(br.readLine());                
                br.close();
                return sb.toString();
            } else {
            	throw new ConnectException("Connected fail");
            	}            
		}catch(Exception e){
			return "Error";
		}		
	}
}
