package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.resource.core.IProject;

public class AddUserToRole {
	private CreateProject mCP;
	private CreateAccount mCA;
	private AccountObject mAccount = null;
	private IProject mProject = null;
	private ProjectObject mProjectObject = null;
	private Configuration mConfig = new Configuration();

	public AddUserToRole(CreateProject CP, CreateAccount CA) {
		mCP = CP;
		mCA = CA;
		mAccount = mCA.getAccountList().get(0);
		mProject = mCP.getProjectList().get(0);
		mProjectObject = mCP.getAllProjects().get(0);
	}

	/**
	 * 指定目前要新增的專案 Index
	 */
	public void setProjectIndex(int index) {
		if (index < mCP.getProjectList().size()) {
			mProject = mCP.getProjectList().get(index);
		}
	}

	/**
	 * 指定目前要新增的Account Index
	 */
	public void setAccountIndex(int index) {
		if (index < mCA.getAccountCount()) {
			mAccount = mCA.getAccountList().get(index);
		}
	}

	/**
	 * 取得目前指定的 Project
	 */
	public IProject getNowProject() {
		return mProject;
	}
	
	/**
	 * 取得目前指定的 Project
	 */
	public ProjectObject getNowProjectObject() {
		return mProjectObject;
	}

	/**
	 * 取得目前指定的 Account
	 */
	public AccountObject getNowAccount() {
		return mAccount;
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
		long projectId = mProjectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_PRODUCTOWNER);
	}

	/**
	 * 將目前指定的 Account 加入 Scrum Team 角色
	 */
	public void exe_ST() {
		long projectId = mProjectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_SCRUMTEAM);
	}

	/**
	 * 將目前指定的 Account 加入 Scrum Master 角色
	 */
	public void exe_SM() {
		long projectId = mProjectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_SCRUMMASTER);
	}

	/**
	 * 將目前指定的 Account 加入 Stakeholder 角色
	 */
	public void exe_Sh() {
		long projectId = mProjectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_STAKEHOLDER);
	}

	/**
	 * 將目前指定的 Account 加入 Guest 角色
	 */
	public void exe_Guest() {
		long projectId = mProjectObject.getId();
		updateAccount(projectId, ScrumEnum.SCRUMROLE_GUEST);
	}

	/**
	 * 將目前 Account 指定為系統管理員
	 */
	public void setNowAccountIsSystem() {
		mAccount = new AccountMapper().getAccount("admin");;
	}

	/**
	 * 將目前 Account 指定為disable
	 */
	public void setEnable(CreateAccount CA, int index, Boolean isEnable) {
		// ezScrum v1.8
		AccountObject account = CA.getAccountList().get(index);
		AccountHelper helper = new AccountHelper(mConfig.getUserSession());
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
		AccountHelper helper = new AccountHelper(mConfig.getUserSession());
		try {
			helper.addAssignedRole(mAccount.getId(), projectId, role);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("class: AddUserToRole, method: updateAccount, exception: " + e.toString());
		}
	}
}
