package ntut.csie.ezScrum.dao;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	public static AccountDAO getInstance() {
		if (sInstance == null) {
			sInstance = new AccountDAO();
		}
		return (AccountDAO) sInstance;
	}
	
	@Override
	public long create(AccountObject account) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addInsertValue(AccountEnum.ACCOUNT, account.getAccount());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, account.getName());
		valueSet.addInsertValue(AccountEnum.EMAIL, account.getEmail());
		valueSet.addInsertValue(AccountEnum.PASSWORD, getMd5(account.getPassword()));
		valueSet.addInsertValue(AccountEnum.ENABLE, account.getEnable().equals("true") ? "1" : "0");
		valueSet.addInsertValue(AccountEnum.CREATE_TIME, System.currentTimeMillis());
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME, System.currentTimeMillis());
		String query = valueSet.getInsertQuery();
		
		mControl.execute(query, true);
		
		String[] keys = mControl.getKeys();
		long id = Long.parseLong(keys[0]);
		return id;
	}
	
	/**
	 * 取出 account 的在 project 的 role 權限列表 
	 * 
	 * @param id - account id
	 * @return
	 */
	public HashMap<String, ProjectRole> getProjectRoleList(String id) {
		try {
			StringBuilder query = new StringBuilder();
			query.append("select * from ").append(ProjectRoleEnum.TABLE_NAME).append(" as pr")
				 .append(" cross join ").append(ProjectEnum.TABLE_NAME).append(" as p on ")
				 					   .append(ProjectRoleEnum.PROJECT_ID).append(" = p.").append(ProjectEnum.ID)
				 .append(" cross join ").append(ScrumRoleEnum.TABLE_NAME).append(" as sr on")
				 					   .append(" pr.").append(ProjectRoleEnum.PROJECT_ID).append(" = sr.").append(ScrumRoleEnum.PROJECT_ID)
				 					   .append(" and pr.").append(ProjectRoleEnum.ROLE).append(" = sr.").append(ScrumRoleEnum.ROLE)
			 	 .append(" where ").append(ProjectRoleEnum.ACCOUNT_ID).append(" = ").append(id);
			ResultSet result = mControl.executeQuery(query.toString());
			HashMap<String, ProjectRole> map = new HashMap<String, ProjectRole>();
			ProjectRole systemRole = getSystemRole(id); 
			if (systemRole != null)	map.put("system", systemRole);
			if (result.next()) {
				do {
					map.put(result.getString(ProjectEnum.PID), getProjectWithScrumRole(result));
				} while (result.next());
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * -------------------------------------------
	 * System Table Operation
	 * -------------------------------------------
	 */
	
	public ProjectRole getSystemRole(String id) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(SystemEnum.TABLE_NAME);
			valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, id);
			ResultSet result = mControl.executeQuery(valueSet.getSelectQuery());
			if (result.next()) {
				ProjectObject project = new ProjectObject("0", "system", "system", "system", "admin", "0", 0);
				ScrumRole scrumRole = new ScrumRole("system", "admin");
				scrumRole.setisAdmin(true);
				return new ProjectRole(project, scrumRole);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ScrumRole getScrumRole(String projectId, String role, ResultSet result) throws SQLException {
		ScrumRole scrumRole = new ScrumRole(projectId, role);
		scrumRole.setisGuest(RoleEnum.Guest == RoleEnum.valueOf(role));
		scrumRole.setAccessProductBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG));
		scrumRole.setAccessReleasePlan(result.getBoolean(ScrumRoleEnum.ACCESS_RELEASE_PLAN));
		scrumRole.setReadReport(result.getBoolean(ScrumRoleEnum.ACCESS_REPORT));
		scrumRole.setAccessRetrospective(result.getBoolean(ScrumRoleEnum.ACCESS_RETROSPECTIVE));
		scrumRole.setAccessSprintBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG));
		scrumRole.setAccessSprintPlan(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_PLAN));
		scrumRole.setAccessUnplannedItem(result.getBoolean(ScrumRoleEnum.ACCESS_UNPLANNED));
		scrumRole.setAccessTaskBoard(result.getBoolean(ScrumRoleEnum.ACCESS_TASKBOARD));
		scrumRole.setEditProject(result.getBoolean(ScrumRoleEnum.ACCESS_EDIT_PROJECT));
		return scrumRole;
	}
	
	private ProjectRole getProjectWithScrumRole(ResultSet result) throws SQLException {
		String id = result.getString(ProjectRoleEnum.PROJECT_ID);
		String pid = result.getString(ProjectEnum.PID);
		String name = result.getString(ProjectEnum.NAME);
		String comment = result.getString(ProjectEnum.COMMENT);
		String productOwner = result.getString(ProjectEnum.PRODUCT_OWNER);
		String maxSize = result.getString(ProjectEnum.ATTATCH_MAX_SIZE);
		ProjectObject project = new ProjectObject(id, pid, name, comment, productOwner, maxSize, 0);
		
		RoleEnum role = RoleEnum.values()[result.getInt(ProjectRoleEnum.ROLE)];
		ScrumRole scrumRole = getScrumRole(pid, role.name(), result);

		return new ProjectRole(project, scrumRole);
    }

	@Override
	public AccountObject get(long id) throws SQLException {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		
		AccountObject account = null;
		if (result.next()) {
			account = convert(result);
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
		valueSet.addInsertValue(AccountEnum.ENABLE, account.getEnable().equals("true") ? "1" : "0");
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

	private AccountObject convert(ResultSet result) throws SQLException {
		String id;
		try {
			id = result.getString(ProjectRoleEnum.ACCOUNT_ID);
		} catch (SQLException e) {
			id = result.getString(AccountEnum.ID);
		}
		String account = result.getString(AccountEnum.ACCOUNT);
		String name = result.getString(AccountEnum.NICK_NAME);
		String password = result.getString(AccountEnum.PASSWORD);
		String email = result.getString(AccountEnum.EMAIL);
		String enable = result.getString(AccountEnum.ENABLE).equals("1") ? "true" : "false";
		HashMap<String, ProjectRole> roles = getProjectRoleList(id);
		return new AccountObject(id, account, name, password, email, enable, roles);
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
				hs = (new StringBuilder(String.valueOf(hs))).append("0").append(stmp).toString();
			} else {
				hs = (new StringBuilder(String.valueOf(hs))).append(stmp).toString();
			}
		}
		return hs;
	}
}
