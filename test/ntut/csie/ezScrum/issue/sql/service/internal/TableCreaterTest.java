package ntut.csie.ezScrum.issue.sql.service.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZSCRUM_STORY_RELATION_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZSCRUM_TAG_RELATION_TABLE;
import ntut.csie.ezScrum.issue.sql.service.internal.TableCreater.EZSCRUM_TAG_TABLE;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TableCreaterTest {
	private ISQLControl mISQLControl;
	private CreateProject mCP;
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create service control info.
		MantisService Service = new MantisService(mConfig);
		mISQLControl = Service.getControl();
		mISQLControl.setUser(mConfig.getDBAccount());
		mISQLControl.setPassword(mConfig.getDBPassword());
		mISQLControl.connect();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											
		
		if (mISQLControl != null) {
			try {
				mISQLControl.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();	
		
		mConfig.setTestMode(false);
		mConfig.save();
	
		
		// release resource
		mISQLControl = null;
		mCP = null;
		mConfig = null;
	}

	// test ezScrum
	@Test
	public void testcreateEzScrumTables() throws Exception {
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
		TableCreater.createEzScrumTables(tables, mISQLControl);
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
	
	// get now all tables
	private ArrayList<String> getTableList() throws SQLException {
		ArrayList<String> tables = new ArrayList<String>();
		
		ResultSet rs = mISQLControl.executeQuery("show tables");
		while( (rs != null) && rs.next()) {
			tables.add(rs.getString(1));
		}
		
		return tables;
	}
	
	// drop the table
	private void DropTable(String TableName) {
		String ins = "DROP TABLE `" + TableName + "`";
		mISQLControl.execute(ins);
	}
	
	// check column of table exist or not
	private boolean assertColumnExist(String TableName, String ColumnName) throws SQLException {
		String Ins = "SELECT COUNT(`" + ColumnName + "`) FROM `" + TableName + "`";
		ResultSet rs = mISQLControl.executeQuery(Ins);
		
		if (rs.next()) {
			return rs.getBoolean(1);
		}
		
		return false;
	}
}