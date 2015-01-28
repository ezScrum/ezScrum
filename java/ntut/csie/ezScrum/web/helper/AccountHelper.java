package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.jcis.account.core.IRole;

public class AccountHelper {
	private final String SYSTEM = "system";
	private AccountMapper mAccountMapper;
	private IUserSession mUserSession;
	public AccountHelper() {
		mAccountMapper = new AccountMapper();
	}
	
	public AccountHelper(IUserSession userSession) {
		mUserSession = userSession;
		mAccountMapper = new AccountMapper();
	}

	public String validateAccountID(String id) {

		// 判斷帳號是否符合只有英文+數字的格式
		Pattern p = Pattern.compile("[0-9a-zA-Z_]*");
		Matcher m = p.matcher(id);
		boolean b = m.matches();

		// 若帳號可建立且ID format正確 則回傳true
		AccountMapper am = new AccountMapper();
		if (b && !am.isAccountExist(id) && !id.isEmpty()) {
			return "true";
		}

		return "false";
	}

	/**
	 * 進行帳號建立的動作, 並且將帳號 Assign Roles, 建立完畢執行儲存檔案
	 */
	public AccountObject createAccount(AccountInfo user) {
		AccountObject account = mAccountMapper.createAccount(user);
		return account;
	}

	public AccountObject updateAccount(AccountInfo user) {
		AccountObject updateAccount = mAccountMapper.updateAccount(user);
		return updateAccount;
	}

	public void deleteAccount(String id) {
		mAccountMapper.deleteAccount(Long.parseLong(id));
	}

	/**
	 * Assign Role
	 * @param id - account id
	 * @return
	 */
	public String getAssignedProject(String id) {
		// ezScrum v1.8
		AccountObject account = mAccountMapper.getAccount(Long.parseLong(id));
		HashMap<String, ProjectRole> rolesMap = mAccountMapper.getProjectRoleList(Long.parseLong(id));
		List<String> assignedProject = new ArrayList<String>();
		StringBuilder assignRoleInfo = new StringBuilder();
		
		// 取得帳號的Assign資訊
		assignRoleInfo.append("<AssignRoleInfo>");
		assignRoleInfo.append("<AccountInfo>");
		// Account Info
		assignRoleInfo.append("<ID>").append(account.getId()).append("</ID>");
		assignRoleInfo.append("<Account>").append(account.getUsername()).append("</Account>");
		assignRoleInfo.append("<Name>").append(account.getNickName()).append("</Name>");
		// Assign Roles
		assignRoleInfo.append("<Roles>");
		for (Entry<String, ProjectRole> entry : rolesMap.entrySet()) {
			ScrumRole permission = entry.getValue().getScrumRole();
			ProjectObject project = entry.getValue().getProject();
			String resource = permission.getProjectName();
			String operation = permission.getRoleName();
			assignRoleInfo.append("<Assigned>")
			  			  .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
						  .append("<Resource>").append(resource).append("</Resource>")
						  .append("<Operation>").append(operation).append("</Operation>")
						  .append("</Assigned>");
			assignedProject.add(resource);	// 記錄此project為assigned	
		}
		assignRoleInfo.append("</Roles>");
		
		// UnAssign Roles
		ProjectLogic projectLogic = new ProjectLogic();
		ArrayList<ProjectObject> projects = projectLogic.getProjects();
		for (ProjectObject project : projects) {
			String resource = project.getName();
			// 如果project沒有被assigned權限，則代表為unassigned的project
			if (!assignedProject.contains(resource)) {
				assignRoleInfo.append("<Unassigned>")
				  			  .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
							  .append("<Resource>").append(resource).append("</Resource>")
							  .append("</Unassigned>");
			}
		}
		// 判斷是否為administrator
		if (!assignedProject.contains(this.SYSTEM)) assignRoleInfo.append("<Unassigned><ResourceId>0</ResourceId><Resource>").append(this.SYSTEM).append("</Resource></Unassigned>");
		
		assignRoleInfo.append("</AccountInfo>");
		assignRoleInfo.append("</AssignRoleInfo>");
		
		return assignRoleInfo.toString();
	}
	public AccountObject assignRole_add(String id, String res, String op) throws Exception {
		// ezScrum v1.8
		AccountObject account = null;
		if (op.equals("admin")) {
			account = mAccountMapper.addSystemRole(Long.parseLong(id));
		} else {
			account = mAccountMapper.addProjectRole(Long.parseLong(res), Long.parseLong(id), RoleEnum.valueOf(op));
		}
		return account;
	}

	public AccountObject assignRole_remove(String id, String res, String op) throws Exception {
		// ezScrum v1.8
		AccountObject account = null;
		if (op.equals("admin")) {
			account = mAccountMapper.removeSystemRole(Long.parseLong(id));
		} else {
			account = mAccountMapper.removeProjectRole(Long.parseLong(res), Long.parseLong(id), RoleEnum.valueOf(op));
		}
		return account;
	}

	public String getAccountXML(AccountObject account) {
		List<AccountObject> accountList = new LinkedList<AccountObject>();
		accountList.add(account);
		return this.getXmlstring(accountList);
	}

	public String getAccountListXML() {
		AccountMapper am = new AccountMapper();
		List<AccountObject> accountList = am.getAccounts();
		return this.getXmlstring(accountList);
	}

	public String getManagementView(AccountObject account) {
		String result = "";
		if (account.getRoles().get("system") != null) {
			result = "Admin_ManagementView";
		} else {
			result = "User_ManagementView";
		}

		return result;
	}

	/**
	 * 將Roles String轉成Role String List
	 */
	private List<String> translateRoleStringWithCheck(IRole[] roles, String role) {
		List<String> roleList = new ArrayList<String>();
		// default
		if (roles != null) {
			for (IRole irole : roles) {
				if (!irole.getRoleId().equals(role)) roleList.add(irole.getRoleId());
			}
		}
		return roleList;
	}

	/**
	 * 將Roles String轉成Role String List
	 */
	private List<String> translateRoleString(IRole[] roles, String role) {
		List<String> roleList = new ArrayList<String>();
		// default
		if (roles != null) {
			for (IRole irole : roles) {
				roleList.add(irole.getRoleId());
			}
		}

		roleList.add(role);
		return roleList;
	}

	// ezScrum v1.8
	private String getXmlstring(List<AccountObject> users) {
		Iterator<AccountObject> iter = users.iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("<Accounts>");
		while (iter.hasNext()) {
			AccountObject account = (AccountObject) iter.next();
			sb.append("<AccountInfo>");
			sb.append("<ID>").append(account.getId()).append("</ID>");
			sb.append("<Account>").append(account.getUsername()).append("</Account>");
			sb.append("<Name>").append(account.getNickName()).append("</Name>");
			sb.append("<Mail>").append(account.getEmail()).append("</Mail>");
			sb.append("<Roles>").append(TranslateUtil.getRolesString(account.getRoles())).append("</Roles>");
			sb.append("<Enable>").append(account.getEnable()).append("</Enable>");
			sb.append("</AccountInfo>");
		}
		sb.append("</Accounts>");

		return sb.toString();
	}

}
