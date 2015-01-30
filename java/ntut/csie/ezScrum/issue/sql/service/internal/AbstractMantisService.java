package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;

public abstract class AbstractMantisService {

	private ISQLControl m_control;
	// private ITSPrefsStorage m_prefs;
	private Configuration m_config;

	public int getUserID(String userName) {
		// IQueryValueSet valueSet = new MySQLQuerySet();
		// valueSet.addTableName("mantis_user_table");
		// valueSet.addLikeCondition("username", userName);
		// String query = valueSet.getSelectQuery();
		// // String query = "SELECT `id` FROM `mantis_user_table` WHERE
		// `username`
		// // LIKE '"
		// // + userName + "'";
		// try {
		// ResultSet result = m_control.executeQuery(query);
		// int userID = 0;
		// if (result.next()) {
		// userID = result.getInt("id");
		// }
		// return userID;
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// return 0;
		// ezScrum v1.8
		// 還沒改到這裡的過渡期程式碼，因為user都已經轉移到資料庫，這個mantis_user_table應該不能用了，改用account
		// table
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(AccountEnum.USERNAME, userName);
		String query = valueSet.getSelectQuery();
		try {
			ResultSet result = m_control.executeQuery(query);
			int userID = 0;
			if (result.next()) {
				userID = result.getInt(AccountEnum.ID);
			}
			return userID;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected String getUserName(int userID) {
		// IQueryValueSet valueSet = new MySQLQuerySet();
		// valueSet.addTableName("mantis_user_table");
		// valueSet.addEqualCondition("id", Integer.toString(userID));
		// String query = valueSet.getSelectQuery();
		// // String query = "SELECT `username` FROM `mantis_user_table` WHERE
		// `id`
		// // ="
		// // + userID;
		// try {
		// ResultSet result = m_control.executeQuery(query);
		// String userName = "";
		// if (result.next()) {
		// userName = result.getString("username");
		// }
		// return userName;
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// return "";

		// ezScrum v1.8
		// 還沒改到這裡的過渡期程式碼，因為user都已經轉移到資料庫，這個mantis_user_table應該不能用了，改用account
		// table
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, Integer.toString(userID));
		String query = valueSet.getSelectQuery();
		try {
			ResultSet result = m_control.executeQuery(query);
			String userName = "";
			if (result.next()) {
				userName = result.getString(AccountEnum.USERNAME);
			}
			return userName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	protected void setControl(ISQLControl control) {
		m_control = control;
	}

	protected ISQLControl getControl() {
		return m_control;
	}

	protected void setConfig(Configuration config) {
		m_config = config;
	}

	protected Configuration getConfig() {
		return m_config;
	}

	protected int getProjectID(String projectName) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_project_table");
		valueSet.addLikeCondition("name", projectName);
		String query = valueSet.getSelectQuery();
		// String query = "SELECT `id` FROM `mantis_project_table` WHERE `name`
		// LIKE '"
		// + projectName + "'";
		// Statement stmt;
		try {
			ResultSet result = getControl().executeQuery(query);
			int projectId = -1;
			if (result.next())
				projectId = result.getInt("id");
			return projectId;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	protected int getProjectId(String pid) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(ProjectEnum.NAME, pid);
		String query = valueSet.getSelectQuery();

		ResultSet result = getControl().executeQuery(query);
		int projectId = -1;
		try {
			if (result.next()) {
				projectId = result.getInt(ProjectEnum.ID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectId;
	}

	protected String getProjectName(int projectID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_project_table");
		valueSet.addLikeCondition("id", String.valueOf(projectID));
		String query = valueSet.getSelectQuery();
		try {
			ResultSet result = getControl().executeQuery(query);
			String projectName = "";
			if (result.next())
				projectName = result.getString("name");
			return projectName;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	protected int getProjectAccessLevel(int userID, int projectID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_project_user_list_table");
		valueSet.addEqualCondition("project_id", Integer.toString(projectID));
		valueSet.addEqualCondition("user_id", Integer.toString(userID));
		String query = valueSet.getSelectQuery();
		// String query = "SELECT `access_level` FROM
		// `mantis_project_user_list_table` WHERE `project_id` ="
		// + projectID + " AND `user_id` =" + userID;
		// Statement stmt;
		try {
			// stmt = conn.createStatement();
			// ResultSet result = stmt.executeQuery(query);
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				int accessLevel = result.getInt("access_level");
				return accessLevel;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (getProjectViewStatus(projectID))
			return getDefaultAccessLevel(userID);
		return 0;
	}

	protected boolean getProjectViewStatus(int projectID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_project_table");
		valueSet.addEqualCondition("id", Integer.toString(projectID));
		String query = valueSet.getSelectQuery();

		try {
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				if (result.getInt("view_state") > ITSEnum.PUBLIC_VIEW_STATUS)
					return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	protected int getDefaultAccessLevel(int userID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_user_table");
		valueSet.addEqualCondition("id", Integer.toString(userID));

		String query = valueSet.getSelectQuery();

		// String query = "SELECT `access_level` FROM `mantis_user_table` WHERE
		// `id` ="
		// + userID;
		// Statement stmt;
		try {
			// stmt = conn.createStatement();
			// ResultSet result = stmt.executeQuery(query);
			ResultSet result = getControl().executeQuery(query);

			int accessLevel = ITSEnum.VIEWER_ACCESS_LEVEL;

			if (result.next())
				accessLevel = result.getInt("access_level");

			return accessLevel;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ITSEnum.VIEWER_ACCESS_LEVEL;
	}

	protected List<String> getAllUsers() {
		List<String> list = new ArrayList<String>();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_user_table");
		valueSet.addEqualCondition("enabled", "1");
		String query = valueSet.getSelectQuery();

		// String query =
		// "SELECT `username` FROM `mantis_user_table` WHERE `enabled` = 1";
		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {
				list.add(result.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
