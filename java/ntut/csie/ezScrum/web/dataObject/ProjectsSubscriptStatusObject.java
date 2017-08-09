package ntut.csie.ezScrum.web.dataObject;

import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class ProjectsSubscriptStatusObject {
	private JSONObject SubscriptStatus;
	
	public ProjectsSubscriptStatusObject(){
		SubscriptStatus = new JSONObject();
	}
	
	public String GetProjectsSubscriptStatus(){
		return SubscriptStatus.toString();
	}
	
	public void SetProjectsSubscriptStatus(String projectsSubscriptStatus){
		try{
			SubscriptStatus = new JSONObject(projectsSubscriptStatus);
		}catch(JSONException e){
			System.out.println(e);
		}
	}
	
	public String GetProjectStatusById(Long id){
		try{
			JSONArray projectsStatusArray = SubscriptStatus.getJSONArray("ezScrum");
			for(int index = 0; index < projectsStatusArray.length();index++){
				Long projectId = projectsStatusArray.getJSONObject(index).getLong("projectId");
				if(projectId == id)
					return projectsStatusArray.getJSONObject(index).toString();
			}
		}catch(JSONException e){
			System.out.println(e);
		}		
		return null;
	}
	
	public void SetProjectStatusById(Long id, String projectSubscriptStatus){
		try{
			JSONObject projectStatus = new JSONObject(projectSubscriptStatus);
			JSONArray projectsStatusArray = SubscriptStatus.getJSONArray("ezScrum");
			ArrayList<JSONObject> list = new ArrayList<JSONObject>();
			for(int index = 0; index < projectsStatusArray.length();index++){
				Long projectId = projectsStatusArray.getJSONObject(index).getLong("projectId");
				if(projectId == id){
					list.add(projectStatus);
				}
				else{
					list.add(projectsStatusArray.getJSONObject(index));
				}
			}
			SubscriptStatus.remove("ezScrum");
			SubscriptStatus.put("ezScrum", (new JSONArray(list)).toString());
		}catch(JSONException e){
			System.out.println(e);
		}		
	}	
}
