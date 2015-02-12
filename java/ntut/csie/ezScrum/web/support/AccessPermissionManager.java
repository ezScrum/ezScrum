package ntut.csie.ezScrum.web.support;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.logic.ProjectLogic;

public class AccessPermissionManager {
	
	static public void setupPermission(HttpServletRequest request, IUserSession userSession){
		// ezScrum v1.8
        //check create project permission
        AccountObject account = userSession.getAccount();
        HashMap<String, ProjectRole> roleMap = account.getRoles();
        ProjectRole role = roleMap.get("system");
        Boolean isAdmin = false;
        if (role != null) isAdmin = true;
        
        // 設定使用者是否擁有建立專案的權限
        request.getSession().setAttribute("CreateProject", isAdmin);
        
        // 設定使用者是否為系統管理員的權限
        request.getSession().setAttribute("Administration", isAdmin);
        
        // 判斷對於project而言,使用者能使用的功能權限
        ProjectLogic projectLogic = new ProjectLogic();
        request.getSession().setAttribute("FunctionAccess", projectLogic.getProjectPermissionMap(account));
        
        // 設定User role Session 
        request.getSession().setAttribute("ScrumRoles", roleMap);
	}
}
