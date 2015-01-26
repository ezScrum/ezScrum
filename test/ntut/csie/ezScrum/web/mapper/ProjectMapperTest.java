package ntut.csie.ezScrum.web.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import junit.framework.TestCase;

public class ProjectMapperTest extends TestCase{
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private ProjectMapper mProjectMapper;

	@Override
    protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();// 初始化 SQL
		
		mControl = new MySQLControl(mConfig);
		mControl.connection();
		mProjectMapper = new ProjectMapper();
		ini = null;
		super.setUp();
    }

	@Override
    protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		ini = null;
		mConfig = null;
		mControl = null;
		mProjectMapper = null;
		
		super.tearDown();
    }
	
	public void testCreateProject() throws SQLException{
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.name = "TEST_PROJECT_NAME";
		projectInfo.displayName = "TEST_PROJECT_DISPLAYNAME";
		projectInfo.common = "TEST_PROJECT_COMMON";
		projectInfo.manager = "TEST_PROJECT_MANAGER";
		projectInfo.attachFileSize = 2048L;
		
		// create project
		long projectId = mProjectMapper.createProject(projectInfo.name, projectInfo);
		assertTrue(projectId > 0);
		
		// get project using query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectEnum.ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		if (resultSet.next()) {
			ProjectObject project= ProjectDAO.getInstance().convert(resultSet);
			assertEquals(projectInfo.name, project.getName());
			assertEquals(projectInfo.displayName, project.getDisplayName());
			assertEquals(projectInfo.common, project.getComment());
			assertEquals(projectInfo.manager, project.getManager());
			assertEquals(projectInfo.attachFileSize, project.getAttachFileSize());
		}
	}
}
