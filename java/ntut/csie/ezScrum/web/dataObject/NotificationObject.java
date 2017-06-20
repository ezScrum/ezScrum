package ntut.csie.ezScrum.web.dataObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScru.web.microservice.ServiceConfiguration;

public class NotificationObject {
	private String sender = "";
	private String messageTitle = "";
	private String messageBody ="";
	private String fromURL = "";
	private long projectId;
	private String accToken;
	private ArrayList<Long> receiversId;
	private String NotificationURL;
		
	public NotificationObject(){
		receiversId = new ArrayList<Long>();
		setNotificationURL();
	}
	
	public NotificationObject(String sender,Long projectId, String messageTitle,String messageBody){
		this.sender = sender;
		this.projectId = projectId;
		this.messageTitle = messageTitle;
		this.messageBody = messageBody;
		receiversId = new ArrayList<Long>();
		setNotificationURL();
	}
	
	public void setSender(String sender){
		this.sender = sender;
	}
	
	public void setProjectId(long projectId){
		this.projectId = projectId;
	}
	
	public void setMessageTitle(String messageTitle){
		this.messageTitle = messageTitle;
	}

	public void setMessageBody(String messageBody){
		this.messageBody = messageBody;
	}

	public void setFromURL(String fromURL){
		this.fromURL = fromURL;
	}
	
	public void setReceiversId(ArrayList<Long> receiversId){
		this.receiversId.clear();
		for(long receiverId : receiversId){
			this.receiversId.add(receiverId);
		}
	}
	
	public void setAccToken(String accToken){
		this.accToken = accToken;
	}
	
	private void setNotificationURL(){
		try{
			ServiceConfiguration sc = new ServiceConfiguration("Notification");
			this.NotificationURL = "http://" + sc.getServiceUrl() + ":" + sc.getServicePort();
			System.out.println(this.NotificationURL);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public String send(){
		HttpURLConnection connection = null;
		try{
			JSONObject json = new JSONObject();
			json.put("sender", sender);
			json.put("receivers", new JSONArray(receiversId).toString());
			json.put("accToken", accToken);
			json.put("projectId", projectId);
			json.put("messageTitle", messageTitle);
			json.put("messageBody", messageBody);
			json.put("fromURL", fromURL);
			
			URL url = new URL(NotificationURL + "/notify/send");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true );
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type","application/json");
		    
	        OutputStream wr = connection.getOutputStream();
            wr.write(json.toString().getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            int HttpResult = connection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                sb.append(br.readLine());
                br.close();
                return "Send Message " + sb.toString();
            } else {
                return "Notification service not connect.";
            }
		}catch(Exception e){
			return "Send Message Fail.";
		}
	}
}
