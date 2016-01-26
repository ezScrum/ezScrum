package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IWorkspace;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;
import ntut.csie.jcis.resource.core.ResourceFacade;

public class ProjectMapper {
	public ProjectMapper() {
	}

	/**
	 * @param name projectName
	 * @param projectInfo 其他資訊都包成 Info
	 * @return projectId
	 */
	public long createProject(String name, ProjectInfo projectInfo) {
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(projectInfo.displayName)
			.setComment(projectInfo.common)
			.setManager(projectInfo.manager)
			.setAttachFileSize(projectInfo.attachFileSize)
			.save();
		return project.getId();
	}
	
	/**
	 * Get project by id
	 * 
	 * @param id projectId
	 * @return ProjectObject
	 */
	public ProjectObject getProject(long id) {
		return ProjectObject.get(id);
	}
	
	/**
	 * Get project by name
	 * 
	 * @param name projectName
	 * @return ProjectObject
	 */
	public ProjectObject getProject(String name) {
		return ProjectObject.get(name);
	}
	
	/**
	 * Get all projects
	 * 
	 * @return ProjectObject list
	 */
	public ArrayList<ProjectObject> getAllProjects() {
		return ProjectObject.getAllProjects();
	}
	
	/**
	 * @param id projectId
	 * @param projectInfo 其他資訊都包成 Info
	 */
	public void updateProject(long id, ProjectInfo projectInfo) {
		ProjectObject project = ProjectObject.get(id);
		if(project != null) {
			project.setDisplayName(projectInfo.displayName).setComment(projectInfo.common)
			.setManager(projectInfo.manager).setAttachFileSize(projectInfo.attachFileSize)
			.save();
		}
	}
	
	/**
	 * Delete project by id
	 * 
	 * @param id projectId
	 */
	public void deleteProject(long id) {
		ProjectObject project = ProjectObject.get(id);
		project.delete();
	}

	/**
	 * Get all members in project include enable and disable members
	 * 
	 * @param projectId
	 * @return AccountObject list
	 */
	public ArrayList<AccountObject> getProjectMembers(long projectId) {
		ProjectObject project = ProjectObject.get(projectId);
		return project.getProjectMembers();
	}

	/**
	 * Get members in project only enable members
	 * 
	 * @param projectId
	 * @return AccountObject list
	 */
	public static ArrayList<AccountObject> getProjectWorkers(long projectId) {
		return ProjectObject.get(projectId).getProjectWorkers();
	}

	public ArrayList<String> getProjectWorkersUsername(long projectId) {
		ArrayList<AccountObject> projectWorkers = getProjectWorkers(projectId);
		ArrayList<String> projectWorkersUsername = new ArrayList<String>();
		for (AccountObject projectWorker : projectWorkers) {
			projectWorkersUsername.add(projectWorker.getUsername());
		}
		return projectWorkersUsername;
	}

	/**
	 * 透過projectID取得Project information
	 * 
	 * @param projectID
	 * @return
	 */
	@Deprecated
	public IProject getProjectByID(String projectID) {
		IWorkspace workspace = ResourceFacade.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject project = root.getProject(projectID);
		return project;
	}
}
