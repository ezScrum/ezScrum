package ntut.csie.ezScrum.issue.sql.service.internal;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;

public class TableCreater {

	public static boolean isAllTableExist(ISQLControl checker) {
		boolean exist = true;
		List<String> nameList = new ArrayList<String>();

		checker.connect();
		try {
			ResultSet result = checker.executeQuery("SHOW TABLES");
			while (result.next()) {
				nameList.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//資料表格如果沒有到44個表示Table有缺少
		if(nameList.size() < 43)
		{
			exist = false;
		}
		/*-----------------------------------------------------------
		 *	檢查Table是否存在
		-------------------------------------------------------------*/
//		createEzScrumTables(nameList, checker);
//		createEzTrackTables(nameList, checker);
//		createEzKanbanTables(nameList, checker);
//		createDoDTables(nameList, checker);

		checker.close();
		return exist;
	}

	/************************************************************
	 * 執行SQL Script
	 *************************************************************/
	public static void importSQL(Connection conn, InputStream in) throws SQLException {
		Scanner s = new Scanner(in,"UTF-8");
		s.useDelimiter("(;(\r)?\n)|(--\n)");
		Statement st = null;
		try {
			st = conn.createStatement();
			while (s.hasNext()) {
				String line = s.next();
				if (line.startsWith("/*!") && line.endsWith("*/")) {
					int i = line.indexOf(' ');
					line = line.substring(i + 1, line.length() - " */".length());
				}

				if (line.trim().length() > 0) {
					st.execute(line);
				}
			}
		} finally {
			if (st != null)
				st.close();
		}
	}

	/*-----------------------------------------------------------
	 *	產生Table的SQL指令
	-------------------------------------------------------------*/
	public static abstract class Tables {
		private String TableName = "";
		private List<String> columns = null;

		public Tables(String TableName) {
			this.columns = new ArrayList<String>();
			this.TableName = TableName;

			addColumns();
		}

		public void addColumn(String column) {
			this.columns.add(column);
		}

		public String getTableName() {
			return this.TableName;
		}

		public List<String> getColumns() {
			return new ArrayList<String>(this.columns);
		}

		public abstract void addColumns();

		public abstract String getCreatTableInstruction();
	}

	// =================== ezScrum ========================
	public static class EZSCRUM_STORY_RELATION_TABLE extends Tables {
		public EZSCRUM_STORY_RELATION_TABLE() {
			super(ITSEnum.EZSCRUM_STORY_RELATION);
		}

		@Override
		public void addColumns() {
			this.addColumn("id");
			this.addColumn("storyID");
			this.addColumn("projectID");
			this.addColumn("releaseID");
			this.addColumn("sprintID");
			// UnitTest 需要這兩個欄位,為何會被去掉?
			this.addColumn("estimation");
			this.addColumn("importance");
			//
			this.addColumn("updateDate");
		}

		@Override
		public String getCreatTableInstruction() {
			// UnitTest 需要 estimation & importance 這兩個欄位,為何會被去掉?
			String Ins = "CREATE TABLE `" + this.getTableName() + "` (" + "`id` int(8) NOT NULL auto_increment,"
					+ "`storyID` int(10) unsigned NOT NULL," + "`projectID` int(10) unsigned NOT NULL,"
					+ "`releaseID` int(10) default NULL," + "`sprintID` int(10) default NULL,"
					+ "`estimation` int(8) default NULL," + "`importance` int(8) default NULL,"
					+ "`updateDate` timestamp NULL default CURRENT_TIMESTAMP," + "PRIMARY KEY  (`id`),"
					+ "KEY `updateDate` (`sprintID`,`projectID`,`storyID`,`updateDate`)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	public static class EZSCRUM_TAG_RELATION_TABLE extends Tables {
		public EZSCRUM_TAG_RELATION_TABLE() {
			super(ITSEnum.EZSCRUM_TAG_RELATION);
		}

		@Override
		public void addColumns() {
			this.addColumn("tag_id");
			this.addColumn("story_id");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` (" + "`tag_id` int(10) NOT NULL,"
					+ "`story_id` int(10) NOT NULL," + "KEY `tag_id` (`tag_id`)," + "KEY `story_id` (`story_id`)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	public static class EZSCRUM_TAG_TABLE extends Tables {
		public EZSCRUM_TAG_TABLE() {
			super(ITSEnum.EZSCRUM_TAG_TABLE);
		}

		@Override
		public void addColumns() {
			this.addColumn("id");
			this.addColumn("project_id");
			this.addColumn("name");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment," + "`project_id` int(10) NOT NULL,"
					+ "`name` varchar(100) NOT NULL," + " CONSTRAINT pk_tag_proejct PRIMARY KEY (id, project_id)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	// =================== ezScrum ========================

	// =================== ezKanban ========================
	public static class EZKANBAN_STATISPRDER_TABLE extends Tables {
		public EZKANBAN_STATISPRDER_TABLE() {
			super(ITSEnum.EZKANBAN_STATUSORDER);
		}

		@Override
		public void addColumns() {
			this.addColumn("issueID");
			this.addColumn("order");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` (" + "`issueID` int(10) NOT NULL default '0',"
					+ "`order` tinyint(3) NOT NULL default '0'," + "KEY `issueID` (`issueID`),"
					+ "KEY `order` (`order`)," + "KEY `order_2` (`order`)," + "KEY `order_3` (`order`)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	// =================== ezTrack ========================
	public static class EZTARCK_COMBOFIELD_TABLE extends Tables {
		public EZTARCK_COMBOFIELD_TABLE() {
			super(ITSEnum.EZTRACK_COMBOFIELD);
		}

		@Override
		public void addColumns() {
			this.addColumn("TypeFieldID");
			this.addColumn("ComboName");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` (" + "`TypeFieldID` int(10) unsigned NOT NULL,"
					+ "`ComboName` varchar(40) NOT NULL default ''," + "KEY `TypeFieldID` (`TypeFieldID`)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	public static class EZTRACK_ISSUERELATION_TABLE extends Tables {
		public EZTRACK_ISSUERELATION_TABLE() {
			super(ITSEnum.EZTRACK_ISSUERELATION);
		}

		@Override
		public void addColumns() {
			this.addColumn("IssueID_src");
			this.addColumn("IssueID_des");
			this.addColumn("Type");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` (" + "`IssueID_src` int(10) unsigned NOT NULL,"
					+ "`IssueID_des` int(10) unsigned NOT NULL,"
					+ "`Type` int(2) NOT NULL default '1' COMMENT 'Relation的關係'"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	public static class EZTRACK_ISSUETYPE_TABLE extends Tables {
		public EZTRACK_ISSUETYPE_TABLE() {
			super(ITSEnum.EZTRACK_ISSUETYPE);
		}

		@Override
		public void addColumns() {
			this.addColumn("IssueTypeID");
			this.addColumn("ProjectID");
			this.addColumn("IssueTypeName");
			this.addColumn("IsPublic");
			this.addColumn("IsKanban");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` ("
					+ "`IssueTypeID` int(10) unsigned NOT NULL auto_increment,"
					+ "`ProjectID` int(10) unsigned NOT NULL default '0',"
					+ "`IssueTypeName` varchar(20) NOT NULL default '',"
					+ "`IsPublic` tinyint(2) NOT NULL default '0'," + "`IsKanban` tinyint(2) NOT NULL default '0',"
					+ "PRIMARY KEY  (`IssueTypeID`)," + "KEY `ProjectID` (`ProjectID`)"
					+ ") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=70 ";

			return Ins;
		}
	}

	public static class EZTRACK_REPORT_TABLE extends Tables {
		public EZTRACK_REPORT_TABLE() {
			super(ITSEnum.EZTRACK_REPORT);
		}

		@Override
		public void addColumns() {
			this.addColumn("ReportID");
			this.addColumn("ProjectID");
			this.addColumn("IssueTypeID");
			this.addColumn("ReportDescription");
			this.addColumn("ReporterName");
			this.addColumn("ReporterEmail");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` ("
					+ "`ReportID` int(10) unsigned NOT NULL auto_increment,"
					+ "`ProjectID` int(10) unsigned NOT NULL default '0',"
					+ "`IssueTypeID` int(10) unsigned NOT NULL default '0'," + "`ReportDescription` longtext NOT NULL,"
					+ "`ReporterName` varchar(50) NOT NULL default '',"
					+ "`ReporterEmail` varchar(80) NOT NULL default ''," + " PRIMARY KEY  (`ReportID`)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ";

			return Ins;
		}
	}

	public static class EZTRACK_TYPEFIELD_TABLE extends Tables {
		public EZTRACK_TYPEFIELD_TABLE() {
			super(ITSEnum.EZTRACK_TYPEFIELD);
		}

		@Override
		public void addColumns() {
			this.addColumn("TypeFieldID");
			this.addColumn("IssueTypeID");
			this.addColumn("TypeFieldName");
			this.addColumn("TypeFieldCategory");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` ("
					+ "`TypeFieldID` int(10) unsigned NOT NULL auto_increment,"
					+ "`IssueTypeID` int(10) unsigned NOT NULL default '0',"
					+ "`TypeFieldName` varchar(20) NOT NULL default '',"
					+ "`TypeFieldCategory` varchar(80) NOT NULL default ''," + "PRIMARY KEY  (`TypeFieldID`),"
					+ "KEY `IssueTypeID` (`IssueTypeID`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=193 ";

			return Ins;
		}
	}

	public static class EZTRACK_TYPEFIELDVALUE_TABLE extends Tables {
		public EZTRACK_TYPEFIELDVALUE_TABLE() {
			super(ITSEnum.EZTRACK_TYPEFIELDVALUE);
		}

		@Override
		public void addColumns() {
			this.addColumn("IssueID");
			this.addColumn("TypeFieldID");
			this.addColumn("FieldValue");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE `" + this.getTableName() + "` ("
					+ "`IssueID` int(10) unsigned NOT NULL default '0',"
					+ "`TypeFieldID` int(10) unsigned NOT NULL default '0'," + "`FieldValue` text NOT NULL,"
					+ "KEY `IssueID` (`IssueID`)," + "KEY `TypeFieldID` (`TypeFieldID`)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

			return Ins;
		}
	}

	// =================== ezTrack ========================

	// =================== DOD tool od ks2024 ===================
	// Table: COMMIT_LOG, store the commit information
	public static class COMMIT_LOG_TABLE extends Tables {
		public COMMIT_LOG_TABLE() {
			super(ITSEnum.DOD_COMMIT_LOG);
		}

		@Override
		public void addColumns() {
			this.addColumn("ID");
			this.addColumn("AUTHOR");
			this.addColumn("CHANGEDFILES");
			this.addColumn("LOG");
			this.addColumn("REVISION");
			this.addColumn("COMMITTIME");
			this.addColumn("PROJECT_ID");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` ("
					+ "`ID` int(11) NOT NULL auto_increment," + "`AUTHOR` varchar(255) default NULL," // commit
					// author
					+ "`CHANGEDFILES` text," // commit files path
					+ "`LOG` text," // commit log
					+ "`REVISION` int(11) default NULL," // commit version
					+ "`COMMITTIME` timestamp NOT NULL default CURRENT_TIMESTAMP," // commit
					// time
					+ "`PROJECT_ID` int(11) default NULL," // commit project_id
					// mapping to
					// mantis_project_table
					+ "PRIMARY KEY (`ID`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0;";

			return Ins;
		}
	}

	// Table: COMMIT_STORY_RELATION, store the commit information mapping to
	// relationship of issue
	public static class COMMIT_STORY_RELATION_TABLE extends Tables {
		public COMMIT_STORY_RELATION_TABLE() {
			super(ITSEnum.DOD_COMMIT_STORY_RELATION);
		}

		@Override
		public void addColumns() {
			this.addColumn("COMMITID");
			this.addColumn("ISSUEID");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` ("
					+ "`COMMITID` int(11) default NULL," // commit id mapping to
					// COMMIT_LOG
					+ "`ISSUEID` int(11) default NULL" // issue id mapping to
					// mantis_bug_table
					+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";

			return Ins;
		}
	}

	// Table: QUERY, store customize SQL query string
	public static class QUERY_TABLE extends Tables {
		public QUERY_TABLE() {
			super(ITSEnum.DOD_QUERY);
		}

		@Override
		public void addColumns() {
			this.addColumn("ID");
			this.addColumn("PROJECT_ID");
			this.addColumn("USER_ID");
			this.addColumn("NAME");
			this.addColumn("PROFILE");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` ("
					+ "`ID` int(11) NOT NULL auto_increment," + "`PROJECT_ID` int(11) default NULL," // commit
					// project_id
					// mapping
					// to
					// mantis_project_table
					+ "`USER_ID` int(10)," // ezScrum user id mapping to
					// mantis_user_table
					+ "`NAME` varchar(255) NOT NULL default ''," // SQL query
					// string to
					// user
					+ "`PROFILE` text," // SQL query string to instruction
					+ "PRIMARY KEY (`ID`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";

			return Ins;
		}
	}

	// Table: BUILDRESULT, store the C.C build result
	public static class BUILDRESULT_TABLE extends Tables {
		public BUILDRESULT_TABLE() {
			super(ITSEnum.DOD_BUILDRESULT);
		}

		@Override
		public void addColumns() {
			this.addColumn("ID");
			this.addColumn("PROJECT_ID");
			this.addColumn("REVISION");
			this.addColumn("LABEL");
			this.addColumn("BUILDRESULT");
			this.addColumn("BUILDMESSAGE");
			this.addColumn("HASTESTRESULT");
			this.addColumn("TESTRESULT");
			this.addColumn("TESTMESSAGE");
			this.addColumn("HASCOVERAGERESULT");
			this.addColumn("CLASSCOVERAGE");
			this.addColumn("METHODCOVERAGE");
			this.addColumn("BLOCKCOVERAGE");
			this.addColumn("LINECOVERAGE");
			this.addColumn("BUILDTIME");
		}

		@Override
		public String getCreatTableInstruction() {
			String Ins = "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` ("
					+ "`ID` int(11) NOT NULL auto_increment," + "`PROJECT_ID` int(11) default NULL," // commit
					// project_id
					// mapping
					// to
					// mantis_project_table
					+ "`REVISION` int(11) default NULL," // commit version, not
					// use now
					+ "`LABEL` varchar(255) NOT NULL default ''," // LABEL,
					// CruiseControl
					// 建置編號, not
					// use now
					+ "`BUILDRESULT` BOOL default '0'," // C.I build result
					+ "`BUILDMESSAGE` text ," // C.I build return message
					+ "`HASTESTRESULT` BOOL default '0'," // C.I do the UnitTest
					+ "`TESTRESULT` BOOL default '0'," // C.I has the UnitTest
					// result
					+ "`TESTMESSAGE` text ," // C.I UnitTest return message
					+ "`HASCOVERAGERESULT` BOOL default '0'," // C.I do the
					// TestCoverage
					+ "`CLASSCOVERAGE` varchar(255) NOT NULL default ''," // C.I
					// one
					// of
					// TestCoverage
					+ "`METHODCOVERAGE` varchar(255) NOT NULL default ''," // C.I
					// one
					// of
					// TestCoverage
					+ "`BLOCKCOVERAGE` varchar(255) NOT NULL default ''," // C.I
					// one
					// of
					// TestCoverage
					+ "`LINECOVERAGE` varchar(255) NOT NULL default ''," // C.I
					// one
					// of
					// TestCoverage
					+ "`BUILDTIME` timestamp NOT NULL default CURRENT_TIMESTAMP," // C.I
					// build
					// time
					+ "PRIMARY KEY (`ID`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

			return Ins;
		}
	}

	// =================== DOD tool od ks2024 ===================

	// ezScrum tables
	public static void createEzScrumTables(List<String> nameList, ISQLControl checker) {
		// ezscrum_relation_story
		if (!nameList.contains(ITSEnum.EZSCRUM_STORY_RELATION)) {
			checker.execute(new EZSCRUM_STORY_RELATION_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZSCRUM_STORY_RELATION);
		}

		// ezscrum_tag_relation
		if (!nameList.contains(ITSEnum.EZSCRUM_TAG_RELATION)) {
			checker.execute(new EZSCRUM_TAG_RELATION_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZSCRUM_TAG_RELATION);
		}

		// ezscrum_tag_table
		if (!nameList.contains(ITSEnum.EZSCRUM_TAG_TABLE)) {
			checker.execute(new EZSCRUM_TAG_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZSCRUM_TAG_TABLE);
		}
	}

	// ezTrack tables
	public static void createEzTrackTables(List<String> nameList, ISQLControl checker) {
		// eztrack_combofield
		if (!nameList.contains(ITSEnum.EZTRACK_COMBOFIELD)) {
			checker.execute(new EZTARCK_COMBOFIELD_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZTRACK_COMBOFIELD);
		}

		// eztrack_issuerelation
		if (!nameList.contains(ITSEnum.EZTRACK_ISSUERELATION)) {
			checker.execute(new EZTRACK_ISSUERELATION_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZTRACK_ISSUERELATION);
		}

		// eztrack_issuetype
		if (!nameList.contains(ITSEnum.EZTRACK_ISSUETYPE)) {
			checker.execute(new EZTRACK_ISSUETYPE_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZTRACK_ISSUETYPE);
		}

		// eztrack_report
		if (!nameList.contains(ITSEnum.EZTRACK_REPORT)) {
			checker.execute(new EZTRACK_REPORT_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZTRACK_REPORT);
		}

		// eztrack_typefield
		if (!nameList.contains(ITSEnum.EZTRACK_TYPEFIELD)) {
			checker.execute(new EZTRACK_TYPEFIELD_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZTRACK_TYPEFIELD);
		}

		// eztrack_typefieldvalue
		if (!nameList.contains(ITSEnum.EZTRACK_TYPEFIELDVALUE)) {
			checker.execute(new EZTRACK_TYPEFIELDVALUE_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZTRACK_TYPEFIELDVALUE);
		}
	}

	// ezKanban tables
	public static void createEzKanbanTables(List<String> nameList, ISQLControl checker) {
		// ezkanban_statusorder
		if (!nameList.contains(ITSEnum.EZKANBAN_STATUSORDER)) {
			checker.execute(new EZKANBAN_STATISPRDER_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.EZKANBAN_STATUSORDER);
		}
	}

	// DoD tables
	public static void createDoDTables(List<String> nameList, ISQLControl checker) {
		// DOD_COMMIT_LOG
		if (!nameList.contains(ITSEnum.DOD_COMMIT_LOG)) {
			checker.execute(new COMMIT_LOG_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.DOD_COMMIT_LOG);
		}

		// DOD_COMMIT_STORY_RELATION
		if (!nameList.contains(ITSEnum.DOD_COMMIT_STORY_RELATION)) {
			checker.execute(new COMMIT_STORY_RELATION_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.DOD_COMMIT_STORY_RELATION);
		}

		// DOD_QUERY
		if (!nameList.contains(ITSEnum.DOD_QUERY)) {
			checker.execute(new QUERY_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.DOD_QUERY);
		}

		// DOD_BUILDRESULT
		if (!nameList.contains(ITSEnum.DOD_BUILDRESULT)) {
			checker.execute(new BUILDRESULT_TABLE().getCreatTableInstruction());
			System.out.println("System Create new Table: " + ITSEnum.DOD_BUILDRESULT);
		}
	}
}
