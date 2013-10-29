package ntut.csie.ezScrum.restful.mobile.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.mobile.util.CommonUtil;
import ntut.csie.ezScrum.restful.mobile.util.ProjectUtil;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.resource.core.IProject;

public class ConvertIProject {
	private JSONObject projectList;	//	projectList
	private JSONArray projectArray;
	public ConvertIProject() throws JSONException {
		projectList = new JSONObject();
		projectArray = new JSONArray();
		projectList.put( ProjectUtil.TAG_PROJECTS, projectArray );
	}
	public void addProject(IProject p) throws JSONException{
		JSONObject project = new JSONObject();
		JSONObject projectProperties = new JSONObject();
		projectProperties.put(CommonUtil.TAG_ID, p.getProjectDesc().getName() );				//	id	
		projectProperties.put(CommonUtil.TAG_NAME, p.getProjectDesc().getDisplayName() );		//	name	
		projectProperties.put(ProjectUtil.TAG_COMMENT, p.getProjectDesc().getComment() );		//	comment		
		projectProperties.put(ProjectUtil.TAG_MANAGER, p.getProjectDesc().getProjectManager() );//	projectManager
		//	createDate
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String createDate = dateFormat.format( p.getProjectDesc().getCreateDate());
		projectProperties.put(ProjectUtil.TAG_CREATEDATE, createDate );	
		//	demoDate
		SprintPlanHelper SPhelper = new SprintPlanHelper(p);
		String demoDate = SPhelper.getNextDemoDate();
		if(demoDate == null) {
			projectProperties.put(ProjectUtil.TAG_DEMODATE, "No Plan!");
		} else {
			projectProperties.put(ProjectUtil.TAG_DEMODATE, demoDate);
		}
		project.put(ProjectUtil.TAG_PROJECT, projectProperties);
		projectArray.put(project);
	}
	public String getJSONString(){
		return projectList.toString();
	}

}
