package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.logic.AccountLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowScrumReportAction extends Action {
	private static Log log = LogFactory.getLog(ShowScrumReportAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("execute Show Scrum Report Action");
//		IProject project = (IProject) request.getSession().getAttribute("Project");
		IProject project = SessionManager.getProject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	
    	// get Account, ScrumRole
    	UserObject account = session.getAccount();
//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, acc);
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
//		MantisAccountMapper accountHelper = new MantisAccountMapper(project, session);
//		
//		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
//    	if ( accountHelper.CheckAccount(request) && sr.getAccessTaskBoard() ) {
//			return mapping.findForward("success");
//		}else{
//			return mapping.findForward("permissionDenied");
//		}
		AccountLogic accountLogic = new AccountLogic();
		
		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
    	if ( accountLogic.checkAccount(request) && sr.getAccessTaskBoard() ) {
			return mapping.findForward("success");
		}else{
			return mapping.findForward("permissionDenied");
		}
	}
}