package ntut.csie.ezScrum.web.helper;

import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;
import ntut.csie.ezScrum.web.support.AccessPermissionUI;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;

public class ScrumRoleHelper {

	public String getResourceList()
	{
		ProjectLogic projectLogic = new ProjectLogic();
		List<IProject> projects = projectLogic.getAllProjects();
		
		List<ProjectNameUI> pnui_list = new LinkedList<ProjectNameUI>();
		for (IProject p : projects) {
			pnui_list.add(new ProjectNameUI(p));
		}
		
		Gson gson = new Gson();		
		return gson.toJson(pnui_list);
	}
		
	private class ProjectNameUI {
		private String text = "";
		private boolean leaf = true;
		private String cls = "treepanel-leaf";
    	private String iconCls = "leaf-icon";
		
		public ProjectNameUI(IProject project) {
			this.text = project.getName();
		}
	}	
	
	public String getScrumRolePermissionList(String projectId, String scrumRole) {
		ProjectMapper projectMapper = new ProjectMapper();
		IProject project = projectMapper.getProjectByID(projectId);
		
		Gson gson = new Gson();
		String result = "";
		if (project.exists()) {
			ScrumRoleMapper srm = new ScrumRoleMapper();
			ScrumRole scrumrole = srm.getPermission(projectId, scrumRole);
			
			result = gson.toJson(new AccessPermissionUI(scrumrole));
		} else {
			result = gson.toJson(new AccessPermissionUI(null));
		}		
		return result;
	}	
	
	public void updateScrumRolePermission(String projectId, String scrumRole, String permissionList) throws Exception {
		Gson gson = new Gson();
		AccessPermissionUI permissionUI = gson.fromJson(permissionList, AccessPermissionUI.class);
		
		ScrumRole scrumrole = new ScrumRole(projectId, scrumRole);
		
		if (scrumrole != null) {
			scrumrole.setAccessProductBacklog(permissionUI.AccessProductBacklog);
			scrumrole.setAccessReleasePlan(permissionUI.AccessReleasePlan);
			scrumrole.setAccessSprintPlan(permissionUI.AccessSprintPlan);
			scrumrole.setAccessSprintBacklog(permissionUI.AccessSprintBacklog);
			scrumrole.setAccessTaskBoard(permissionUI.AccessTaskboard);
			scrumrole.setAccessUnplannedItem(permissionUI.AccessUnplanned);
			scrumrole.setAccessRetrospective(permissionUI.AccessRetrospective);
			scrumrole.setReadReport(permissionUI.AccessReport);
			scrumrole.setEditProject(permissionUI.AccessEditProject);			
			
			ScrumRoleMapper srm = new ScrumRoleMapper();
			srm.update(scrumrole);
		}		
	}	
	
}
