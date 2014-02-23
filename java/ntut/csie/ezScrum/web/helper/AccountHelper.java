package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectInformation;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.RoleEnum;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;

public class AccountHelper {

	// from GetAssignedProjectAction
	private final String SYSTEM = "system";
	private AccountMapper mAccountMapper;
	private IUserSession mUserSession;
//	private HashMap<String, RoleEnum> mRoleMap;
	public AccountHelper() {
		mAccountMapper = new AccountMapper();
//		initRoleMap();
	}
	
	public AccountHelper(IUserSession userSession) {
		mUserSession = userSession;
		mAccountMapper = new AccountMapper(null, mUserSession);
//		initRoleMap();
	}

//	private void initRoleMap() {
//		mRoleMap = new HashMap<String, RoleEnum>();
//		mRoleMap.put("ProductOwner", RoleEnum.ProductOwner);
//		mRoleMap.put("ScrumMaster", RoleEnum.ScrumMaster);
//		mRoleMap.put("ScrumTeam", RoleEnum.ScrumTeam);
//		mRoleMap.put("Stakeholder", RoleEnum.Stakeholder);
//		mRoleMap.put("Guest", RoleEnum.Guest);
//	}
	
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
	public UserObject createAccount(UserInformation user, String roles) {
		UserObject account = mAccountMapper.createAccount(user, roles);
		return account;
	}

	public UserObject updateAccount(UserInformation user) {
		UserObject updateAccount = mAccountMapper.updateAccount(user);
		return updateAccount;
	}

	public void deleteAccount(String id) {
		mAccountMapper.deleteAccount(id);
	}

	/**
	 * Assign Role
	 * @param id - account id
	 * @return
	 */
	public String getAssignedProject(String id) {
//		IAccount account = mAccountMapper.getAccountById(id);
//		IRole[] roleList = account.getRoles();
//		List<String> assignedProject = new ArrayList<String>();
//
//		StringBuilder sb = new StringBuilder();
//
//		// 取得帳號的Assign資訊
//		sb.append("<AssignRoleInfo>");
//		sb.append("<Account>");
//		// Account Info
//		sb.append("<ID>" + account.getID() + "</ID>");
//		sb.append("<Name>" + account.getName() + "</Name>");
//
//		// Assign Roles
//		sb.append("<Roles>");
//		for (IRole role : roleList) {
//			IPermission[] permissions = role.getPermisions();
//			if (permissions != null) {
//				for (IPermission permission : permissions) {
//					String resource = permission.getResourceName();
//					String operation = permission.getOperation();
//					if (resource.equals("system") && (operation.equals("read") || operation.equals("createProject"))) continue;
//					sb.append("<Assigned>");
//					sb.append("<Resource>" + resource + "</Resource>");
//					sb.append("<Operation>" + operation + "</Operation>");
//					sb.append("</Assigned>");
//					// 記錄此project為assigned
//					assignedProject.add(resource);
//				}
//			}
//		}
//		sb.append("</Roles>");
//
//		// 取得尚未被Assign的專案資訊
//		ProjectLogic projectLogic = new ProjectLogic();
//		List<ProjectInformation> projects = projectLogic.getAllProjectsForDb();
//
//		for (ProjectInformation project : projects) {
//			String resource = project.getName();
//			// 如果project沒有被assigned權限，則代表為unassigned的project
//			if (!assignedProject.contains(resource)) {
//				sb.append("<Unassigned><ResourceId>")
//				.append(project.getId())
//				.append("</ResourceId><Resource>")
//				.append(resource)
//				.append("</Resource></Unassigned>");
//			}
//		}
//		// 判斷是否為administrator
//		if (!assignedProject.contains(this.SYSTEM)) sb.append("<Unassigned><Resource>" + this.SYSTEM + "</Resource></Unassigned>");
//
//		sb.append("</Account>");
//		sb.append("</AssignRoleInfo>");
//
//		return sb.toString();
		
		// ezScrum v1.8
		UserObject account = mAccountMapper.getAccountById(id);
		HashMap<String, ProjectRole> rolesMap = mAccountMapper.getProjectRoleList(id);
		List<String> assignedProject = new ArrayList<String>();
		StringBuilder assignRoleInfo = new StringBuilder();
		
		// 取得帳號的Assign資訊
		assignRoleInfo.append("<AssignRoleInfo>");
		assignRoleInfo.append("<AccountInfo>");
		// Account Info
		assignRoleInfo.append("<ID>").append(account.getId()).append("</ID>");
		assignRoleInfo.append("<Account>").append(account.getAccount()).append("</Account>");
		assignRoleInfo.append("<Name>").append(account.getName()).append("</Name>");
		// Assign Roles
		assignRoleInfo.append("<Roles>");
		for (Entry<String, ProjectRole> entry : rolesMap.entrySet()) {
			ScrumRole permission = entry.getValue().getScrumRole();
			ProjectInformation project = entry.getValue().getProject();
			String resource = permission.getProjectName();
			String operation = permission.getRoleName();
//			if (resource.equals("system") && (operation.equals("read") || operation.equals("createProject"))) continue;
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
		List<ProjectInformation> projects = projectLogic.getAllProjectsForDb();
		for (ProjectInformation project : projects) {
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
	public UserObject assignRole_add(String id, String res, String op) throws Exception {
//		IAccount account = mAccountMapper.getAccountById(id);
//		IRole[] roles = account.getRoles();
//		String role = "";
//		if (res.equals(ScrumEnum.SYSTEM)) role = op;
//		else role = res + "_" + op;
//		List<String> roleList = this.translateRoleString(roles, role);
//
//		// 進行帳號更新的動作, 並且將帳號 Assign Roles
//		mAccountMapper.addRole(mUserSession, account, roleList, id, res, op);
//
//		account = mAccountMapper.getAccountById(id);
//		(new ScrumRoleLogic()).setScrumRoles(mUserSession.getAccount());// reset Project<-->ScrumRole map
//
//		return account;
		
		// ezScrum v1.8
		mAccountMapper.addRoleToDb(res, id, RoleEnum.valueOf(op));
		UserObject account = mAccountMapper.getAccountById(id);
		return account;
	}

	public UserObject assignRole_remove(String id, String res, String op) throws Exception {
//		IAccount account = mAccountMapper.getAccountById(id);
//		IRole[] roles = account.getRoles();
//
//		String role = "";
//		if (res.equals(ScrumEnum.SYSTEM)) role = op;
//		else role = res + "_" + op;
//
//		List<String> roleList = this.translateRoleStringWithCheck(roles, role);
//
//		// 進行帳號更新的動作, 並且將帳號 Remove Roles
//		mAccountMapper.removeRole(mUserSession, account, id, roleList, res);
//
//		account = mAccountMapper.getAccountById(id);
//		(new ScrumRoleLogic()).setScrumRoles(mUserSession.getAccount());// reset Project<-->ScrumRole map
//
//		return account;
		
		// ezScrum v1.8
		mAccountMapper.removeRoleToDb(res, id, RoleEnum.valueOf(op));
		UserObject account = mAccountMapper.getAccountById(id);
		return account;
	}

	public String getAccountXML(UserObject account) {
		List<UserObject> accountList = new LinkedList<UserObject>();
		accountList.add(account);
		return this.getXmlstring(accountList);
	}

	public String getAccountListXML() {
		AccountMapper am = new AccountMapper();
		List<UserObject> accountList = am.getAccountList();
		return this.getXmlstring(accountList);
	}

	public String getManagementView(UserObject account) {
		String result = "";
//		IPermission permAdmin = mAccountMapper.getPermission("system", "admin");

//		if (Boolean.valueOf(account.checkPermission(permAdmin))) {
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

//	private String getXmlstring(List<IActor> actors) {
//		Iterator<IActor> iter = actors.iterator();
//		// write projects to XML format
//		StringBuilder sb = new StringBuilder();
//		sb.append("<Accounts>");
//		while (iter.hasNext()) {
//			IAccount account = (IAccount) iter.next();
//			sb.append("<Account>");
//			sb.append("<ID>" + account.getID() + "</ID>");
//			sb.append("<Name>" + account.getName() + "</Name>");
//			sb.append("<Mail>" + account.getEmail() + "</Mail>");
//			String mail = account.getEmail();
//			sb.append("<Roles>" + TranslateUtil.getRolesString(account.getRoles()) + "</Roles>");
//			sb.append("<Enable>" + account.getEnable() + "</Enable>");
//			String enable = account.getEnable();
//			if (enable == null || enable.equalsIgnoreCase("true")) enable = "true";
//			else enable = "false";
//			if (mail == null) mail = "";
//			sb.append("</Account>");
//		}
//		sb.append("</Accounts>");
//
//		return sb.toString();
//	}
	
	// ezScrum v1.8
	private String getXmlstring(List<UserObject> users) {
		Iterator<UserObject> iter = users.iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("<Accounts>");
		while (iter.hasNext()) {
			UserObject account = (UserObject) iter.next();
			sb.append("<AccountInfo>");
			sb.append("<ID>").append(account.getId()).append("</ID>");
			sb.append("<Account>").append(account.getAccount()).append("</Account>");
			sb.append("<Name>").append(account.getName()).append("</Name>");
			sb.append("<Mail>").append(account.getEmail()).append("</Mail>");
			sb.append("<Roles>").append(TranslateUtil.getRolesString(account.getRoles())).append("</Roles>");
			sb.append("<Enable>").append(account.getEnable()).append("</Enable>");
			sb.append("</AccountInfo>");
		}
		sb.append("</Accounts>");

		return sb.toString();
	}

}
