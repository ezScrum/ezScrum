package ntut.csie.ezScrum.web.support;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;

public class AccessPermissionManager {
	
	static public void setupPermission(HttpServletRequest request, IUserSession userSession){
//        //check create project permission
//		IAccount account = userSession.getAccount();
//        
//		AccountMapper accountMapper = new AccountMapper();
//        IPermission permCreateProject = accountMapper.getPermission("system_createProject");
//        IPermission permAdmin = accountMapper.getPermission("system", "admin");
//
//        //設定使用者是否擁有建立專案的權限
//        request.getSession().setAttribute("CreateProject", Boolean.valueOf(account.checkPermission(permCreateProject)));
//
//        //設定使用者是否為系統管理員的權限
//        request.getSession().setAttribute("Administration", Boolean.valueOf(account.checkPermission(permAdmin)));
//		
//        //判斷對於project而言,使用者能使用的功能權限
//        ProjectLogic projectLogic = new ProjectLogic();
//        request.getSession().setAttribute("FunctionAccess", projectLogic.getProjectPermissionMap(account));
//        
//        //透過專案資訊得到對應的權限
//        Map<String, ScrumRole> scrumRoles = (new ScrumRoleLogic()).getScrumRoles(account);
//        
//        //設定User role Session 
//        request.getSession().setAttribute("ScrumRoles", scrumRoles);
		
		// ezScrum v1.8
        //check create project permission
        UserObject account = userSession.getAccount();
        HashMap<String, ProjectRole> roleMap = account.getRoles();
        ScrumRole role = roleMap.get("system").getScrumRole();
//        AccountMapper accountMapper = new AccountMapper();
//        IPermission permCreateProject = accountMapper.getPermission("system_createProject");
//        IPermission permAdmin = accountMapper.getPermission("system", "admin");
        Boolean isAdmin = false;
        if (role != null) isAdmin = true;
        
        // 設定使用者是否擁有建立專案的權限
        request.getSession().setAttribute("CreateProject", isAdmin);
        
        // 設定使用者是否為系統管理員的權限
        request.getSession().setAttribute("Administration", isAdmin);
        
        // 判斷對於project而言,使用者能使用的功能權限
        ProjectLogic projectLogic = new ProjectLogic();
        request.getSession().setAttribute("FunctionAccess", projectLogic.getProjectPermissionMap(account));
        
        // 透過專案資訊得到對應的權限
//        Map<String, ScrumRole> scrumRoles = (new ScrumRoleLogic()).getScrumRoles(account);
        
        // 設定User role Session 
        request.getSession().setAttribute("ScrumRoles", roleMap);
	}
}
