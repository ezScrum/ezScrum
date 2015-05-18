package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.HashMap;
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

public class AccountHelper {
	private final String SYSTEM = "system";
	private AccountMapper mAccountMapper;
	private IUserSession mUserSession;
	public AccountHelper() {
		mAccountMapper = new AccountMapper();
	}
	
	public AccountHelper(IUserSession userSession) {
		mUserSession = userSession;
		mAccountMapper = new AccountMapper(mUserSession);
	}

	public String validateUsername(String username) {

		// 判斷帳號是否符合只有英文+數字的格式
		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		Matcher matcher = pattern.matcher(username);
		boolean doesMatch = matcher.matches();

		// 若帳號可建立且ID format正確 則回傳true
		AccountMapper accountMapper = new AccountMapper();
		if (doesMatch && !accountMapper.isAccountExist(username) && !username.isEmpty()) {
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

	public boolean deleteAccount(long id) {
		return mAccountMapper.deleteAccount(id);
	}

	/**
	 * Assign Role
	 * @param accountId
	 * @return XML string
	 */
	public String getAssignedProject(long accountId) {
		AccountObject account = mAccountMapper.getAccount(accountId);
		HashMap<String, ProjectRole> rolesMap = mAccountMapper.getProjectRoleList(accountId);
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
			assignedProject.add(resource);	// 記錄此 project 為 assigned	
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
	
	public AccountObject addAssignedRole(long accountId, long projectId, String scrumRole) {
		// ezScrum v1.8
		AccountObject account = null;
		if (scrumRole.equals("admin")) {
			account = mAccountMapper.addSystemRole(accountId);
		} else {
			account = mAccountMapper.addProjectRole(projectId, accountId, RoleEnum.valueOf(scrumRole));
		}
		return account;
	}

	public AccountObject removeAssignRole(long accountId, long projectId, String role) throws Exception {
		// ezScrum v1.8
		AccountObject account = null;
		if (role.equals("admin")) {
			account = mAccountMapper.removeSystemRole(accountId);
		} else {
			account = mAccountMapper.removeProjectRole(projectId, accountId, RoleEnum.valueOf(role));
		}
		return account;
	}

	public String getAccountXML(AccountObject account) {
		ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();
		accounts.add(account);
		return getXmlstring(accounts);
	}

	public String getAccountListXML() {
		AccountMapper accountMapper = new AccountMapper();
		ArrayList<AccountObject> accounts = accountMapper.getAccounts();
		return getXmlstring(accounts);
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

	// ezScrum v1.8
	private String getXmlstring(ArrayList<AccountObject> accounts) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<Accounts>");
		for (AccountObject account : accounts) {
			if (account == null) {
				stringBuilder.append("Account not found.");
			} else {
				stringBuilder.append("<AccountInfo>");
				stringBuilder.append("<ID>").append(account.getId()).append("</ID>");
				stringBuilder.append("<Account>").append(account.getUsername()).append("</Account>");
				stringBuilder.append("<Name>").append(account.getNickName()).append("</Name>");
				stringBuilder.append("<Mail>").append(account.getEmail()).append("</Mail>");
				stringBuilder.append("<Roles>").append(TranslateUtil.getRolesString(account.getRoles())).append("</Roles>");
				stringBuilder.append("<Enable>").append(account.getEnable()).append("</Enable>");
				stringBuilder.append("</AccountInfo>");
			}
		}
		stringBuilder.append("</Accounts>");
		
		return stringBuilder.toString();
	}

}
