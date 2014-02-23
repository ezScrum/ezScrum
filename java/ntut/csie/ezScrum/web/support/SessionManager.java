package ntut.csie.ezScrum.web.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.iternal.IProjectSummaryEnum;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.resource.core.IProject;

/**
 * @author franklin
 */
public class SessionManager {

	private HttpSession m_session = null;
	private final static String sessionAttributeNameForPermession = "Permession";

	public SessionManager(HttpServletRequest request) {
		m_session = request.getSession();
	}

	// IProjectSummaryEnum.PROJECT 和 IProjectSummaryEnum.PROJECT_INFO_FORM use in TaskBoardCardPanel.jsp
	/**
	 * 將project instance設定於session中
	 * 
	 * @param project
	 */
	public void setProject(IProject project) {
		m_session.removeAttribute(IProjectSummaryEnum.PROJECT);
		m_session.setAttribute(IProjectSummaryEnum.PROJECT, project);
	}

	/**
	 * 設定ProjectInfoForm的instance到session中
	 * 
	 * @param infoForm
	 */
	public void setProjectInfoForm(ProjectInfoForm infoForm) {
		m_session.removeAttribute(IProjectSummaryEnum.PROJECT_INFO_FORM);
		m_session.setAttribute(IProjectSummaryEnum.PROJECT_INFO_FORM, infoForm);
	}

	/**
	 * 從session中取得project info form的instance
	 * 
	 * @return
	 */
	// public ProjectInfoForm getProjectInfoForm(){
	// return (ProjectInfoForm) m_session.getAttribute(IProjectSummaryEnum.PROJECT_INFO_FORM);
	// }

	// /**
	// * 從session中取得project的instance
	// *
	// * @param projectName
	// * @return
	// */
	// public IProject getProject(){
	// return (IProject)m_session.getAttribute(IProjectSummaryEnum.PROJECT);
	// }

	/**
	 * 從session中取得project的instance 如果session中沒有的話，則從底層撈出project資料放置session cache起來
	 * 
	 * @param projectName
	 * @author Zam
	 * @param request client端傳上來的request
	 * @time 2012/8/28
	 */
	public static final IProject getProject(HttpServletRequest request) {
		HttpSession session = request.getSession();
		// 拿到request header的URL parameter
		String projectID = getURLParameter(request, "PID");
		
		if (projectID != null) {
			// 拿session裡的project資料
			IProject project = (IProject) session.getAttribute(projectID);
			/**
			 * 如果session拿不到project的資料，則往DB找
			 */
			if (project == null) {
				// project = ResourceFacade.getProject(projectID);
				project = (new ProjectMapper()).getProjectByID(projectID);
				if (project != null) {
					session.setAttribute(projectID, project);
				}
			}
			return project;
		}
		return null;
	}

	/**
	 * 傳入request與參數名稱，拿到request header的URL parameter
	 * 
	 * @param request client端傳上來的request
	 * @param paramName 要取得的參數名稱
	 * @author Zam
	 * @time 2012/8/28
	 */
	private static String getURLParameter(HttpServletRequest request, String paramName) {
		try {
			/**
			 * 拿到request header的URL parameter 如果拿不到referer資訊此時回傳null
			 */
			String referer = request.getHeader("Referer");
			if (referer == null) return null;
			// 將URL轉換成沒有萬用字元符號
			String url = URLDecoder.decode(referer, "UTF-8");

			/**
			 * 如果用 "?"拆除出來的params是null或只有一個urlParams 代表網址後面沒有帶參數 此時回傳null
			 */
			String[] urlParams = url.split("\\?");
			if (urlParams == null || urlParams.length <= 1) return null;

			// 取得URL 問號後面的Parameter參數
			url = urlParams[1];
			// params 裡面存的大概像這樣 [{id=15}, {op=55}]，為各個parameter
			String[] params = url.split("&");
			// value 用來接params的 parameter的key跟value ex: [id, 15]
			String[] value;

			/**
			 * 找到對應的parameter name並把value回傳給使用者
			 */
			for (int i = 0; i < params.length; i++) {
				value = params[i].split("=");
				if (value[0].equals(paramName)) {
					return value[1];
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return paramName;
	}

	/**
	 * 判斷Session是否有記錄該使用者對於專案操作的權限。 有的話則使用Session記錄即可，反之必須建立該使用者權限在Session， 以利於系統不必重複new ScrumRole instance，避免JVM out of memory。
	 * 
	 * @param request
	 * @param project
	 * @param account
	 * @author SPARK
	 * @return ScrumRole
	 */
	public static ScrumRole getScrumRole(HttpServletRequest request, IProject project, UserObject account) {
//		// printAllSessionAttribute(request);
//		String userID = account.getAccount();
//		HttpSession session = request.getSession();
//		String userPermessionNameForSession = userID + sessionAttributeNameForPermession;
//		Map<String, ScrumRole> scrumRolesMap = (Map<String, ScrumRole>) session.getAttribute(userPermessionNameForSession);
//		if (scrumRolesMap == null) {
//			// scrumRolesMap = new ScrumRoleManager().getScrumRoles(account);
//			scrumRolesMap = (new ScrumRoleLogic()).getScrumRoles(account);
//			session.setAttribute(userPermessionNameForSession, scrumRolesMap);
//		}
//		ScrumRole scrumRole = scrumRolesMap.get(project.getName());
//		return scrumRole;
		
		// ezScrum v1.8
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, account);
		return scrumRole;
	}

	/**
	 * 移除Session中User所有專案的權限記錄。 example:session.removeAttribute("UserName + Permession");
	 * 
	 * @param request
	 * @param account
	 * @author SPARK
	 */
	public static void removeScrumRolesMap(HttpServletRequest request, UserObject account) {
		String userPermessionNameForSession = account.getAccount() + sessionAttributeNameForPermession;

		List<HttpSession> sessionList = HttpSessionCollector.getSessionList(userPermessionNameForSession);

		for (HttpSession session : sessionList) {
			session.removeAttribute(userPermessionNameForSession);
		}
	}

	/**
	 * 確認登入帳號是否擁有admin權限
	 * 
	 * @author SPARK
	 * @param account
	 * @return
	 */
	private static boolean checkIsAdmin(IAccount account) {
		for (IRole role : account.getRoles()) {
			String roleName = role.getRoleName();
			String roleID = role.getRoleId();
			if (roleName.equals("administrator") && roleID.equals("admin")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 移除Session中User所有專案的權限記錄
	 * 
	 * @author SPARK
	 * @param allSessions
	 */
	private static void removeAdmineRole(Map<String, HttpSession> allSessions) {
		for (Entry<String, HttpSession> entry : allSessions.entrySet()) {
			HttpSession session = entry.getValue();
			IUserSession userSession = (IUserSession) session.getAttribute("UserSession");
			UserObject account = userSession.getAccount();
			HashMap<String, ProjectRole> roles = account.getRoles();
			ProjectRole role = roles.get("system");
			if (role != null) {
				ScrumRole scrumRole = role.getScrumRole();
				String roleName = scrumRole.getRoleName();
				if (roleName.equals("admin")) {
					String userPermessionNameForSession = account.getAccount() + sessionAttributeNameForPermession;
					session.removeAttribute(userPermessionNameForSession);
				}
			}
		}
	}
}
