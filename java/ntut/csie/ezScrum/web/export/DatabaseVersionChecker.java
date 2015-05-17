package ntut.csie.ezScrum.web.export;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;

public class DatabaseVersionChecker {
	public ArrayList<String> mTablesName = new ArrayList<String>();
	public ArrayList<String> mLossesTablesName = new ArrayList<String>();
	public ArrayList<String> mInvalidTablesName = new ArrayList<String>();
	private ISQLControl mControl;
	private static String TABLE_NAME_LABEL = "TABLE_NAME";
	private static String PORT = "3306";
	// tables in version 1.7.2
	public static String ACCOUNT = "account";
	public static String ATTACH_FILE = "attach_file";
	public static String BUILDRESULT = "buildresult";
	public static String COMMIT_LOG = "commit_log";
	public static String COMMIT_STORY_RELATION = "commit_story_relation";
	public static String EZKANBAN_STATUSORDER = "ezkanban_statusorder";
	public static String EZSCRUM_STORY_RELATION = "ezscrum_story_relation";
	public static String EZSCRUM_TAG_RELATION = "ezscrum_tag_relation";
	public static String EZSCRUM_TAG_TABLE = "ezscrum_tag_table";
	public static String EZTRACK_COMBOFIELD = "eztrack_combofield";
	public static String EZTRACK_ISSUERELATION = "eztrack_issuerelation";
	public static String EZTRACK_ISSUETYPE = "eztrack_issuetype";
	public static String EZTRACK_REPORT = "eztrack_report";
	public static String EZTRACK_TYPEFIELD = "eztrack_typefield";
	public static String EZTRACK_TYPEFIELDVALUE = "eztrack_typefieldvalue";
	public static String HISTORY = "history";
	public static String ISSUE_PARTNER_RELATION = "issue_partner_relation";
	public static String MANTIS_BUGNOTE_TABLE = "mantis_bugnote_table";
	public static String MANTIS_BUGNOTE_TEXT_TABLE = "mantis_bugnote_text_table";
	public static String MANTIS_BUG_FILE_TABLE = "mantis_bug_file_table";
	public static String MANTIS_BUG_HISTORY_TABLE = "mantis_bug_history_table";
	public static String MANTIS_BUG_MONITOR_TABLE = "mantis_bug_monitor_table";
	public static String MANTIS_BUG_RELATIONSHIP_TABLE = "mantis_bug_relationship_table";
	public static String MANTIS_BUG_TABLE = "mantis_bug_table";
	public static String MANTIS_BUG_TAG_TABLE = "mantis_bug_tag_table";
	public static String MANTIS_BUG_TEXT_TABLE = "mantis_bug_text_table";
	public static String MANTIS_CONFIG_TABLE = "mantis_config_table";
	public static String MANTIS_CUSTOM_FIELD_PROJECT_TABLE = "mantis_custom_field_project_table";
	public static String MANTIS_CUSTOM_FIELD_STRING_TABLE = "mantis_custom_field_string_table";
	public static String MANTIS_CUSTOM_FIELD_TABLE = "mantis_custom_field_table";
	public static String MANTIS_EMAIL_TABLE = "mantis_email_table";
	public static String MANTIS_FILTERS_TABLE = "mantis_filters_table";
	public static String MANTIS_NEWS_TABLE = "mantis_news_table";
	public static String MANTIS_PROJECT_CATEGORY_TABLE = "mantis_project_category_table";
	public static String MANTIS_PROJECT_FILE_TABLE = "mantis_project_file_table";
	public static String MANTIS_PROJECT_HIERARCHY_TABLE = "mantis_project_hierarchy_table";
	public static String MANTIS_PROJECT_TABLE = "mantis_project_table";
	public static String MANTIS_PROJECT_USER_LIST_TABLE = "mantis_project_user_list_table";
	public static String MANTIS_PROJECT_VERSION_TABLE = "mantis_project_version_table";
	public static String MANTIS_SPONSORSHIP_TABLE = "mantis_sponsorship_table";
	public static String MANTIS_TAG_TABLE = "mantis_tag_table";
	public static String MANTIS_TOKENS_TABLE = "mantis_tokens_table";
	public static String MANTIS_USER_PREF_TABLE = "mantis_user_pref_table";
	public static String MANTIS_USER_PRINT_PREF_TABLE = "mantis_user_print_pref_table";
	public static String MANTIS_USER_PROFILE_TABLE = "mantis_user_profile_table";
	public static String MANTIS_USER_TABLE = "mantis_user_table";
	public static String PROJECT = "project";
	public static String PROJECT_ROLE = "project_role";
	public static String QUERY = "query";
	public static String RELEASE = "release";
	public static String RETROSPECTIVE = "retrospective";
	public static String SCRUM_ROLE = "scrum_role";
	public static String SPRINT = "sprint";
	public static String STORY = "story";
	public static String STORY_TAG_RELATION = "story_tag_relation";
	public static String SYSTEM = "system";
	public static String TAG = "tag";
	public static String TASK = "task";
	public static String UNPLANNED = "unplanned";
	
	public DatabaseVersionChecker() {
		Configuration config = new Configuration();
		mControl = new MySQLControl(config.getServerUrl(), PORT,
				config.getDBName());
		mControl.setUser(config.getDBAccount());
		mControl.setPassword(config.getDBPassword());
		setTablesToVerify();
	}
	
	public String getReport() {
		String report = "";
		if (isDatabaseVersionValid()) {
			return report;
		}
		report += "Database version error";
		if (!mLossesTablesName.isEmpty()) {
			report += "\nLoss tables: ";
			for (int i = 0; i < mLossesTablesName.size(); i++) {
				String tableName = mLossesTablesName.get(i);
				if (i == mLossesTablesName.size() - 1) {
					report += tableName;
				} else {
					report += tableName + ", ";
				}
			}
		}
		if (!mInvalidTablesName.isEmpty()) {
			report += "\nInvalid tables: ";
			for (int i = 0; i < mInvalidTablesName.size(); i++) {
				String tableName = mInvalidTablesName.get(i);
				if (i == mInvalidTablesName.size() - 1) {
					report += tableName;
				} else {
					report += tableName + ", ";
				}
			}
		}
		return report;
	}

	public boolean isDatabaseVersionValid() {
		boolean isVersionValid = true;
		ArrayList<String> currentTablesName = getCurrentTablesName();
		mLossesTablesName.clear();
		mInvalidTablesName.clear();
		for (String tableName : mTablesName) {
			if (!currentTablesName.contains(tableName)) {
				mLossesTablesName.add(tableName);
			}
		}
		for (String currentTableName : currentTablesName) {
			if (!mTablesName.contains(currentTableName)) {
				mInvalidTablesName.add(currentTableName);
			}
		}
		isVersionValid = mLossesTablesName.isEmpty() && mInvalidTablesName.isEmpty();
		return isVersionValid;
	}

	private ArrayList<String> getCurrentTablesName() {
		ArrayList<String> currentTablesName = new ArrayList<String>();
		mControl.connection();
		try {
			Connection connection = mControl.getconnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet resultSet = databaseMetaData.getTables(null, null, "%",
					null);
			while (resultSet.next()) {
				String tableName = resultSet.getString(TABLE_NAME_LABEL);
				currentTablesName.add(tableName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mControl.close();
		}
		return currentTablesName;
	}

	private void setTablesToVerify() {
		mTablesName.add(ACCOUNT);
		mTablesName.add(ATTACH_FILE);
		mTablesName.add(BUILDRESULT);
		mTablesName.add(COMMIT_LOG);
		mTablesName.add(COMMIT_STORY_RELATION);
		mTablesName.add(EZKANBAN_STATUSORDER);
		mTablesName.add(EZSCRUM_STORY_RELATION);
		mTablesName.add(EZSCRUM_TAG_RELATION);
		mTablesName.add(EZSCRUM_TAG_TABLE);
		mTablesName.add(EZTRACK_COMBOFIELD);
		mTablesName.add(EZTRACK_ISSUERELATION);
		mTablesName.add(EZTRACK_ISSUETYPE);
		mTablesName.add(EZTRACK_REPORT);
		mTablesName.add(EZTRACK_TYPEFIELD);
		mTablesName.add(EZTRACK_TYPEFIELDVALUE);
		mTablesName.add(HISTORY);
		mTablesName.add(ISSUE_PARTNER_RELATION);
		mTablesName.add(MANTIS_BUGNOTE_TABLE);
		mTablesName.add(MANTIS_BUGNOTE_TEXT_TABLE);
		mTablesName.add(MANTIS_BUG_FILE_TABLE);
		mTablesName.add(MANTIS_BUG_HISTORY_TABLE);
		mTablesName.add(MANTIS_BUG_MONITOR_TABLE);
		mTablesName.add(MANTIS_BUG_RELATIONSHIP_TABLE);
		mTablesName.add(MANTIS_BUG_TABLE);
		mTablesName.add(MANTIS_BUG_TAG_TABLE);
		mTablesName.add(MANTIS_BUG_TEXT_TABLE);
		mTablesName.add(MANTIS_CONFIG_TABLE);
		mTablesName.add(MANTIS_CUSTOM_FIELD_PROJECT_TABLE);
		mTablesName.add(MANTIS_CUSTOM_FIELD_STRING_TABLE);
		mTablesName.add(MANTIS_CUSTOM_FIELD_TABLE);
		mTablesName.add(MANTIS_EMAIL_TABLE);
		mTablesName.add(MANTIS_FILTERS_TABLE);
		mTablesName.add(MANTIS_NEWS_TABLE);
		mTablesName.add(MANTIS_PROJECT_CATEGORY_TABLE);
		mTablesName.add(MANTIS_PROJECT_FILE_TABLE);
		mTablesName.add(MANTIS_PROJECT_HIERARCHY_TABLE);
		mTablesName.add(MANTIS_PROJECT_TABLE);
		mTablesName.add(MANTIS_PROJECT_USER_LIST_TABLE);
		mTablesName.add(MANTIS_PROJECT_VERSION_TABLE);
		mTablesName.add(MANTIS_SPONSORSHIP_TABLE);
		mTablesName.add(MANTIS_TAG_TABLE);
		mTablesName.add(MANTIS_TOKENS_TABLE);
		mTablesName.add(MANTIS_USER_PREF_TABLE);
		mTablesName.add(MANTIS_USER_PRINT_PREF_TABLE);
		mTablesName.add(MANTIS_USER_PROFILE_TABLE);
		mTablesName.add(MANTIS_USER_TABLE);
		mTablesName.add(PROJECT);
		mTablesName.add(PROJECT_ROLE);
		mTablesName.add(QUERY);
		mTablesName.add(RELEASE);
		mTablesName.add(RETROSPECTIVE);
		mTablesName.add(SCRUM_ROLE);
		mTablesName.add(SPRINT);
		mTablesName.add(STORY);
		mTablesName.add(STORY_TAG_RELATION);
		mTablesName.add(SYSTEM);
		mTablesName.add(TAG);
		mTablesName.add(TASK);
		mTablesName.add(UNPLANNED);
	}
}
