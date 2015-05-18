package ntut.csie.ezScrum.web.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.AccessPermissionManager;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectHelper {

	private static Log log = LogFactory.getLog(ProjectHelper.class);
	private ProjectMapper mProjectMapper;
	private ProjectLogic mProjectLogic;

	public ProjectHelper() {
		mProjectMapper = new ProjectMapper();
		mProjectLogic = new ProjectLogic();
	}

	public String getProjectListXML(AccountObject account) {
		log.info(" handle project list xml format");

		// get all projects
		List<IProject> projects = mProjectLogic.getAllProjects();
		// ezScrum v1.8
		ArrayList<ProjectObject> projectObjects = mProjectLogic.getProjects();
		
		// get the user and projects permission mapping
		Map<String, Boolean> map = mProjectLogic.getProjectPermissionMap(account);

		// get the demo date
		HashMap<String, String> hm = new HashMap<String, String>();
		for (IProject project : projects) {
			SprintPlanHelper SPhelper = new SprintPlanHelper(project);
			String demoDate = SPhelper.getNextDemoDate();
			if (demoDate == null) hm.put(project.getName(), "No Plan!");
			else hm.put(project.getName(), demoDate);
		}

		// ezScrum v1.8
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("<Projects>");
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		for (ProjectObject project : projectObjects) {
			if (map.get(project.getName()) == Boolean.TRUE) {
				sb.append("<Project>");
				sb.append("<ID>").append(tsc.TranslateXMLChar(project.getName())).append("</ID>");
				sb.append("<Name>").append(tsc.TranslateXMLChar(project.getDisplayName())).append("</Name>");
				sb.append("<Comment>").append(tsc.TranslateXMLChar(project.getComment())).append("</Comment>");
				sb.append("<ProjectManager>").append(tsc.TranslateXMLChar(project.getManager())).append("</ProjectManager>");
				sb.append("<CreateDate>").append(dateFormat.format(project.getCreateTime())).append("</CreateDate>");
				sb.append("<DemoDate>").append(hm.get(project.getName())).append("</DemoDate>");
				sb.append("</Project>");
			}
		}
		sb.append("</Projects>");

		return sb.toString();
	}

	/**
	 * 回傳此專案的Scrum Team 內可以存取 TaskBoard 權限的人，代表可以領取工作者
	 * 
	 * @param userSession
	 * @param project
	 */
	public List<String> getProjectScrumWorkerList(IUserSession userSession, IProject project) {
		return mProjectMapper.getProjectScrumWorkerList(userSession, project);
	}
	
	/**
	 * 回傳此專案的Scrum Team 內可以存取 TaskBoard 權限的人，代表可以領取工作者
	 * ezScrum v1.8
	 * 
	 * @param userSession
	 * @param project
	 */
	public List<AccountObject> getProjectScrumWorkersForDb(IUserSession userSession, ProjectObject project) {
		return mProjectMapper.getProjectWorkers(project.getId());
	}

	public String getCreateProjectXML(HttpServletRequest request,
			IUserSession userSession, String fromPage, ProjectInfo projectInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<Root>");

		// create project
		if (fromPage != null) {
			if (fromPage.equals("createProject")) {
				sb.append("<CreateProjectResult>");

				IProject iproject = null;
				ProjectObject project = null;
				try {
					// 轉換格式
					ProjectInfoForm projectInfoForm = convertProjectInfo(projectInfo);

					iproject = mProjectMapper.createProject(userSession, projectInfoForm);

					// 重新設定權限, 當專案被建立時, 重新讀取此 User 的權限資訊
					SessionManager sessionManager = new SessionManager(request);
					sessionManager.setProject(iproject);
					AccessPermissionManager.setupPermission(request, userSession);
					
					// -- ezScrum v1.8 --
					long projectId = mProjectMapper.createProject(projectInfo.name, projectInfo);
					project = ProjectObject.get(projectId);
					sessionManager.setProjectObject(request, project);
					
					sb.append("<Result>Success</Result>");
					sb.append("<ID>" + project.getName() + "</ID>");
					// -- ezScrum v1.8 --
				} catch (Exception e) {
					mProjectMapper.deleteProject(project.getId());
                    e.printStackTrace();
                }

				sb.append("</CreateProjectResult>");
			}
		}
		sb.append("</Root>");
		return sb.toString();
	}
	
	// ezScrum v1.8
	public ArrayList<AccountObject> getProjectMemberList(ProjectObject project) {
		return mProjectMapper.getProjectMembers(project.getId());
	}

	private ProjectInfoForm convertProjectInfo(ProjectInfo projectInfo) {
		String name = projectInfo.name;
		String displayName = projectInfo.displayName;
		String comment = projectInfo.common;
		String manager = projectInfo.manager;
		long attachFileSize = projectInfo.attachFileSize;

		ProjectInfoForm saveProjectInfoForm = new ProjectInfoForm();
		// 塞入假資料
		saveProjectInfoForm.setServerType("SVN");
		saveProjectInfoForm.setCvsConnectionType("pserver");
		saveProjectInfoForm.setSvnHook("Close");
		saveProjectInfoForm.setOutputPath("/");
		saveProjectInfoForm.setSourcePathString("/");
		// 塞入使用者輸入的資料
		saveProjectInfoForm.setName(name);
		saveProjectInfoForm.setDisplayName(displayName);
		saveProjectInfoForm.setComment(comment);
		saveProjectInfoForm.setProjectManager(manager);
		saveProjectInfoForm.setAttachFileSize(String.valueOf(attachFileSize));
		// log info
		log.info("saveProjectInfoForm.getOutputPath()=" + saveProjectInfoForm.getOutputPath());
		log.info("saveProjectInfoForm.getSourcePaths().length=" + saveProjectInfoForm.getSourcePaths().length);

		return saveProjectInfoForm;
	}
	
	/**
	 * Get project by id
	 * @param id
	 * @return project object
	 */
	public ProjectObject getProject(long id) {
		return mProjectMapper.getProject(id);
	}
	
	/**
	 * Get project by project name
	 * @param projectName
	 * @return project object
	 */
	public ProjectObject getProjectByName(String projectName) {
		return mProjectMapper.getProject(projectName);
	}
	
	/**
	 * Update project use DAO
	 * @param id 必要參數
	 * @param projectInfo 其他資訊都包成 Info
	 */
	public void updateProject(long id, ProjectInfo projectInfo) {
		mProjectMapper.updateProject(id, projectInfo);
	}
	
	public boolean isProjectExisted(String projectName) {
		return mProjectLogic.isProjectExisted(projectName);
	}
	
	public boolean isUserExistInProject(ProjectObject project, IUserSession userSession) {
		return mProjectLogic.isUserExistInProject(project, userSession);
	}
}
