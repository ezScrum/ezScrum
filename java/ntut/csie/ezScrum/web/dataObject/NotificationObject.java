package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class NotificationObject {
	private String title;
	private String messageBody;
	private String eventSource;
	private ArrayList<Long> recipients_id;
	private JSONObject messageFilter = new JSONObject();
	
	public NotificationObject(String title, String messageBody, String eventSource){
		this.title = title;
		this.messageBody = messageBody;
		this.eventSource = eventSource;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getMessageBody(){
		return messageBody;
	}
	
	public void setMessageBody(String messageBody){
		this.messageBody = messageBody;
	}
	
	public String getEventSource(){
		return eventSource;
	}
	
	public void setEventSource(String eventSource){
		this.eventSource = eventSource;
	}
	
	public ArrayList<Long> getRecipientsId(){
		return recipients_id;
	}
	
	public void setRecipientsId(ArrayList<Long> recipients_id){
		this.recipients_id = recipients_id;
	}
	
	public JSONObject getMessageFilter(){
		return messageFilter;
	}
	
	public void addMessageFilter(String key, String value){
		try{
			messageFilter.put(key, value);
		}catch(JSONException e){
			
		}		
	}
	
	public void addMessageFilter(String key, Long value){
		try{
			messageFilter.put(key, value);
		}catch(JSONException e){
			
		}		
	}
}
