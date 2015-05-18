package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.logic.AccountLogic;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class FowardAction extends Action{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		AccountLogic accountLogic = new AccountLogic();
		
		// 檢查帳號不通過，提示錯誤頁面
		if ( accountLogic.checkAccount(request) ){
			return mapping.findForward("success");
		}else{
			return mapping.findForward("permissionDenied");
		}
	}
	
}
