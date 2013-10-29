package ntut.csie.ezScrum.web.sqlService;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.internal.Account;

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
	private ITSPrefsStorage mPrefs;

	public MySQLService(ITSPrefsStorage prefs) {
		mPrefs = prefs;

		if (!mPrefs.getDBType().equals("")) TABLE_TYPE = mPrefs.getDBType();
		if (!mPrefs.getDBName().equals("")) DB_NAME = mPrefs.getDBName();

		// 設定要使用的SQLControl
		mControl = new MySQLControl(mPrefs.getServerUrl(), PORT_SERVICE_MYSQL, DB_NAME);
		mControl.setUser(prefs.getDBAccount());
		mControl.setPassword(prefs.getDBPassword());
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
	 * Account Table Operation
	 */

	public boolean createAccount(UserInformation user) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addInsertValue(AccountEnum.ACCOUNT, user.getId());
		valueSet.addInsertValue(AccountEnum.NICK_NAME, user.getName());
		valueSet.addInsertValue(AccountEnum.EMAIL, user.getEmail());
		valueSet.addInsertValue(AccountEnum.PASSWORD, getMd5(user.getPassword()));
		valueSet.addInsertValue(AccountEnum.ENABLE, user.getEnable().equals("true") ? "1" : "0");
		valueSet.addInsertValue(AccountEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(AccountEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	public boolean updateAccount(UserInformation user) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addLikeCondition(AccountEnum.ACCOUNT, user.getId());
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
		valueSet.addLikeCondition(AccountEnum.ACCOUNT, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public IAccount getAccountById(String id) {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			valueSet.addLikeCondition(AccountEnum.ACCOUNT, id);
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

	public IAccount confirmAccount(String account, String password) {
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

	public List<IActor> getAccountList() {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			String query = valueSet.getSelectQuery();
			ResultSet result = mControl.executeQuery(query);
			List<IActor> list = new ArrayList<IActor>();
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

	private IAccount getAccount(ResultSet result) throws SQLException {
		String id = result.getString(AccountEnum.ACCOUNT);
		String name = result.getString(AccountEnum.NICK_NAME);
		String password = result.getString(AccountEnum.PASSWORD);
		String email = result.getString(AccountEnum.EMAIL);
		String enable = result.getString(AccountEnum.ENABLE).equals("1") ? "true" : "false";
		boolean isGuest = false;
		boolean encryption = false;
		IAccount account = new Account(id, name, password, isGuest, encryption);
		account.setEmail(email);
		account.setEnable(enable);
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
				hs = (new StringBuilder(String.valueOf(hs))).append("0").append(stmp).toString();
			} else {
				hs = (new StringBuilder(String.valueOf(hs))).append(stmp).toString();
			}
		}
		return hs;
	}
}
