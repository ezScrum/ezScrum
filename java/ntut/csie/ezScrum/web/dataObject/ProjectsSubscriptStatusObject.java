package ntut.csie.ezScrum.web.dataObject;

import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.web.logic.ProjectLogic;

import java.util.ArrayList;
import java.util.Map;

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
	
	public void SetProjectsSubscriptStatusInit(AccountObject account){
		ProjectLogic mProjectLogic = new ProjectLogic();;
		ArrayList<ProjectObject> projects = mProjectLogic.getProjects();
		Map<String, Boolean> map = mProjectLogic.getProjectPermissionMap(account);
		
		try{
			JSONArray projectsStatus = new JSONArray();
			for(ProjectObject project : projects){
				if (map.get(project.getName()) == Boolean.TRUE){
					JSONObject projectStatus = new JSONObject();
					JSONObject eventStatus = new JSONObject();
					//TODO eventStatus
					projectStatus.put("Id", project.getName());
					projectStatus.put("Subscribe", true);
					projectStatus.put("event", eventStatus);
					projectsStatus.put(projectStatus);
				}	
			}
			SubscriptStatus.put("ezScrum", projectsStatus);
		}catch(JSONException e){
			System.out.println(e);
		}		
	}
	
	public void updateProjectStatus(String projectName, String statusType, boolean status){
			JSONObject ProjectStatus = GetProjectStatusByProjectName(projectName);
			SetProjectStatus(ProjectStatus,statusType,status);
	}
	
	public JSONObject GetProjectStatusByProjectName(String projectName){
		try{
			JSONArray projectsStatusArray = SubscriptStatus.getJSONArray("ezScrum");
			for(int index = 0; index < projectsStatusArray.length();index++){
				String id = projectsStatusArray.getJSONObject(index).getString("Id");
				if(id.equals(projectName))
					return projectsStatusArray.getJSONObject(index);
			}
		}catch(JSONException e){
			System.out.println(e);
		}		
		return null;
	}
	
	private void SetProjectStatus(JSONObject ProjectStatus, String type, boolean status){
		try{
			if(type.equals("Project")){
				ProjectStatus.remove("Subscribe");
				ProjectStatus.put("Subscribe",status);
			}
		}catch(JSONException e){
			System.out.println(e);
		}		
	}	
}
