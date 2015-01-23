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
				return convertAccount(result);
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
				return convertAccount(result);
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
				return convertAccount(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

//	public List<AccountObject> getAccountList() {
//		try {
//			IQueryValueSet valueSet = new MySQLQuerySet();
//			valueSet.addTableName(AccountEnum.TABLE_NAME);
//			String query = valueSet.getSelectQuery();
//			ResultSet result = mControl.executeQuery(query);
//			List<AccountObject> list = new ArrayList<AccountObject>();
//			if (result.next()) {
//				do {
//					list.add(convertAccount(result));
//				} while (result.next());
//				return list;
//			} else {
//				return null;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	private AccountObject convertAccount(ResultSet result) throws SQLException {
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
	 * Project Role Table Operation
	 * -------------------------------------------
	 */
	
	public boolean createProjectRole(long projectId, String accountId, RoleEnum role) {
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
	
	public boolean deleteProjectRole(long projectId, String accountId, RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addEqualCondition(ProjectRoleEnum.ROLE, String.valueOf(role.ordinal()));
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
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
}
