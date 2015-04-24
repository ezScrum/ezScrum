package ntut.csie.ezScrum.web.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.iternal.IProjectSummaryEnum;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONException;

/**
 * @author franklin
 */
public class SessionManager {

	private HttpSession mSession = null;
	private final static String sessionAttributeNameForPermession = "Permession";

	public SessionManager(HttpServletRequest request) {
		mSession = request.getSession();
	}

	// IProjectSummaryEnum.PROJECT 和 IProjectSummaryEnum.PROJECT_INFO_FORM use in TaskBoardCardPanel.jsp
	/**
	 * 將project instance設定於session中
	 * 
	 * @param project
	 */
	public void setProject(IProject project) {
		mSession.removeAttribute(IProjectSummaryEnum.PROJECT);
		mSession.setAttribute(IProjectSummaryEnum.PROJECT, project);
	}

	/**
	 * 設定ProjectInfoForm的instance到session中
	 * 
	 * @param infoForm
	 */
	public void setProjectInfoForm(ProjectInfoForm infoForm) {
		mSession.removeAttribute(IProjectSummaryEnum.PROJECT_INFO_FORM);
		mSession.setAttribute(IProjectSummaryEnum.PROJECT_INFO_FORM, infoForm);
	}

	/**
	 * 從session中取得project的instance 如果session中沒有的話，則從底層撈出project資料放置session cache起來
	 * 
	 * @param projectName
	 * @author Zam
	 * @param request client端傳上來的request
	 * @time 2012/8/28
	 */
	@Deprecated
	public static final IProject getProject(HttpServletRequest request) {
		HttpSession session = request.getSession();
		// 拿到request header的URL parameter
		String projectID = getURLParameter(request, "PID");
		
		if (projectID != null) {
			// 拿session裡的project資料
			ProjectObject project = (ProjectObject) session.getAttribute(projectID);
			IProject iProject = null;
			/**
			 * 如果session拿不到project的資料，則往DB找
			 */
			if (project == null) {
				// project = ResourceFacade.getProject(projectID);
				iProject = new ProjectMapper().getProjectByID(projectID);
				if (iProject != null) {
					session.setAttribute(projectID, project);
				}
				return iProject;
			}
			iProject = new ProjectMapper().getProjectByID(projectID);
			return iProject;
		}
		return null;
	}
	
	/**
	 * 從session中取得project的instance 如果session中沒有的話，則從底層撈出project資料放置session cache起來
	 * ezScrum v1.8
	 * 
	 * @param projectName
	 * @author Zam
	 * @param request client端傳上來的request
	 * @time 2014/2/24
	 */
	public static final ProjectObject getProjectObject(HttpServletRequest request) {
		HttpSession session = request.getSession();
		// 拿到 request header 的 URL parameter
		String projectName = getURLParameter(request, "PID");
		if (projectName != null) {
			// 拿 session 裡的 project 資料
			ProjectObject project = (ProjectObject) session.getAttribute(projectName + "_new");	// 當IProject完全改完，把new拿掉
			/**
			 * 如果 session 拿不到 project 的資料，則往 DB 找
			 */
			if (project == null) {
				project = new ProjectMapper().getProject(projectName);
				if (project != null) {
					try {
						project.toJSON().toString();
					} catch (JSONException e) {
					}
					session.setAttribute(projectName + "_new", project);	// 當IProject完全改完，把new拿掉
				}
			}
			return project;
		}
		return null;
	}
	
	/**
	 * 從session中取得project的instance 如果session中沒有的話，則從底層撈出project資料放置session cache起來
	 * ezScrum v1.8
	 * 
	 * @param projectName
	 * @author Zam
	 * @param request client端傳上來的request
	 * @time 2014/2/24
	 */
	public void setProjectObject(HttpServletRequest request, ProjectObject project) {
		HttpSession session = request.getSession();
		session.removeAttribute(project.getName() + "_new");		// 當IProject完全改完，把new拿掉
		session.setAttribute(project.getName() + "_new", project);	// 當IProject完全改完，把new拿掉
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
	public static ScrumRole getScrumRole(HttpServletRequest request, IProject project, AccountObject account) {
		// ezScrum v1.8
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, account);
		return scrumRole;
	}
	
	public static ScrumRole getScrumRole(HttpServletRequest request, ProjectObject project, AccountObject account) {
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
	public static void removeScrumRolesMap(HttpServletRequest request, AccountObject account) {
		String userPermessionNameForSession = account.getUsername() + sessionAttributeNameForPermession;

		List<HttpSession> sessionList = HttpSessionCollector.getSessionList(userPermessionNameForSession);

		for (HttpSession session : sessionList) {
			session.removeAttribute(userPermessionNameForSession);
		}
	}
}
