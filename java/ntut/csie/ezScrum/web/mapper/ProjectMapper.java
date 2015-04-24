package ntut.csie.ezScrum.web.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.iternal.MantisProjectManager;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.MantisAccountManager;
import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.jcis.project.core.ICVS;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IResource;
import ntut.csie.jcis.resource.core.IWorkspace;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;
import ntut.csie.jcis.resource.core.ResourceFacade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectMapper {
	private static Log log = LogFactory.getLog(ProjectMapper.class);

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
		project.reload();
		
		// 新建 project，也把 serial number 建起來
		long projectId = project.getId();
		SerialNumberDAO serialnumberDAO = SerialNumberDAO.getInstance();
		serialnumberDAO.create(new SerialNumberObject(projectId
				, 0, 0, 0, 0, 0, 0));
				
		return projectId;
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
		return ProjectObject.get(projectId).getProjectMembers();
	}

	/**
	 * Get members in project only enable members
	 * 
	 * @param projectId
	 * @return AccountObject list
	 */
	public ArrayList<AccountObject> getProjectWorkers(long projectId) {
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
	 * 建立專案的資料結構及外部檔案
	 * 
	 * @param userSession
	 * @param tmpPrefs
	 * @param ProjectInfoForm
	 *            projectInfoForm
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public IProject createProject(IUserSession userSession, ProjectInfoForm projectInfoForm) throws Exception {
		Configuration config = new Configuration();
		// save in the workspace，並且建立Project資料夾
		// 這樣後續的設定檔複製儲存動作才能正常進行
		IProject project = createProjectWorkspace(userSession, projectInfoForm, config);

		// 建立專案資訊 in database
		createProjectDB(config, project, userSession);

		return project;
	}

	/**
	 * 建立資料庫中有關專案的資訊
	 * 
	 * @param tmpPrefs
	 * @param project
	 * @param userSession
	 * @throws Exception
	 */
	@Deprecated
	private void createProjectDB(Configuration tmpConfig, IProject project, IUserSession userSession) throws Exception {
		// 測試連線並且檢查DB內的Table是否正確
		MantisService mantisService = new MantisService(tmpConfig);
		mantisService.testConnect();

		// Create Project in Mantis 因為確定 ITS 資料正確，所以不用再對
		// createProject 做一次確認
		MantisProjectManager pm = new MantisProjectManager(project, userSession);
		pm.CreateProject(project.getName());
	}

	/**
	 * 建立外部檔案有關於專案的資訊
	 * 
	 * @param userSession
	 * @param saveProjectInfoForm
	 * @param tmpPrefs
	 * @return
	 */
	@Deprecated
	private IProject createProjectWorkspace(IUserSession userSession, ProjectInfoForm saveProjectInfoForm, Configuration tmpConfig) {
		IProject project = null;
		try {
			log.info("Save Project Info!");
			project = saveProjectInformation(saveProjectInfoForm);
			if (!project.exists()) {
				project.create();
			}
		} catch (Exception e) {
			log.warn("Save Project Error!" + e.getMessage());
		}
		return project;
	}

	/**
	 * 刪除專案所在的資料夾
	 * 
	 * @param projectID
	 */
	@Deprecated
	public void deleteProject(String projectID) {
		IProject project = this.getProjectByID(projectID);
		try {
			project.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新專案資訊
	 * 
	 * @param saveProjectInfoForm
	 * @return
	 */
	@Deprecated
	public IProject updateProject(ProjectInfoForm saveProjectInfoForm) {
		IProject project = saveProjectInformation(saveProjectInfoForm);
		if (project.exists()) {
			project.save();
		}
		return project;
	}

	/**
	 * 取得所有專案列表
	 * 
	 * @return
	 */
	@Deprecated
	public List<IProject> getAllProjectList() {
		IWorkspace workspace = ResourceFacade.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		root.refreshLocal(IResource.DEPTH_ONE);

		IProject[] projectArray = root.getProjects();
		List<IProject> list = Arrays.asList(projectArray);
		return list;
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

	/**
	 * 取的專案存於外部檔案的資料
	 * 
	 * @param project
	 * @return
	 */
	@Deprecated
	public ProjectInfoForm getProjectInfoForm(ProjectObject project) {
		IProject iProject = getProjectByID(project.getName());
		IProjectDescription desc = iProject.getProjectDesc();

		ProjectInfoForm form = new ProjectInfoForm();
		String fileSize = desc.getAttachFileSize();
		if (fileSize == null || fileSize.compareTo("") == 0) form.setAttachFileSize("2");
		else form.setAttachFileSize(desc.getAttachFileSize());
		form.setName(desc.getName());
		form.setDisplayName(desc.getDisplayName());
		form.setComment(desc.getComment());
		form.setCreateDate(desc.getCreateDate());
		form.setProjectManager(desc.getProjectManager());
		form.setOutputPath(desc.getOutput().getPathString());
		form.setProjectManager(desc.getProjectManager());
		form.setState(desc.getState());
		form.setVersion(desc.getVersion());

		ICVS cvs = desc.getCVS();
		form.setServerType(cvs.getServerType());
		form.setCvsConnectionType(cvs.getConnectionType());
		form.setCvsHost(cvs.getHost());
		form.setCvsUserID(cvs.getUserID());
		form.setCvsRepositoryPath(cvs.getRepositoryPath() + "/" + cvs.getModuleName());
		form.setCvsPassword(cvs.getPassword());
		form.setSvnHook(cvs.getSvnHook());

		log.info("Version:" + form.getVersion());

		form.setSourcePaths(getSourceStrings(desc.getSrc()));

		log.info("External Library length:"
		        + desc.getExternalReferences().length);

		log.info("Proejct Reference length:"
		        + desc.getProjectReferences().length);
		log.info("Source Path length:" + desc.getProjectReferences().length);

		return form;
	}

	/**
	 * 回傳此專案的Scrum Team 內可以存取 TaskBoard 權限的人，代表可以領取工作者
	 * 
	 * @param userSession
	 * @param project
	 */
	@Deprecated
	public List<String> getProjectScrumWorkerList(IUserSession userSession, IProject project) {
		MantisAccountManager mantisAccountManager = new MantisAccountManager(userSession);
		List<String> scrumWorkers = mantisAccountManager.getScrumWorkers(project);
		return scrumWorkers;
	}

	@Deprecated
	private static String[] getSourceStrings(IPath[] paths) {
		String[] sourceArray = new String[paths.length];

		for (int i = 0; i < paths.length; i++) {
			sourceArray[i] = paths[i].getPathString();
		}

		return sourceArray;
	}

	/**
	 * 檢查 check file path 檔案是否存在， 否則依據 clone file path 複製一份檔案過去
	 */
	@Deprecated
	public void checkAndClone(String checkfilepath, String clonefilepath) throws IOException {
		File checkfile = new File(checkfilepath);

		if (checkfile.exists()) {
			return;
		} else {
			// clone it
			InputStream in = new FileInputStream(clonefilepath);
			OutputStream out = new FileOutputStream(checkfilepath);

			byte[] buf = new byte[1024];		// buffer
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

			log.info("clonefile path = " + clonefilepath + " copy to " + checkfilepath);
		}
	}

	/**
	 * 儲存專案資訊
	 * 
	 * @param form
	 * @return
	 */
	@Deprecated
	private IProject saveProjectInformation(ProjectInfoForm form) {
		IProject project = this.getProjectByID(form.getName());
		IProjectDescription desc = project.getProjectDesc();
		desc.setComment(form.getComment());
		desc.setProjectManager(form.getProjectManager());
		desc.setDisplayName(form.getDisplayName());
		desc.setVersion(form.getVersion());
		desc.setState(form.getState());
		desc.setOutput(convertStringToOutPath(form.getOutputPath()));
		desc.setSrc(convertStringToSourcePath(form.getSourcePaths()));
		String fileSize = form.getAttachFileSize();
		// 如果fileSize沒有填值的話，則自動填入2
		if (fileSize.compareTo("") == 0) desc.setAttachFileSize("2");
		else desc.setAttachFileSize(form.getAttachFileSize());
		ICVS cvs = desc.getCVS();
		cvs.setServerType(form.getServerType());
		cvs.setConnectionType(form.getCvsConnectionType());
		cvs.setHost(form.getCvsHost());
		cvs.setPassword(form.getCvsPassword());

		cvs.setUserID(form.getCvsUserID());
		cvs.setSvnHook(form.getSvnHook());
		// 處理RepositoryPath
		String repositoryPath = form.getCvsRepositoryPath();
		String[] pathList = repositoryPath.split("/");
		repositoryPath = "";
		for (int i = 0; i < pathList.length; i++) {
			if (i == pathList.length - 1) {
				cvs.setModuleName(pathList[i]);
			} else if (!pathList[i].isEmpty()) {
				repositoryPath += "/" + pathList[i];
			}
		}
		cvs.setRepositoryPath(repositoryPath);
		return project;
	}

	@Deprecated
	private IPath[] convertStringToSourcePath(String[] sourcePathArray) {
		IPath[] sourcePaths = new IPath[sourcePathArray.length];

		for (int i = 0; i < sourcePathArray.length; i++) {
			if (sourcePathArray[i].charAt(0) != '\\'
			        && sourcePathArray[i].charAt(0) != '/') {
				sourcePathArray[i] = File.separatorChar + sourcePathArray[i];
			}
			sourcePaths[i] = ResourceFacade.createPath(sourcePathArray[i]);
		}

		return sourcePaths;
	}

	@Deprecated
	private IPath convertStringToOutPath(String outPath) {

		if (outPath.charAt(0) != '\\' && outPath.charAt(0) != '/') {
			outPath = File.separatorChar + outPath;
		}

		return ResourceFacade.createPath(outPath);
	}
}
