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
	public AccountObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		
		AccountObject account = null;
		try {
			if (result.next()) {
				account = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return account;
	}
	
	@Override
	public boolean update(AccountObject account) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, account.getId());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, account.getName());
		valueSet.addInsertValue(AccountEnum.EMAIL, account.getEmail());
		if (account.getPassword() != null && !account.getPassword().equals("")) {
			valueSet.addInsertValue(AccountEnum.PASSWORD, getMd5(account.getPassword()));
		}
		valueSet.addInsertValue(AccountEnum.ENABLE, account.getEnable() == true ? 1 : 0);
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
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
	 * 取出 account 的在 project 的 role 權限列表
	 * 
	 * @param id
	 *            - account id
	 * @return account access map
	 */
	public HashMap<String, ProjectRole> getProjectRoleList(long id) {
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
				.append(ProjectRoleEnum.ACCOUNT_ID).append(" = ").append(id);
		HashMap<String, ProjectRole> map = new HashMap<String, ProjectRole>();
		ProjectRole systemRole = getSystemRole(id);
		ResultSet result = mControl.executeQuery(query.toString());

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
	 * ------------------------------------------- System Table Operation
	 * -------------------------------------------
	 */

	/**
	 * 藉由 account id 判斷是否取出專案下的管理者帳號
	 * 
	 * @param id
	 *            account id
	 * @return admin account's project role
	 */
	public ProjectRole getSystemRole(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, id);
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

	public ScrumRole convertScrumRole(String projectId, String role,
			ResultSet result) throws SQLException {
		ScrumRole scrumRole = new ScrumRole(projectId, role);
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
		ProjectObject project = new ProjectObject(
				result.getLong(ProjectRoleEnum.PROJECT_ID),
				result.getString(ProjectEnum.NAME));
		project.setComment(result.getString(ProjectEnum.COMMENT))
				.setManager(result.getString(ProjectEnum.PRODUCT_OWNER))
				.setAttachFileSize(result.getLong(ProjectEnum.ATTATCH_MAX_SIZE))
				.save();

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
				members.add(convertAccount(result));
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
		MySQLQuerySet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ScrumRoleEnum.TABLE_NAME + '.'
				+ ScrumRoleEnum.PROJECT_ID, id);
		valueSet.addEqualCondition(ProjectRoleEnum.TABLE_NAME + '.'
				+ ProjectRoleEnum.PROJECT_ID, id);
		valueSet.addEqualCondition(ScrumRoleEnum.ACCESS_TASKBOARD, "1");
		valueSet.addCrossJoin(ProjectRoleEnum.TABLE_NAME,
				ScrumRoleEnum.TABLE_NAME + '.' + ScrumRoleEnum.ROLE,
				ProjectRoleEnum.TABLE_NAME + '.' + ProjectRoleEnum.ROLE);
		valueSet.addCrossJoin(AccountEnum.TABLE_NAME,
				ProjectRoleEnum.ACCOUNT_ID, AccountEnum.TABLE_NAME + '.'
						+ AccountEnum.ID);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
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
		valueSet.addEqualCondition(AccountEnum.USERNAME, username);
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
	 * Get project all accounts
	 * 
	 * @return account list
	 */
	public ArrayList<AccountObject> getAccounts() {
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
				String.valueOf(account.getEnable() == true ? 1 : 0));
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME,
				String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	public AccountObject convertAccount(ResultSet result) throws SQLException {
		long id = result.getLong(AccountEnum.ID);
		String username = result.getString(AccountEnum.USERNAME);
		String nickName = result.getString(AccountEnum.NICK_NAME);
		String password = result.getString(AccountEnum.PASSWORD);
		String email = result.getString(AccountEnum.EMAIL);
		boolean enable = result.getBoolean(AccountEnum.ENABLE);
		HashMap<String, ProjectRole> roles = getProjectRoleList(id);

		AccountObject account = new AccountObject(id, username);
		account.setPassword(password).setNickName(nickName).setEmail(email)
				.setEnable(enable).setRoles(roles);
		return account;
	}

	private String getMd5(String str) {
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