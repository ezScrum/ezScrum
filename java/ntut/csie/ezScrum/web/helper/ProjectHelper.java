package ntut.csie.ezScrum.web.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
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

	public ProjectHelper() {
		mProjectMapper = new ProjectMapper();
	}

	public ProjectInfoForm getProjectInfoForm(IProject project) {
		return mProjectMapper.getProjectInfoForm(project);
	}

	public String getProjectListXML(UserObject account) {
		log.info(" handle project list xml format");

		// get all projects
		ProjectLogic projectLogic = new ProjectLogic();
		List<IProject> projects = projectLogic.getAllProjects();
		// ezScrum v1.8
		List<ProjectObject> projectsForDb = projectLogic.getAllProjectsForDb();
		
		// get the user and projects permission mapping
		Map<String, Boolean> map = projectLogic.getProjectPermissionMap(account);

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
		for (ProjectObject project : projectsForDb) {
			if (map.get(project.getName()) == Boolean.TRUE) {
				sb.append("<Project>");
				sb.append("<ID>").append(tsc.TranslateXMLChar(project.getName())).append("</ID>");
				sb.append("<Name>").append(tsc.TranslateXMLChar(project.getDisplayName())).append("</Name>");
				sb.append("<Comment>").append(tsc.TranslateXMLChar(project.getComment())).append("</Comment>");
				sb.append("<ProjectManager>").append(tsc.TranslateXMLChar(project.getManager())).append("</ProjectManager>");
				sb.append("<CreateDate>").append(dateFormat.format(project.getCreateDate())).append("</CreateDate>");
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
		ProjectMapper projectMapper = new ProjectMapper();
		return projectMapper.getProjectScrumWorkerList(userSession, project);
	}
	
	/**
	 * 回傳此專案的Scrum Team 內可以存取 TaskBoard 權限的人，代表可以領取工作者
	 * ezScrum v1.8
	 * 
	 * @param userSession
	 * @param project
	 */
	public List<UserObject> getProjectScrumWorkerListForDb(IUserSession userSession, ProjectObject project) {
		ProjectMapper projectMapper = new ProjectMapper();
		return projectMapper.getProjectScrumWorkerListForDb(project.getId());
	}

	public String getCreateProjectXML(HttpServletRequest request, IUserSession userSession, String fromPage, ProjectObject projectInformation) {
		StringBuilder sb = new StringBuilder();
		ProjectMapper projectMapper = new ProjectMapper();
		sb.append("<Root>");

		// create project
		if (fromPage != null) {
			if (fromPage.equals("createProject")) {
				sb.append("<CreateProjectResult>");

				IProject project = null;
				try {
					// 轉換格式
					ProjectInfoForm projectInfoForm = this.convertProjectInfo(projectInformation);

					project = projectMapper.createProject(userSession, projectInfoForm);

					// 重新設定權限, 當專案被建立時, 重新讀取此User的權限資訊
					SessionManager projectSessionManager = new SessionManager(request);
					projectSessionManager.setProject(project);
					AccessPermissionManager.setupPermission(request, userSession);

					// 建立專案角色和權限的外部檔案
					AccountMapper accountMapper = new AccountMapper();
					accountMapper.createPermission(project);
					accountMapper.createRole(project);
					
					// -- ezScrum v1.8 --
					projectInformation = projectMapper.createProjectForDb(projectInformation);
					projectMapper.createScrumRole(projectInformation.getId());
					
					sb.append("<Result>Success</Result>");
					sb.append("<ID>" + projectInformation.getName() + "</ID>");
					// -- ezScrum v1.8 --
				} catch (Exception e) {
					projectMapper.deleteProject(projectInformation.getName());
                    e.printStackTrace();
                }

				sb.append("</CreateProjectResult>");
			}
		}
		sb.append("</Root>");
		return sb.toString();
	}
	
	// ezScrum v1.8
	public List<UserObject> getProjectMemberList(ProjectObject project) {
		return mProjectMapper.getProjectMemberListForDb(project.getId());
	}

	private ProjectInfoForm convertProjectInfo(ProjectObject projectInformation) {
		String name = projectInformation.getName();
		String displayName = projectInformation.getDisplayName();
		String comment = projectInformation.getComment();
		String manager = projectInformation.getManager();
		String attachFileSize = projectInformation.getAttachFileSize();

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
		saveProjectInfoForm.setAttachFileSize(attachFileSize);
		// log info
		log.info("saveProjectInfoForm.getOutputPath()=" + saveProjectInfoForm.getOutputPath());
		log.info("saveProjectInfoForm.getSourcePaths().length=" + saveProjectInfoForm.getSourcePaths().length);

		return saveProjectInfoForm;
	}
	
	// ezScrum v1.8
	public ProjectObject updateProject(ProjectObject project) {
		return mProjectMapper.updateProjectForDb(project);
	}
}
