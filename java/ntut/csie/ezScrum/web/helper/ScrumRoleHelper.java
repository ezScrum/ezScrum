package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;
import ntut.csie.ezScrum.web.support.AccessPermissionUI;

import com.google.gson.Gson;

public class ScrumRoleHelper {

	public String getResourceList() {
		ProjectLogic projectLogic = new ProjectLogic();
		ArrayList<ProjectObject> projects = projectLogic.getProjects();

		List<ProjectNameUI> pnui_list = new LinkedList<ProjectNameUI>();
		for (ProjectObject project : projects) {
			pnui_list.add(new ProjectNameUI(project));
		}

		return new Gson().toJson(pnui_list);
	}

	private class ProjectNameUI {
		private String id = "";
		private String text = "";
		private boolean leaf = true;
		private String cls = "treepanel-leaf";
		private String iconCls = "leaf-icon";

		public ProjectNameUI(ProjectObject project) {
			this.id = String.valueOf(project.getId());
			this.text = project.getName();
		}
	}

	public String getScrumRolePermissionList(long projectId, String projectName, String scrumRole) {
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectObject project = projectMapper.getProject(projectId);

		Gson gson = new Gson();
		if (project != null) {
			ScrumRole scrumrole = new ScrumRoleMapper().getScrumRole(projectId, projectName, scrumRole);
			return gson.toJson(new AccessPermissionUI(scrumrole));
		} else {
			return gson.toJson(new AccessPermissionUI(null));
		}
	}

	public void updateScrumRolePermission(String id, String projectId, String scrumRole, String permissionList) throws Exception {
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
			srm.updateScrumRole(Long.parseLong(id), scrumrole);
		}
	}

}
