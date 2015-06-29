package ntut.csie.ezScrum.dao;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectRoleEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.databasEnum.ScrumRoleEnum;
import ntut.csie.ezScrum.web.databasEnum.SystemEnum;

public class AccountDAO extends AbstractDAO<AccountObject, AccountObject> {

	private static AccountDAO sInstance = null;

	public static AccountDAO getInstance() {
		if (sInstance == null) {
			sInstance = new AccountDAO();
		}
		return sInstance;
	}

	@Override
	public long create(AccountObject account) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addInsertValue(AccountEnum.USERNAME, account.getUsername());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, account.getNickName());
		valueSet.addInsertValue(AccountEnum.EMAIL, account.getEmail());
		valueSet.addInsertValue(AccountEnum.PASSWORD,
				getMd5(account.getPassword()));
		valueSet.addInsertValue(AccountEnum.ENABLE,
				String.valueOf(account.getEnable() == true ? 1 : 0));
		valueSet.addInsertValue(AccountEnum.CREATE_TIME,
				System.currentTimeMillis());
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME,
				System.currentTimeMillis());
		String query = valueSet.getInsertQuery();

		mControl.execute(query, true);

		String[] keys = mControl.getKeys();
		long id = Long.parseLong(keys[0]);
		return id;
	}

	@Override
	public boolean update(AccountObject account) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, account.getId());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, account.getNickName());
		valueSet.addInsertValue(AccountEnum.EMAIL, account.getEmail());
		if (account.getPassword() != null && !account.getPassword().equals("")) {
			valueSet.addInsertValue(AccountEnum.PASSWORD,
					getMd5(account.getPassword()));
		}
		valueSet.addInsertValue(AccountEnum.ENABLE,
				account.getEnable() == true ? 1 : 0);
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME,
				String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * Create map about user and role in each attend project
	 * 
	 * @param projectId
	 * @param accountId
	 * @param role
	 * @return isCreateSuccess
	 */
	public boolean createProjectRole(long projectId, long accountId,
			RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addInsertValue(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addInsertValue(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addInsertValue(ProjectRoleEnum.ROLE,
				String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ProjectRoleEnum.CREATE_TIME,
				String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(ProjectRoleEnum.UPDATE_TIME,
				String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * Get account access mapping each attend project
	 * 
	 * @param accountId
	 * @return account access map
	 */
	public HashMap<String, ProjectRole> getProjectRoleMap(long accountId) {
		StringBuilder query = new StringBuilder();
		query.append("select * from ").append(ProjectRoleEnum.TABLE_NAME)
				.append(" as pr").append(" cross join ")
				.append(ProjectEnum.TABLE_NAME).append(" as p on ")
				.append(ProjectRoleEnum.PROJECT_ID).append(" = p.")
				.append(ProjectEnum.ID).append(" cross join ")
				.append(ScrumRoleEnum.TABLE_NAME).append(" as sr on")
				.append(" pr.").append(ProjectRoleEnum.PROJECT_ID)
				.append(" = sr.").append(ScrumRoleEnum.PROJECT_ID)
				.append(" and pr.").append(ProjectRoleEnum.ROLE)
				.append(" = sr.").append(ScrumRoleEnum.ROLE).append(" where ")
				.append(ProjectRoleEnum.ACCOUNT_ID).append(" = ")
				.append(accountId);

		String queryString = query.toString();
		HashMap<String, ProjectRole> map = new HashMap<String, ProjectRole>();
		ProjectRole systemRole = getSystemRole(accountId);
		ResultSet result = mControl.executeQuery(queryString);

		try {
			if (systemRole != null) {
				map.put("system", systemRole);
			}
			while (result.next()) {
				map.put(result.getString(ProjectEnum.NAME),
						getProjectWithScrumRole(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return map;
	}

	/**
	 * Delete account's role in project
	 * 
	 * @param projectId
	 * @param accountId
	 * @param role
	 * @return isDeleteSuccess
	 */
	public boolean deleteProjectRole(long projectId, long accountId,
			RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addEqualCondition(ProjectRoleEnum.ROLE,
				String.valueOf(role.ordinal()));
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * Create project system role
	 * 
	 * @param accountId
	 * @return isCreateSuccess
	 */
	public boolean createSystemRole(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addInsertValue(SystemEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * 藉由 account id 判斷是否取出專案下的管理者帳號
	 * 
	 * @param accountId
	 * @return admin account's project role
	 */
	public ProjectRole getSystemRole(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ProjectRole projectRole = null;

		try {
			if (result.next()) {
				ProjectObject project = new ProjectObject(0, "system");
				project.setDisplayName("system").setComment("system")
						.setManager("admin").setAttachFileSize(0)
						.setCreateTime(0);
				ScrumRole scrumRole = new ScrumRole("system", "admin");
				scrumRole.setisAdmin(true);
				projectRole = new ProjectRole(project, scrumRole);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return projectRole;
	}

	/**
	 * Delete account's system role in project
	 * 
	 * @param accountId
	 * @return isDeleteSuccess
	 */
	public boolean deleteSystemRole(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public ScrumRole convertScrumRole(String projectName, String role,
			ResultSet result) throws SQLException {
		ScrumRole scrumRole = new ScrumRole(projectName, role);
		scrumRole.setisGuest(RoleEnum.Guest == RoleEnum.valueOf(role));
		scrumRole.setAccessProductBacklog(result
				.getBoolean(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG));
		scrumRole.setAccessReleasePlan(result
				.getBoolean(ScrumRoleEnum.ACCESS_RELEASE_PLAN));
		scrumRole.setReadReport(result.getBoolean(ScrumRoleEnum.ACCESS_REPORT));
		scrumRole.setAccessRetrospective(result
				.getBoolean(ScrumRoleEnum.ACCESS_RETROSPECTIVE));
		scrumRole.setAccessSprintBacklog(result
				.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG));
		scrumRole.setAccessSprintPlan(result
				.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_PLAN));
		scrumRole.setAccessUnplannedItem(result
				.getBoolean(ScrumRoleEnum.ACCESS_UNPLANNED));
		scrumRole.setAccessTaskBoard(result
				.getBoolean(ScrumRoleEnum.ACCESS_TASKBOARD));
		scrumRole.setEditProject(result
				.getBoolean(ScrumRoleEnum.ACCESS_EDIT_PROJECT));
		return scrumRole;
	}

	public ProjectRole getProjectWithScrumRole(ResultSet result)
			throws SQLException {
		ProjectObject project = ProjectObject.get(result
				.getLong(ProjectRoleEnum.PROJECT_ID));
		RoleEnum role = RoleEnum.values()[result.getInt(ProjectRoleEnum.ROLE)];
		ScrumRole scrumRole = convertScrumRole(project.getName(), role.name(),
				result);

		return new ProjectRole(project, scrumRole);
	}

	/**
	 * 取得專案下的所有成員
	 * 
	 * @param id
	 *            project id
	 * @return project member list
	 */
	/**
	 * 取得專案下的所有成員
	 * 
	 * @param id
	 *            project id
	 * @return project member list
	 */
	public ArrayList<AccountObject> getProjectMembers(long id) {
		MySQLQuerySet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectRoleEnum.PROJECT_ID, id);
		valueSet.addCrossJoinMultiCondition(AccountEnum.TABLE_NAME,
				ProjectRoleEnum.ACCOUNT_ID, AccountEnum.TABLE_NAME + '.'
						+ AccountEnum.ID, AccountEnum.ENABLE, "1");
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<AccountObject> members = new ArrayList<AccountObject>();
		try {
			while (result.next()) {
				members.add(convertAccountUseAccountId(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}

		return members;
	}

	/**
	 * 取得專案下的所有能工作的成員
	 * 
	 * @param id
	 *            project id
	 * @return return worker list
	 */
	public ArrayList<AccountObject> getProjectWorkers(long id) {
		StringBuilder query = new StringBuilder();
		query.append("select ").append(AccountEnum.TABLE_NAME).append(".* from ").append(ProjectRoleEnum.TABLE_NAME).append(", ").append(AccountEnum.TABLE_NAME)
			.append(" where ")
			.append(ProjectRoleEnum.TABLE_NAME).append(".").append(ProjectRoleEnum.PROJECT_ID)
			.append(" = ").append(id)
			.append(" AND ")
			.append(ProjectRoleEnum.TABLE_NAME).append(".").append(ProjectRoleEnum.ACCOUNT_ID)
			.append(" = ").append(AccountEnum.TABLE_NAME).append(".").append(AccountEnum.ID)
			.append(" AND (")
			.append(ProjectRoleEnum.TABLE_NAME).append(".").append(ProjectRoleEnum.ROLE).append(" = ").append(RoleEnum.ScrumMaster.ordinal())
			.append(" OR ")
			.append(ProjectRoleEnum.TABLE_NAME).append(".").append(ProjectRoleEnum.ROLE).append(" = ").append(RoleEnum.ScrumTeam.ordinal())
			.append(")");
		ResultSet result = mControl.executeQuery(query.toString());
		ArrayList<AccountObject> workers = new ArrayList<AccountObject>();
		try {
			while (result.next()) {
				workers.add(convertAccount(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return workers;
	}

	/**
	 * Get account by account id
	 * 
	 * @param id
	 *            account id
	 * @return AccountObject
	 */
	@Override
	public AccountObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		AccountObject account = null;
		try {
			if (result.next()) {
				account = convertAccount(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return account;
	}

	/**
	 * Get account object by account's username
	 * 
	 * @param username
	 *            Account's username
	 * @return AccountObject
	 */
	public AccountObject get(String username) {
		AccountObject accountObject = null;

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(AccountEnum.USERNAME, username);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);
		try {
			if (result.next()) {
				accountObject = convertAccount(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return accountObject;
	}

	/**
	 * Get all accounts in ezScrum
	 * 
	 * @return account list
	 */
	public ArrayList<AccountObject> getAllAccounts() {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();

		try {
			while (result.next()) {
				accounts.add(convertAccount(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return accounts;
	}

	/**
	 * Use username and password to get the account.
	 * 
	 * NOTICE: password already be md5 hashed
	 * 
	 * @param username
	 * @param passwordMd5
	 * @return AccountObject
	 */
	public AccountObject confirmAccount(String username, String passwordMd5) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(AccountEnum.USERNAME, username);
		valueSet.addTextFieldEqualCondition(AccountEnum.PASSWORD, passwordMd5);
		valueSet.addEqualCondition(AccountEnum.ENABLE, 1);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		AccountObject account = null;

		try {
			while (result.next()) {
				account = convertAccount(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return account;
	}

	public AccountObject convertAccount(ResultSet result) throws SQLException {
		long id = result.getLong(AccountEnum.ID);
		String username = result.getString(AccountEnum.USERNAME);
		String nickName = result.getString(AccountEnum.NICK_NAME);
		String password = result.getString(AccountEnum.PASSWORD);
		String email = result.getString(AccountEnum.EMAIL);
		boolean enable = result.getBoolean(AccountEnum.ENABLE);

		AccountObject account = new AccountObject(id, username);
		account.setPassword(password).setNickName(nickName).setEmail(email)
				.setEnable(enable);
		return account;
	}

	/**
	 * For getProjectMembers 因為 join 出來的欄位是 ACCOUNT_ID
	 * 
	 * @param result
	 * @return AccountObject
	 * @throws SQLException
	 */
	public AccountObject convertAccountUseAccountId(ResultSet result)
			throws SQLException {
		long id = result.getLong(ProjectRoleEnum.ACCOUNT_ID);
		String username = result.getString(AccountEnum.USERNAME);
		String nickName = result.getString(AccountEnum.NICK_NAME);
		String password = result.getString(AccountEnum.PASSWORD);
		String email = result.getString(AccountEnum.EMAIL);
		boolean enable = result.getBoolean(AccountEnum.ENABLE);
		long createTime = result.getLong(AccountEnum.CREATE_TIME);
		long updateTime = result.getLong(AccountEnum.UPDATE_TIME);
		AccountObject account = new AccountObject(id, username);
		account.setPassword(password).setNickName(nickName).setEmail(email)
				.setEnable(enable).setCreateTime(createTime).setUpdateTime(updateTime);
		return account;
	}

	public String getMd5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte b[] = md.digest();
		str = byte2hex(b);
		return str;
	}

	private String byte2hex(byte b[]) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 255);
			if (stmp.length() == 1) {
				hs = (new StringBuilder(String.valueOf(hs))).append("0")
						.append(stmp).toString();
			} else {
				hs = (new StringBuilder(String.valueOf(hs))).append(stmp)
						.toString();
			}
		}
		return hs;
	}
}
