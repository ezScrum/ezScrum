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

import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.iternal.MantisProjectManager;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.MantisAccountManager;
import ntut.csie.ezScrum.web.dataObject.ITSInformation;
import ntut.csie.ezScrum.web.dataObject.ProjectInformation;
import ntut.csie.ezScrum.web.dataObject.RoleEnum;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.sqlService.MySQLService;
import ntut.csie.jcis.account.core.IAccount;
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

	private ITSPrefsStorage mPrefs;
	private MySQLService mService;

	public ProjectMapper() {
		mPrefs = new ITSPrefsStorage();
		mService = new MySQLService(mPrefs);
	}

	/**
	 * new mapper function for ezScrum v1.8
	 */
	public ProjectInformation createProjectForDb(ProjectInformation project) {
		mService.openConnect();
		mService.createProject(project);
		project = mService.getProjectByPid(project.getName());
		mService.closeConnect();
		return project;
	}

	public boolean deleteProjectForDb(String id) {
		mService.openConnect();
		boolean result = mService.deleteProject(id);
		mService.closeConnect();
		return result;
	}

	public ProjectInformation updateProjectForDb(ProjectInformation project) {
		mService.openConnect();
		mService.updateProject(project);
		project = mService.getProjectById(project.getId());
		mService.closeConnect();
		return project;
	}

	public List<ProjectInformation> getProjectListForDb() {
		mService.openConnect();
		List<ProjectInformation> result = mService.getProjectList();
		mService.closeConnect();
		if (result == null) result = new ArrayList<ProjectInformation>();
		return result;
	}

	public ProjectInformation getProjectByIdForDb(String id) {
		mService.openConnect();
		ProjectInformation result = mService.getProjectById(id);
		mService.closeConnect();
		return result;
	}
	
	public ProjectInformation getProjectByPidForDb(String pid) {
		mService.openConnect();
		ProjectInformation result = mService.getProjectByPid(pid);
		mService.closeConnect();
		return result;
	}

	public List<UserObject> getProjectMemberListForDb(String id) {
		// TODO 待project role 完成
		mService.openConnect();
		List<UserObject> result = mService.getProjectMemberList(id);
		mService.closeConnect();
		return result;
	}

	public List<UserObject> getProjectScrumWorkerListForDb(String id) {
		// TODO 待project role 完成
		mService.openConnect();
		List<UserObject> result = mService.getProjectWorkerList(id);
		mService.closeConnect();
		return result;
	}

	public void createScrumRole(String id) {
		ScrumRole scrumRole;
		mService.openConnect();
		for (RoleEnum role : RoleEnum.values()) {
			scrumRole = new ScrumRole(role);
			mService.createScrumRole(id, role, scrumRole);
		}
		mService.closeConnect();
    }

	/**
	 * 建立專案的資料結構及外部檔案
	 * 
	 * @param userSession
	 * @param tmpPrefs
	 * @param ProjectInfoForm projectInfoForm
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public IProject createProject(IUserSession userSession, ITSInformation itsInformation, ProjectInfoForm projectInfoForm) throws Exception {
		ITSPrefsStorage tmpPrefs = this.setITSInformation(itsInformation);

		// save in the workspace，並且建立Project資料夾
		// 這樣後續的設定檔複製儲存動作才能正常進行
		IProject project = this.createProjectWorkspace(userSession, projectInfoForm, tmpPrefs);

		// 建立專案資訊 in database
		this.createProjectDB(tmpPrefs, project, userSession);

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
	private void createProjectDB(ITSPrefsStorage tmpPrefs, IProject project, IUserSession userSession) throws Exception {
		// 測試連線並且檢查DB內的Table是否正確
		MantisService mantisService = new MantisService(tmpPrefs);
		mantisService.TestConnect();

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
	private IProject createProjectWorkspace(IUserSession userSession, ProjectInfoForm saveProjectInfoForm, ITSPrefsStorage tmpPrefs) {
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
//		this.saveITSConfig(project, userSession, tmpPrefs); ezScrum v1.8 不需要
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
	 * 透過projectID取得Project information
	 * 
	 * @param projectID
	 * @return
	 */
	@Deprecated
	public IProject cloneProjectByID(String projectID) {
		IWorkspace workspace = ResourceFacade.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject project = root.cloneProject(projectID);
		return project;
	}

	/**
	 * 取的專案存於外部檔案的資料
	 * 
	 * @param project
	 * @return
	 */
	@Deprecated
	public ProjectInfoForm getProjectInfoForm(IProject project) {
		IProjectDescription desc = project.getProjectDesc();

		ProjectInfoForm form = new ProjectInfoForm();
		String fileSize = desc.getAttachFileSize();
		if (fileSize == null || fileSize.compareTo("") == 0)
			form.setAttachFileSize("2");
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
		// form.setCvsModuleName(cvs.getModuleName());
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
	 * 取得專案內的所有成員
	 * 
	 * @param userSession
	 * @param project
	 * @return
	 */
	@Deprecated
	public List<IAccount> getProjectMemberList(IUserSession userSession, IProject project) {
		MantisAccountManager mantisAccountManager = new MantisAccountManager(userSession);
		List<IAccount> projectMemberList = mantisAccountManager.getProjectMemberList(project);
		return projectMemberList;
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
		List<String> scrumWorkerList = mantisAccountManager.getScrumWorkerList(project);
		return scrumWorkerList;
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
	 * 檢查 check file path 檔案是否存在，
	 * 否則依據 clone file path 複製一份檔案過去
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
		if (fileSize.compareTo("") == 0)
			desc.setAttachFileSize("2");
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

//	/** ezScrum v1.8  不需要
//	 * 儲存ITS資訊
//	 * 
//	 * @param project
//	 * @param userSession
//	 * @param tmpPrefs
//	 */
//	@Deprecated
//	private void saveITSConfig(IProject project, IUserSession userSession, ITSPrefsStorage tmpPrefs) {
//		/*-----------------------------------------------------------
//		 *	寫入ITS的設定檔
//		-------------------------------------------------------------*/
//		ITSPrefsStorage prefs = new ITSPrefsStorage(project, userSession);
//		prefs.setServerUrl(tmpPrefs.getServerUrl());
//		prefs.setServicePath(tmpPrefs.getWebServicePath());
//		prefs.setDBAccount(tmpPrefs.getDBAccount());
//		prefs.setDBPassword(tmpPrefs.getDBPassword());
//		prefs.setDBType(tmpPrefs.getDBType());
//		prefs.setDBName(tmpPrefs.getDBName());
//		prefs.save();
//	}
	
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

	/**
	 * 設定ITS資訊
	 * 
	 * @param itsInformation
	 * @return
	 */
	@Deprecated
	private ITSPrefsStorage setITSInformation(ITSInformation itsInformation) {
		final String DEFAULT_ACCOUNT = "ezScrum";
		final String DEFAULT_PASSWORD = "";
		String projectName = itsInformation.getProjectName();
		String serverURL = itsInformation.getServerURL();
		String serverPath = itsInformation.getServerPath();
		String serverAcc = itsInformation.getDbAccount();
		String serverPwd = itsInformation.getDbPassword();
		String dbName = itsInformation.getDbName();
		String dbType = itsInformation.getDbType();

		IProject projectTemp = this.getProjectByID(projectName);

		// ProjectMapper projectMapper = new ProjectMapper();
		// IProject projectTemp = projectMapper.getProjectByID(projectName);

		// 設定ITS資訊
		ITSPrefsStorage tmpPrefs = new ITSPrefsStorage(projectTemp, null);
		tmpPrefs.setServerUrl(serverURL);
		tmpPrefs.setServicePath(serverPath);
		tmpPrefs.setDBAccount(serverAcc);
		tmpPrefs.setDBPassword(serverPwd);
		tmpPrefs.setDBName(dbName);

		/*-----------------------------------------------------------
		 *	設定使用的DB種類，如果是Default的話，那就預設是Local DB
		-------------------------------------------------------------*/
		if (dbType.contains("Default")) {
			tmpPrefs.setDBType("Default");
			tmpPrefs.setDBName(projectName);
			// 並且ServerUrl設成Project名稱
			tmpPrefs.setServerUrl(projectName);
			// 帳號密碼也用預設的
			tmpPrefs.setDBAccount(DEFAULT_ACCOUNT);
			tmpPrefs.setDBPassword(DEFAULT_PASSWORD);
		} else {
			tmpPrefs.setDBType(dbType);
		}

		return tmpPrefs;
	}
}
