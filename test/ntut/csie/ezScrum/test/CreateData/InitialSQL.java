package ntut.csie.ezScrum.test.CreateData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.jcis.core.util.CloseStreamUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class InitialSQL {
	private static Log mlog = LogFactory.getLog(InitialSQL.class);
	public static String mMYSQL = "MySQL";
	public static String mLOCALDB = "Default";
	private String mReDirFile;		// 本地端 initial_bk.sql 檔案位置
	private DBBean mDb = null;
	private String mAppServID = "";		// DB access ID
	private String mAppServPWD = "";		// DB access Password
	private Configuration mConfig = null;
	private String mSQLType = mMYSQL;

	// 遠端執行，使用 DBBean 下 MySQL 指令清空資料表
	public InitialSQL(Configuration configuration) {
		// 依照設定檔初始化資料庫
		// 如果是Default資料庫的話，就不需要特地去清除
		// 目前只有 MySql 需要連線進行清除資料庫
		mConfig = configuration;
		mReDirFile = mConfig.getDataPath() + File.separator + "InitialData" + File.separator + "initial_bk.sql";

		if (mConfig.getDBType().equals("MySQL")) {
			mDb = new DBBean(configuration.getServerUrl(), configuration.getDBAccount(), configuration.getDBPassword(), configuration.getDBName());
			mlog.info("使用 MySQL Database 為測試資料庫");
		} else {
			mSQLType = mLOCALDB;
			mlog.info("使用 Local Database 為測試資料庫");
		}
	}

	public void exe() {
		if (mDb != null) {
			try {
				mDb.doSQL(get_clean_Tables_of_MySQL_Instruction());
			} catch (MySQLSyntaxErrorException e) {
				mlog.debug("class: InitialSQL, method: exe, MySQLSyntaxErrorException: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				mlog.debug("class: InitialSQL, method: exe, Exception: " + e.toString());
				e.printStackTrace();
			} finally {
				mDb.close();
				mDb = null;
				mlog.info("[SQL] initial by connection Success");
			}
		} else if (mSQLType != mLOCALDB) {
			Runtime rt = Runtime.getRuntime();
			String cmdStr = "mysql -u" + mAppServID + " -p" + mAppServPWD + " " + mConfig.getDBName();
			Process p = null;
			try {
				p = rt.exec(cmdStr);
				reDirFileToProcess(p.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
				mlog.debug("class: InitialSQL, method: exe, IOException: " + e.toString());
			}
			mlog.info("[SQL] initial by local file Success");
		}
	}

	private List<String> get_clean_Tables_of_MySQL_Instruction() {
		List<String> sqlIns = new ArrayList<String>();

		// clean DoD
		sqlIns.add("TRUNCATE `buildresult`");
		sqlIns.add("TRUNCATE `commit_log`");
		sqlIns.add("TRUNCATE `commit_story_relation`");
		sqlIns.add("TRUNCATE `query`");

		// clean ezTrack
		sqlIns.add("TRUNCATE `eztrack_combofield`");
		sqlIns.add("TRUNCATE `eztrack_issuerelation`");
		sqlIns.add("TRUNCATE `eztrack_issuetype`");
		sqlIns.add("TRUNCATE `eztrack_report`");
		sqlIns.add("TRUNCATE `eztrack_typefield`");
		sqlIns.add("TRUNCATE `eztrack_typefieldvalue`");

		// clean ezScrum
		sqlIns.add("TRUNCATE `ezscrum_story_relation`;");
		sqlIns.add("TRUNCATE `ezscrum_tag_relation`;");
		sqlIns.add("TRUNCATE `ezscrum_tag_table`;");

		// clean Mantis
		sqlIns.add("TRUNCATE `mantis_bugnote_table`;");
		sqlIns.add("TRUNCATE `mantis_bugnote_text_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_file_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_history_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_monitor_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_relationship_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_tag_table`;");
		sqlIns.add("TRUNCATE `mantis_bug_text_table`;");
		sqlIns.add("TRUNCATE `mantis_custom_field_project_table`;");
		sqlIns.add("TRUNCATE `mantis_custom_field_string_table`;");
		sqlIns.add("TRUNCATE `mantis_custom_field_table`;");
		sqlIns.add("TRUNCATE `mantis_email_table`;");
		sqlIns.add("TRUNCATE `mantis_filters_table`;");
		sqlIns.add("TRUNCATE `mantis_news_table`;");
		sqlIns.add("TRUNCATE `mantis_project_category_table`;");
		sqlIns.add("TRUNCATE `mantis_project_file_table`;");
		sqlIns.add("TRUNCATE `mantis_project_hierarchy_table`;");
		sqlIns.add("TRUNCATE `mantis_project_table`;");
		sqlIns.add("TRUNCATE `mantis_project_user_list_table`;");
		sqlIns.add("TRUNCATE `mantis_project_version_table`;");
		sqlIns.add("TRUNCATE `mantis_sponsorship_table`;");
		sqlIns.add("TRUNCATE `mantis_tag_table`;");
		sqlIns.add("TRUNCATE `mantis_user_pref_table`;");
		sqlIns.add("TRUNCATE `mantis_user_print_pref_table`;");
		sqlIns.add("TRUNCATE `mantis_user_profile_table`;");
		sqlIns.add("DELETE `mantis_user_table`;");

		// ezScrum v1.8 table
		sqlIns.add("TRUNCATE `account`;");
		sqlIns.add("TRUNCATE `project_role`;");
		sqlIns.add("TRUNCATE `scrum_role`;");
		sqlIns.add("TRUNCATE `project`;");
		sqlIns.add("TRUNCATE `sprint`;");
		sqlIns.add("TRUNCATE `story`;");
		sqlIns.add("TRUNCATE `task`;");
		sqlIns.add("TRUNCATE `history`;");
		sqlIns.add("TRUNCATE `issue_partner_relation`;");
		sqlIns.add("TRUNCATE `tag`;");
		sqlIns.add("TRUNCATE `story_tag_relation`;");
		sqlIns.add("TRUNCATE `release`;");
		sqlIns.add("TRUNCATE `retrospective`;");
		sqlIns.add("TRUNCATE `unplanned`;");
		sqlIns.add("TRUNCATE `attach_file`;");
		sqlIns.add("TRUNCATE `serial_number`;");
		sqlIns.add("TRUNCATE `system`;");
		// add default user: admin
		sqlIns.add("INSERT INTO `account` VALUES (1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599);");
		sqlIns.add("INSERT INTO `system` VALUES (1, 1);");
		return sqlIns;
	}

	private void reDirFileToProcess(OutputStream processOuput) {
		byte[] data = new byte[1024];

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		// 將檔案的內容寫入要執行程式的 OutputStream
		try {
			bis = new BufferedInputStream(new FileInputStream(mReDirFile));
			bos = new BufferedOutputStream(processOuput);
			int length = 0;
			while ((length = bis.read(data)) != -1) {
				bos.write(data, 0, length);
			}

			// 將緩衝區中的資料全部寫出
			bos.flush();
		} catch (IOException e) {
			mlog.debug("class: InitialSQL, method: reDirFileToProcess, IOException: " + e.toString());
		} finally {
			// 關閉串流
			CloseStreamUtil.close(bis);
			CloseStreamUtil.close(bos);
		}
	}
}
