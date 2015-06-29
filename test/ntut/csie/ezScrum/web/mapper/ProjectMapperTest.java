package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

public class ProjectMapperTest{
	private long mProjectId = 0;
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private ProjectMapper mProjectMapper;

	@Before
    public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();// 初始化 SQL
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
		mProjectMapper = new ProjectMapper();
		
		// 建立測試資料
		createTestData();
    }

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		ini = null;
		mConfig = null;
		mControl = null;
		mProjectMapper = null;
    }
	
	@Test
	public void testCreateProject() throws SQLException{
		// project data
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.name = "TEST_PROJECT_NAME_1";
		projectInfo.displayName = "TEST_PROJECT_DISPLAYNAME_1";
		projectInfo.common = "TEST_PROJECT_COMMON_1";
		projectInfo.manager = "TEST_PROJECT_MANAGER_1";
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
			ProjectObject project= ProjectDAO.getInstance().convertProject(resultSet);
			assertEquals(projectInfo.name, project.getName());
			assertEquals(projectInfo.displayName, project.getDisplayName());
			assertEquals(projectInfo.common, project.getComment());
			assertEquals(projectInfo.manager, project.getManager());
			assertEquals(projectInfo.attachFileSize, project.getAttachFileSize());
		}
	}
	
	@Test
	public void testUpdateProject() throws SQLException{
		// new project data
		ProjectInfo newProjectInfo = new ProjectInfo();
		newProjectInfo.displayName = "TEST_PROJECT_DISPLAYNAME_NEW";
		newProjectInfo.common = "TEST_PROJECT_COMMON_NEW";
		newProjectInfo.manager = "TEST_PROJECT_MANAGER_NEW";
		newProjectInfo.attachFileSize = 1024L;
		
		// update project
		mProjectMapper.updateProject(mProjectId, newProjectInfo);
		
		// get project using query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectEnum.ID, mProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		if (resultSet.next()) {
			ProjectObject project = ProjectDAO.getInstance().convertProject(resultSet);
			assertEquals(newProjectInfo.displayName, project.getDisplayName());
			assertEquals(newProjectInfo.common, project.getComment());
			assertEquals(newProjectInfo.manager, project.getManager());
			assertEquals(newProjectInfo.attachFileSize, project.getAttachFileSize());
		}
	}
	
	@Test
	public void testDeleteProject() throws SQLException{
		// delete project
		mProjectMapper.deleteProject(mProjectId);
		
		// get project using query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectEnum.ID, mProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		assertFalse(resultSet.first()); // 無此project
	}
	
	@Test
	public void testGetProjectScrumWorkersUsername_WithProductOwner(){
		// check status before test
		assertEquals(0, mProjectMapper.getProjectWorkersUsername(mProjectId).size());
		
		// test account data
		String accountName = "TEST_ACCOUNT_NAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String email = "TEST_ACCOUNT_EMAIL";
		String password = "TEST_ACCOUNT_PASSWORD";
		
		AccountObject account = new AccountObject(accountName);
		account.setNickName(nickName);
		account.setEmail(email);
		account.setPassword(password);
		account.save();
		
		// create project role - ProductOwner
		boolean createAcountResult = account.createProjectRole(mProjectId, RoleEnum.ProductOwner);
		assertTrue(createAcountResult);
		
		// get project scrum workers username
		ArrayList<String> usernames = mProjectMapper.getProjectWorkersUsername(mProjectId);
		assertEquals(0, usernames.size());
	}
	
	@Test
	public void testGetProjectScrumWorkersUsername_WithScrumMater(){
		// check status before test
		assertEquals(0, mProjectMapper.getProjectWorkersUsername(mProjectId).size());
		
		// test account data
		String accountName = "TEST_ACCOUNT_NAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String email = "TEST_ACCOUNT_EMAIL";
		String password = "TEST_ACCOUNT_PASSWORD";
		
		AccountObject account = new AccountObject(accountName);
		account.setNickName(nickName);
		account.setEmail(email);
		account.setPassword(password);
		account.save();
		
		// create project role - ScrumMaster
		boolean createAcountResult = account.createProjectRole(mProjectId, RoleEnum.ScrumMaster);
		assertTrue(createAcountResult);
		
		// get project scrum workers username
		ArrayList<String> usernames = mProjectMapper.getProjectWorkersUsername(mProjectId);
		assertEquals(1, usernames.size());
		assertEquals(accountName, usernames.get(0));
	}
	
	@Test
	public void testGetProjectScrumWorkersUsername_WithScrumTeam(){
		// check status before test
		assertEquals(0, mProjectMapper.getProjectWorkersUsername(mProjectId).size());
		
		// test account data
		String accountName = "TEST_ACCOUNT_NAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String email = "TEST_ACCOUNT_EMAIL";
		String password = "TEST_ACCOUNT_PASSWORD";
		
		AccountObject account = new AccountObject(accountName);
		account.setNickName(nickName);
		account.setEmail(email);
		account.setPassword(password);
		account.save();
		
		// create project role - ScrumTeam
		boolean createAcountResult = account.createProjectRole(mProjectId, RoleEnum.ScrumTeam);
		assertTrue(createAcountResult);
		
		// get project scrum workers username
		ArrayList<String> usernames = mProjectMapper.getProjectWorkersUsername(mProjectId);
		assertEquals(1, usernames.size());
		assertEquals(accountName, usernames.get(0));
	}
	
	/**
	 * 建立測試專案資料
	 */
	private void createTestData(){
		// project data
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.name = "TEST_PROJECT_NAME";
		projectInfo.displayName = "TEST_PROJECT_DISPLAYNAME";
		projectInfo.common = "TEST_PROJECT_COMMON";
		projectInfo.manager = "TEST_PROJECT_MANAGER";
		projectInfo.attachFileSize = 2048L;

		ProjectObject project = new ProjectObject(projectInfo.name);
		project
			.setDisplayName(projectInfo.displayName)
			.setComment(projectInfo.common)
			.setManager(projectInfo.manager)
			.setAttachFileSize(projectInfo.attachFileSize)
			.save();
		project.reload();
		
		// 新建 project，也把 serial number 建起來
		mProjectId = project.getId();
		SerialNumberDAO serialnumberDAO = SerialNumberDAO.getInstance();
		serialnumberDAO.create(new SerialNumberObject(mProjectId, 0, 0, 0, 0, 0, 0));
	}
}
