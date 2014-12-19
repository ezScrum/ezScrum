package ntut.csie.ezScrum.web.sqlService;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectRoleEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.databasEnum.ScrumRoleEnum;
import ntut.csie.ezScrum.web.databasEnum.SystemEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MySQLService {
	private static Log log = LogFactory.getLog(MantisService.class);
	private static String INITIATE_SQL_FILE = "initial_bk.sql";
	final public static String ROOT_TAG = "root";
	final private String PORT_SERVICE_MYSQL = "3306";

	private String TABLE_TYPE = "Default";
	private String DB_NAME = "ezScrum";
	private MySQLControl mControl;
	private Configuration mConfig;

	public MySQLService(Configuration config) {
		mConfig = config;

		if (!mConfig.getDBType().equals("")) TABLE_TYPE = mConfig.getDBType();
		if (!mConfig.getDBName().equals("")) DB_NAME = mConfig.getDBName();

		// 設定要使用的SQLControl
		mControl = new MySQLControl(mConfig.getServerUrl(), PORT_SERVICE_MYSQL, DB_NAME);
		mControl.setUser(mConfig.getDBAccount());
		mControl.setPassword(mConfig.getDBPassword());

	}

	/**
	 * 開啟連線
	 */
	public void openConnect() {
		mControl.connection();
	}

	/**
	 * 關閉連線,主要是關閉SQL的連線 在使用完成一定要關閉連線
	 */
	public void closeConnect() {
		try {
			mControl.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ISQLControl getControl() {
		return mControl;
	}

	/**
	 * ------------------------------------------
	 * Account Table Operation
	 * ------------------------------------------
	 */

	public boolean createAccount(AccountInfo user) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addInsertValue(AccountEnum.ACCOUNT, user.getAccount());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, user.getName());
		valueSet.addInsertValue(AccountEnum.EMAIL, user.getEmail());
		valueSet.addInsertValue(AccountEnum.PASSWORD, getMd5(user.getPassword()));
		valueSet.addInsertValue(AccountEnum.ENABLE, user.getEnable().equals("true") ? "1" : "0");
		valueSet.addInsertValue(AccountEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	public boolean updateAccount(AccountInfo user) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, user.getId());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, user.getName());
		valueSet.addInsertValue(AccountEnum.EMAIL, user.getEmail());
		if (user.getPassword() != null && !user.getPassword().equals("")) {
			valueSet.addInsertValue(AccountEnum.PASSWORD, getMd5(user.getPassword()));
		}
		valueSet.addInsertValue(AccountEnum.ENABLE, user.getEnable().equals("true") ? "1" : "0");
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getUpdateQuery();
		return mControl.executeUpdate(query);
	}

	public boolean deleteAccount(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public AccountObject getAccount(String account) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			valueSet.addTextFieldEqualCondition(AccountEnum.ACCOUNT, account);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			if (result.first()) {
				return getAccount(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public AccountObject getAccountById(String id) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			valueSet.addTextFieldEqualCondition(AccountEnum.ID, id);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			if (result.first()) {
				return getAccount(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public AccountObject confirmAccount(String account, String password) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			valueSet.addTextFieldEqualCondition(AccountEnum.ACCOUNT, account);
			valueSet.addTextFieldEqualCondition(AccountEnum.PASSWORD, getMd5(password));
			valueSet.addTextFieldEqualCondition(AccountEnum.ENABLE, "1"); 
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			if (result.first()) {
				return getAccount(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<AccountObject> getAccountList() {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			List<AccountObject> list = new ArrayList<AccountObject>();
			if (result.next()) {
				do {
					list.add(getAccount(result));
				} while (result.next());
				return list;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private AccountObject getAccount(ResultSet result) throws SQLException {
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
	
	/**
	 * -------------------------------------------
	 * Project Table Operation
	 * -------------------------------------------
	 */
	
	public boolean createProject(ProjectObject project) {
		String createTime = String.valueOf(System.currentTimeMillis());
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addInsertValue(ProjectEnum.PID, project.getName());
		valueSet.addInsertValue(ProjectEnum.NAME, project.getDisplayName());
		valueSet.addInsertValue(ProjectEnum.COMMENT, project.getComment());
		valueSet.addInsertValue(ProjectEnum.PRODUCT_OWNER, project.getManager());
		if (!project.getAttachFileSize().isEmpty()) {
			valueSet.addInsertValue(ProjectEnum.ATTATCH_MAX_SIZE, project.getAttachFileSize());
		}
		valueSet.addInsertValue(ProjectEnum.CREATE_TIME, createTime);
		valueSet.addInsertValue(ProjectEnum.UPDATE_TIME, createTime);
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}
	
	public boolean deleteProject(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(ProjectEnum.PID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
	
	public boolean updateProject(ProjectObject project) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(ProjectEnum.PID, project.getName());
		valueSet.addInsertValue(ProjectEnum.NAME, project.getDisplayName());
		valueSet.addInsertValue(ProjectEnum.COMMENT, project.getComment());
		valueSet.addInsertValue(ProjectEnum.PRODUCT_OWNER, project.getManager());
		valueSet.addInsertValue(ProjectEnum.ATTATCH_MAX_SIZE, project.getAttachFileSize());
		valueSet.addInsertValue(ProjectEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getUpdateQuery();
		return mControl.executeUpdate(query);
	}
	
	public List<ProjectObject> getProjectList() {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(ProjectEnum.TABLE_NAME);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			List<ProjectObject> list = new ArrayList<ProjectObject>();
			if (result.next()) {
				do {
					list.add(getProject(result));
				} while (result.next());
				return list;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ProjectObject getProjectById(String id) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(ProjectEnum.TABLE_NAME);
			valueSet.addEqualCondition(ProjectEnum.ID, id);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			if (result.first()) {
				return getProject(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ProjectObject getProjectByPid(String pid) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(ProjectEnum.TABLE_NAME);
			valueSet.addTextFieldEqualCondition(ProjectEnum.PID, pid);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			if (result.first()) {
				return getProject(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ProjectObject getProject(ResultSet result) throws SQLException {
		String id = result.getString(ProjectEnum.ID);
		String pid = result.getString(ProjectEnum.PID);
		String name = result.getString(ProjectEnum.NAME);
		String comment = result.getString(ProjectEnum.COMMENT);
		String productOwner = result.getString(ProjectEnum.PRODUCT_OWNER);
		String maxSize = result.getString(ProjectEnum.ATTATCH_MAX_SIZE);
		long createDate = result.getLong(ProjectEnum.CREATE_TIME);
		return new ProjectObject(id, pid, name, comment, productOwner, maxSize, createDate);
	}
	
	/**
	 * -------------------------------------------
	 * Project Role Table Operation
	 * -------------------------------------------
	 */
	
	public boolean createProjectRole(String projectId, String accountId, RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addInsertValue(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addInsertValue(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addInsertValue(ProjectRoleEnum.ROLE, String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ProjectRoleEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(ProjectRoleEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}
	
	public boolean deleteProjectRole(String projectId, String accountId, RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addEqualCondition(ProjectRoleEnum.ROLE, String.valueOf(role.ordinal()));
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
	
	public List<AccountObject> getProjectMemberList(String id) {
		try {
			MySQLQuerySet valueSet = new MySQLQuerySet();
			valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
			valueSet.addEqualCondition(ProjectRoleEnum.PROJECT_ID, id);
			valueSet.addCrossJoinMultiCondition(AccountEnum.TABLE_NAME, ProjectRoleEnum.ACCOUNT_ID, AccountEnum.TABLE_NAME + '.' + AccountEnum.ID, AccountEnum.ENABLE, "1");
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			List<AccountObject> list = new ArrayList<AccountObject>();
			if (result.next()) {
				do {
					list.add(getAccount(result));
				} while (result.next());
				return list;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
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
	
	public boolean createSystemRole(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addInsertValue(SystemEnum.ACCOUNT_ID, id);
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	public boolean deleteSystemRole(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
	
	
	/**
	 * -------------------------------------------
	 * Scrum Role Table Operation
	 * -------------------------------------------
	 */
	
	public boolean createScrumRole(String projectId, RoleEnum role, ScrumRole scrumRole) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addInsertValue(ScrumRoleEnum.PROJECT_ID, projectId);
		valueSet.addInsertValue(ScrumRoleEnum.ROLE, String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG, scrumRole.getAccessProductBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RELEASE_PLAN, scrumRole.getAccessReleasePlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_REPORT, scrumRole.getReadReport() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RETROSPECTIVE, scrumRole.getAccessRetrospective() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG, scrumRole.getAccessSprintBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_PLAN, scrumRole.getAccessSprintPlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_UNPLANNED, scrumRole.getAccessUnplannedItem() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_TASKBOARD, scrumRole.getAccessTaskBoard() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_EDIT_PROJECT, scrumRole.getEditProject() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(ScrumRoleEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}
	
	public boolean updateScrumRole(String projectId, RoleEnum role, ScrumRole scrumRole) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ScrumRoleEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(ScrumRoleEnum.ROLE, String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG, scrumRole.getAccessProductBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RELEASE_PLAN, scrumRole.getAccessReleasePlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_REPORT, scrumRole.getReadReport() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RETROSPECTIVE, scrumRole.getAccessRetrospective() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG, scrumRole.getAccessSprintBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_PLAN, scrumRole.getAccessSprintPlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_UNPLANNED, scrumRole.getAccessUnplannedItem() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_TASKBOARD, scrumRole.getAccessTaskBoard() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_EDIT_PROJECT, scrumRole.getEditProject() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getUpdateQuery();
		return mControl.executeUpdate(query);
	}
	
	public ScrumRole getScrumRole(String id, String projectId, RoleEnum role) {
		try {
			MySQLQuerySet valueSet = new MySQLQuerySet();
			valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
			valueSet.addEqualCondition(ScrumRoleEnum.PROJECT_ID, id);
			valueSet.addEqualCondition(ScrumRoleEnum.ROLE, String.valueOf(role.ordinal()));
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			if (result.first()) {
				return getScrumRole(projectId, role.name(), result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	
	public List<AccountObject> getProjectWorkerList(String id) {
		try {
			MySQLQuerySet valueSet = new MySQLQuerySet();
			valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
			valueSet.addEqualCondition(ScrumRoleEnum.TABLE_NAME + '.' + ScrumRoleEnum.PROJECT_ID, id);
			valueSet.addEqualCondition(ProjectRoleEnum.TABLE_NAME + '.' + ProjectRoleEnum.PROJECT_ID, id);
			valueSet.addEqualCondition(ScrumRoleEnum.ACCESS_TASKBOARD, "1");
			valueSet.addCrossJoin(ProjectRoleEnum.TABLE_NAME, ScrumRoleEnum.TABLE_NAME + '.' + ScrumRoleEnum.ROLE, ProjectRoleEnum.TABLE_NAME + '.' + ProjectRoleEnum.ROLE);
			valueSet.addCrossJoin(AccountEnum.TABLE_NAME, ProjectRoleEnum.ACCOUNT_ID, AccountEnum.TABLE_NAME + '.' + AccountEnum.ID);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			List<AccountObject> list = new ArrayList<AccountObject>();
			while (result.next()) {
				list.add(getAccount(result));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
