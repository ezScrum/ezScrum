package ntut.csie.ezScrum.web.action.rbac;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ScrumRoleManageAction extends Action {

	// 純粹拿來轉頁用
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get Account, ScrumRole
		//IAccount acc = session.getAccount();
//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, acc);
		//AccountHelper accountHelper = new AccountHelper(project, session);
		
		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
//		if ( accountHelper.CheckAccount() && sr.getAccessTaskBoard() ) {
			return mapping.findForward("success");
//		}else{
//			return mapping.findForward("permissionDenied");
//		}
		
	}

}
