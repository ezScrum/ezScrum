package ntut.csie.ezScrum.issue.sql.service.internal;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.BUILDRESULT_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.COMMIT_LOG_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.COMMIT_STORY_RELATION_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZSCRUM_STORY_RELATION_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZSCRUM_TAG_RELATION_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZSCRUM_TAG_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZTARCK_COMBOFIELD_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZTRACK_ISSUERELATION_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZTRACK_ISSUETYPE_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZTRACK_REPORT_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZTRACK_TYPEFIELDVALUE_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZTRACK_TYPEFIELD_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.QUERY_TABLE;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.resource.core.IProject;

public class TableCreaterTest extends TestCase {
	private TableCreater tc = null;
	private ISQLControl control;
	private CreateProject CP;
	private Configuration configuration;
	
	public TableCreaterTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		configuration = new Configuration(new UserSession(AccountObject.get("admin")));
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		// 新增Project
		this.CP = new CreateProject(1);
		this.CP.exeCreate();
		IProject project = this.CP.getProjectList().get(0);

		// create service control info.
		MantisService Service = new MantisService(configuration);
		this.control = Service.getControl();
		this.control.setUser(configuration.getDBAccount());
		this.control.setPassword(configuration.getDBPassword());
		this.control.connection();
		
		this.tc = new TableCreater();
	
		super.setUp();
		
		// ============= release ==============
		ini = null;
		project = null;
		Service = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		if (this.control != null) {
			try {
				this.control.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
		
		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project();					// 刪除測試檔案	
		
		configuration.setTestMode(false);
		configuration.save();
	
		
		// ============= release ==============
		ini = null;
		copyProject = null;
		this.tc = null;
		this.control.close();
		this.control = null;
		this.CP = null;
		configuration = null;
	}

	// test ezScrum
	public void testcreateEzScrumTables() throws Exception{
		List<String> tables = new ArrayList<String>();
		
		// drop tables
		DropTable(ITSEnum.EZSCRUM_STORY_RELATION);
		DropTable(ITSEnum.EZSCRUM_TAG_RELATION);
		DropTable(ITSEnum.EZSCRUM_TAG_TABLE);

		tables = getTableList();
		// assert tables not exist
		assertFalse(tables.contains(ITSEnum.EZSCRUM_STORY_RELATION));
		assertFalse(tables.contains(ITSEnum.EZSCRUM_TAG_RELATION));
		assertFalse(tables.contains(ITSEnum.EZSCRUM_TAG_TABLE));
		
		// create table
		this.tc.createEzScrumTables(tables, this.control);
		tables = getTableList();
		// assert tables exist
		assertTrue(tables.contains(ITSEnum.EZSCRUM_STORY_RELATION));
		assertTrue(tables.contains(ITSEnum.EZSCRUM_TAG_RELATION));
		assertTrue(tables.contains(ITSEnum.EZSCRUM_TAG_TABLE));
		
		// assert column exist
		EZSCRUM_STORY_RELATION_TABLE STORY_RELATION = new EZSCRUM_STORY_RELATION_TABLE();
		for (String col : STORY_RELATION.getColumns()) {
			assertNotNull(assertColumnExist(STORY_RELATION.getTableName(), col));
			assertFalse(assertColumnExist(STORY_RELATION.getTableName(), col));
		}
		
		EZSCRUM_TAG_RELATION_TABLE TAG_RELATION = new EZSCRUM_TAG_RELATION_TABLE();
		for (String col : TAG_RELATION.getColumns()) {
			assertNotNull(assertColumnExist(TAG_RELATION.getTableName(), col));
			assertFalse(assertColumnExist(TAG_RELATION.getTableName(), col));
		}
		
		EZSCRUM_TAG_TABLE TAG = new EZSCRUM_TAG_TABLE();
		for (String col : TAG.getColumns()) {
			assertNotNull(assertColumnExist(TAG.getTableName(), col));
			assertFalse(assertColumnExist(TAG.getTableName(), col));
		}
	}
	
	// test ezTrack
	public void testcreateEzTrackTables() throws SQLException {
		List<String> tables = new ArrayList<String>();
		
		// drop tables
		DropTable(ITSEnum.EZTRACK_COMBOFIELD);
		DropTable(ITSEnum.EZTRACK_ISSUERELATION);
		DropTable(ITSEnum.EZTRACK_ISSUETYPE);
		DropTable(ITSEnum.EZTRACK_REPORT);
		DropTable(ITSEnum.EZTRACK_TYPEFIELD);
		DropTable(ITSEnum.EZTRACK_TYPEFIELDVALUE);
		tables = getTableList();
		// assert tables not exist
		assertFalse(tables.contains(ITSEnum.EZTRACK_COMBOFIELD));
		assertFalse(tables.contains(ITSEnum.EZTRACK_ISSUERELATION));
		assertFalse(tables.contains(ITSEnum.EZTRACK_ISSUETYPE));
		assertFalse(tables.contains(ITSEnum.EZTRACK_REPORT));
		assertFalse(tables.contains(ITSEnum.EZTRACK_TYPEFIELD));
		assertFalse(tables.contains(ITSEnum.EZTRACK_TYPEFIELDVALUE));

		
		// create table
		this.tc.createEzTrackTables(tables, this.control);
		tables = getTableList();
		// assert tables exist
		assertTrue(tables.contains(ITSEnum.EZTRACK_COMBOFIELD));
		assertTrue(tables.contains(ITSEnum.EZTRACK_ISSUERELATION));
		assertTrue(tables.contains(ITSEnum.EZTRACK_ISSUETYPE));
		assertTrue(tables.contains(ITSEnum.EZTRACK_REPORT));
		assertTrue(tables.contains(ITSEnum.EZTRACK_TYPEFIELD));
		assertTrue(tables.contains(ITSEnum.EZTRACK_TYPEFIELDVALUE));
		
		// assert column exist
		EZTARCK_COMBOFIELD_TABLE COMBOFIELD = new EZTARCK_COMBOFIELD_TABLE();
		for (String col : COMBOFIELD.getColumns()) {
			assertNotNull(assertColumnExist(COMBOFIELD.getTableName(), col));
			assertFalse(assertColumnExist(COMBOFIELD.getTableName(), col));
		}
		
		EZTRACK_ISSUERELATION_TABLE ISSUERELATION = new EZTRACK_ISSUERELATION_TABLE();
		for (String col : ISSUERELATION.getColumns()) {
			assertNotNull(assertColumnExist(ISSUERELATION.getTableName(), col));
			assertFalse(assertColumnExist(ISSUERELATION.getTableName(), col));
		}
		
		EZTRACK_ISSUETYPE_TABLE ISSUETYPE = new EZTRACK_ISSUETYPE_TABLE();
		for (String col : ISSUETYPE.getColumns()) {
			assertNotNull(assertColumnExist(ISSUETYPE.getTableName(), col));
			assertFalse(assertColumnExist(ISSUETYPE.getTableName(), col));
		}
		
		EZTRACK_REPORT_TABLE REPORT = new EZTRACK_REPORT_TABLE();
		for (String col : REPORT.getColumns()) {
			assertNotNull(assertColumnExist(REPORT.getTableName(), col));
			assertFalse(assertColumnExist(REPORT.getTableName(), col));
		}
		
		EZTRACK_TYPEFIELD_TABLE TYPEFIELD = new EZTRACK_TYPEFIELD_TABLE();
		for (String col : TYPEFIELD.getColumns()) {
			assertNotNull(assertColumnExist(TYPEFIELD.getTableName(), col));
			assertFalse(assertColumnExist(TYPEFIELD.getTableName(), col));
		}
		
		EZTRACK_TYPEFIELDVALUE_TABLE TYPEFIELDVALUE = new EZTRACK_TYPEFIELDVALUE_TABLE();
		for (String col : TYPEFIELDVALUE.getColumns()) {
			assertNotNull(assertColumnExist(TYPEFIELDVALUE.getTableName(), col));
			assertFalse(assertColumnExist(TYPEFIELDVALUE.getTableName(), col));
		}
	}
	
	
	// test DoD
	public void testcreateDoDTables() throws SQLException {
		List<String> tables = new ArrayList<String>();
		
		// drop tables
		DropTable(ITSEnum.DOD_BUILDRESULT);
		DropTable(ITSEnum.DOD_COMMIT_LOG);
		DropTable(ITSEnum.DOD_COMMIT_STORY_RELATION);
		DropTable(ITSEnum.DOD_QUERY);
		tables = getTableList();
		// assert tables not exist
		assertFalse(tables.contains(ITSEnum.DOD_BUILDRESULT));
		assertFalse(tables.contains(ITSEnum.DOD_COMMIT_LOG));
		assertFalse(tables.contains(ITSEnum.DOD_COMMIT_STORY_RELATION));
		assertFalse(tables.contains(ITSEnum.DOD_QUERY));
		
		// create table
		this.tc.createDoDTables(tables, this.control);
		tables = getTableList();
		// assert tables exist
		assertTrue(tables.contains(ITSEnum.DOD_BUILDRESULT));
		assertTrue(tables.contains(ITSEnum.DOD_COMMIT_LOG));
		assertTrue(tables.contains(ITSEnum.DOD_COMMIT_STORY_RELATION));
		assertTrue(tables.contains(ITSEnum.DOD_QUERY));
		
		// assert column exist
		COMMIT_LOG_TABLE LOG = new COMMIT_LOG_TABLE();
		for (String col : LOG.getColumns()) {
			assertNotNull(assertColumnExist(LOG.getTableName(), col));
			assertFalse(assertColumnExist(LOG.getTableName(), col));
		}
		
		COMMIT_STORY_RELATION_TABLE STORY_RELATION = new COMMIT_STORY_RELATION_TABLE();
		for (String col : STORY_RELATION.getColumns()) {
			assertNotNull(assertColumnExist(STORY_RELATION.getTableName(), col));
			assertFalse(assertColumnExist(STORY_RELATION.getTableName(), col));
		}
		
		QUERY_TABLE QUERY = new QUERY_TABLE();
		for (String col : QUERY.getColumns()) {
			assertNotNull(assertColumnExist(QUERY.getTableName(), col));
			assertFalse(assertColumnExist(QUERY.getTableName(), col));
		}
		
		BUILDRESULT_TABLE BUILDRESULT = new BUILDRESULT_TABLE();
		for (String col : BUILDRESULT.getColumns()) {
			assertNotNull(assertColumnExist(BUILDRESULT.getTableName(), col));
			assertFalse(assertColumnExist(BUILDRESULT.getTableName(), col));
		}
	}
	
	// get now all tables
	private ArrayList<String> getTableList() throws SQLException {
		ArrayList<String> tables = new ArrayList<String>();
		
		ResultSet rs = this.control.executeQuery("show tables");
		while( (rs != null) && rs.next()) {
			tables.add(rs.getString(1));
		}
		
		return tables;
	}
	
	// drop the table
	private void DropTable(String TableName) {
		String ins = "DROP TABLE `" + TableName + "`";
		this.control.execute(ins);
	}
	
	// check column of table exist or not
	private boolean assertColumnExist(String TableName, String ColumnName) throws SQLException {
		String Ins = "SELECT COUNT(`" + ColumnName + "`) FROM `" + TableName + "`";
		ResultSet rs = this.control.executeQuery(Ins);
		
		if (rs.next()) {
			return rs.getBoolean(1);
		}
		
		return false;
	}
}