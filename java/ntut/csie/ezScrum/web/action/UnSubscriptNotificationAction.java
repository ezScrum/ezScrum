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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class UnSubscriptNotificationAction extends Action{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		String username = account.getUsername();
		String firebaseToken = request.getParameter("firebaseToken");
		
		String s = UnSubscript(username, firebaseToken);
		response.setContentType("text/html; charset=utf-8");
		
		try {
			response.getWriter().write(s);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String UnSubscript(String username, String firebaseToken){
		HttpURLConnection connection = null;
		try{
			JSONObject json = new JSONObject();
			json.put("username", username);
			json.put("token", firebaseToken);
			
			URL url = new URL("http://localhost:5000/notify/unSubscript");
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
			e.printStackTrace();
			return "Error";
		}
	}
}
