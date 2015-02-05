package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

public class AddUserToRole {
	private CreateProject CP;
	private CreateAccount CA;

	private AccountObject theAccount = null;
	private IProject theProject = null;
	private ProjectObject projectObject = null;

	private Configuration configuration = new Configuration();

	public AddUserToRole(CreateProject cp, CreateAccount ca) {
		this.CP = cp;
		this.CA = ca;

		this.theAccount = this.CA.getAccountList().get(0);
		this.theProject = this.CP.getProjectList().get(0);
		this.projectObject = this.CP.getAllProjects().get(0);
	}

	/**
	 * 指定目前要新增的專案 Index
	 */
	public void setProjectIndex(int index) {
		if (index < this.CP.getProjectList().size()) {
			this.theProject = this.CP.getProjectList().get(index);
		}
	}

	/**
	 * 指定目前要新增的Account Index
	 */
	public void setAccountIndex(int index) {
		if (index < this.CA.getAccountCount()) {
			this.theAccount = this.CA.getAccountList().get(index);
		}
	}

	/**
	 * 取得目前指定的 Project
	 */
	public IProject getNowProject() {
		return this.theProject;
	}
	
	/**
	 * 取得目前指定的 Project
	 */
	public ProjectObject getNowProjectObject() {
		return this.projectObject;
	}

	/**
	 * 取得目前指定的 Account
	 */
	public AccountObject getNowAccount() {
		return this.theAccount;
	}

//	/**
//	 * 將目前指定的 Account 加入 Admin 角色
//	 */
//	public void exe_System() {
//		String res = ScrumEnum.SYSTEM;
//		String op = ScrumEnum.SCRUMROLE_ADMIN;
//		updateAccount(res, op);
//	}

	/**
	 * 將目前指定的 Account 加入 Product Owner 角色
	 */
	public void exe_PO() {
		long projectId = this.projectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_PRODUCTOWNER);
	}

	/**
	 * 將目前指定的 Account 加入 Scrum Team 角色
	 */
	public void exe_ST() {
		long projectId = this.projectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_SCRUMTEAM);
	}

	/**
	 * 將目前指定的 Account 加入 Scrum Master 角色
	 */
	public void exe_SM() {
		long projectId = this.projectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_SCRUMMASTER);
	}

	/**
	 * 將目前指定的 Account 加入 Stakeholder 角色
	 */
	public void exe_Sh() {
		long projectId = this.projectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_STAKEHOLDER);
	}

	/**
	 * 將目前指定的 Account 加入 Guest 角色
	 */
	public void exe_Guest() {
		long projectId = this.projectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_GUEST);
	}

	/**
	 * 將目前 Account 指定為系統管理員
	 */
	public void setNowAccountIsSystem() {
		this.theAccount = new AccountMapper().getAccount("admin");;
	}

	/**
	 * 將目前 Account 指定為disable
	 */
	public void setEnable(CreateAccount CA, int index, Boolean isEnable) {
		// ezScrum v1.8
		AccountObject account = CA.getAccountList().get(index);
		AccountHelper helper = new AccountHelper(configuration.getUserSession());
		AccountInfo user = new AccountInfo();
		user.id = account.getId();
		user.username = account.getUsername();
		user.nickName = account.getNickName();
		user.password = account.getPassword();
		user.email = account.getEmail();
		user.enable = isEnable;
		helper.updateAccount(user);
	}

	private void updateAccount(long projectId, String role) {
		// ezScrum v1.8
		AccountHelper helper = new AccountHelper(configuration.getUserSession());
		try {
			helper.addAssignedRole(theAccount.getId(), projectId, role);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("class: AddUserToRole, method: updateAccount, exception: " + e.toString());
		}
	}
}
