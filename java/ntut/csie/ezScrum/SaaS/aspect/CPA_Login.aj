package ntut.csie.ezScrum.SaaS.aspect;

import ntut.csie.ezScrum.SaaS.interfaces.account.TenantAdmin;
import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.ezScrum.web.action.LogonSubmitAction;
import ntut.csie.ezScrum.web.dataObject.Person;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.IAccount;

public aspect CPA_Login {
//	// replace: public ActionForward LogonSubmitAction.execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	pointcut logonSubmitActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	: execution(ActionForward LogonSubmitAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
//	
//	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
//	: logonSubmitActionPC(mapping, form, request, response) {
//		System.out.println("replaced by AOP...logonSubmitActionPC: " + thisJoinPoint);
//        LogonForm logonForm = (LogonForm) form;
//        
//        //取得登入的使用者帳號密碼
//        String userId = logonForm.getUserId();
//        String password = logonForm.getPassword();
//        System.out.println("userId = " + userId + ", password = " + password);
//        
//        //建立User Session
////      IUserSession userSession = ProjectInfoCenter.getInstance().login(userId, password);
////        IAccountManager manager = AccountFactory.getManager();
////		manager.confirmAccount(userId, password);
////		Account account = (Account)manager.getAccount(userId);
//        AccountMapper accountMapper = new AccountMapper();
//        accountMapper.confirmAccount(userId, password);
//        Account account = (Account)accountMapper.getAccountById(userId);
//		IUserSession userSession  = new UserSession(account);
//		
//        //設定權限資訊
//        AccessPermissionManager.setupPermission(request, userSession);
//
//        //設定User Session
//        request.getSession().setAttribute("UserSession", userSession);
//        
//        //為了要讓插件中可以使用session的中使用者的密碼，所以將原本利用MD5加密的密碼轉換成利用Base64加密。如此加密的密碼才可逆
//        String encodedPassword = new String( Base64.encode( password.getBytes() ) );
//        request.getSession().setAttribute("passwordForPlugin", encodedPassword);
//        
//		if (account.checkPermission(ScrumEnum.TENANT_PERMISSION)) {
//			System.out.println(account.getID() + " is an admin.");
//			return mapping.findForward("Tenant_ManagementView");
//		}
////		} else if(account.checkPermission(AccountManager.ADMINISTRATOR_PERMISSION)){
////			System.out.println(account.getID() + " is an tenant admin.");
////			return mapping.findForward("Admin_ManagementView");
////		}
//        
//        ProjectLogic projectLogic = new ProjectLogic();
//        projectLogic.cloneDefaultFile();
//        
//        return mapping.findForward("success");
//	}
	
	// replace: private Person getPerson(IAccount account)
	pointcut getPersonPC(IAccount account)
	: call(Person LogonSubmitAction.getPerson(IAccount)) && args(account);
	
	Person around(IAccount account)
	: getPersonPC(account) {
		System.out.println("replaced by AOP...getPersonPC: " + thisJoinPoint);
		AccountMapper accountMapper = new AccountMapper();
		if(account.checkPermission(accountMapper.getPermission(ScrumEnum.TENANT_PERMISSION))){
			return new TenantAdmin();
		}
		
		return proceed(account);
	}
}
