package ntut.csie.ezScrum.web.control;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;

public class AccountHelperTest extends TestCase {
	private CreateProject mCP;
	private int mProjectCount = 1;
	private ProjectMapper mProjectMapper = null;
	private Configuration mConfig;
	private IUserSession mUserSession = null;
	private ProjectObject mProject = null;

	public AccountHelperTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 建構 helper
		mProjectMapper = new ProjectMapper();
		mUserSession = mConfig.getUserSession();
		mProject = mCP.getAllProjects().get(0);

		super.setUp();

		// release
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// release
		ini = null;
		mCP = null;
		mProjectMapper = null;
		mConfig = null;
	}

	public void testgetScrumWorkerList() {
		// create 4 accounts
		CreateAccount ca = new CreateAccount(4);
		ca.exe();

		List<String> accountsId = mProjectMapper
				.getProjectWorkersUsername(mProject.getId());
		assertEquals(0, accountsId.size());

		AddUserToRole autr = new AddUserToRole(this.mCP, ca);
		autr.setAccountIndex(0);
		autr.exe_Sh();
		autr.setAccountIndex(1);
		autr.exe_PO();
		autr.setAccountIndex(2);
		autr.exe_SM();
		autr.setAccountIndex(3);
		autr.exe_ST();

		ProjectObject project = mCP.getAllProjects().get(0);
		updatePermission(project, "Stakeholder", false); // 將 Stakeholder 角色設定成不能存取
													// TaskBoard
		updatePermission(project, "ProductOwner", false); // 將 PO 角色設定成不能存取 TaskBoard

		accountsId = this.mProjectMapper
				.getProjectWorkersUsername(this.mProject.getId());
		assertEquals(2, accountsId.size()); // 可以領取工作的角色剩下兩個
		assertTrue(accountsId.contains(ca.getAccount_ID(3)));
		assertTrue(accountsId.contains(ca.getAccount_ID(4)));

		updatePermission(project, "ProductOwner", true); // 將 PO 角色設定成能存取 TaskBoard
		accountsId = this.mProjectMapper
				.getProjectWorkersUsername(this.mProject.getId());
		assertEquals(3, accountsId.size()); // 可以領取工作的角色剩下四個
		assertTrue(accountsId.contains(ca.getAccount_ID(2)));
		assertTrue(accountsId.contains(ca.getAccount_ID(3)));
		assertTrue(accountsId.contains(ca.getAccount_ID(4)));
	}

	private void updatePermission(ProjectObject project, String role,
			boolean accessTaskBoard) {
		List<String> permissionsList = new LinkedList<String>();
		permissionsList.add(ScrumEnum.ACCESS_PRODUCTBACKLOG);
		permissionsList.add(ScrumEnum.ACCESS_RELEASEPLAN);
		permissionsList.add(ScrumEnum.ACCESS_SPRINTPLAN);
		permissionsList.add(ScrumEnum.ACCESS_SPRINTBACKLOG);

		if (accessTaskBoard) {
			permissionsList.add(ScrumEnum.ACCESS_TASKBOARD);
		}

		permissionsList.add(ScrumEnum.ACCESS_UNPLANNED);
		permissionsList.add(ScrumEnum.ACCESS_RETROSPECTIVE);
		permissionsList.add(ScrumEnum.ACCESS_REPORT);
		permissionsList.add(ScrumEnum.ACCESS_EDITPROJECT);

		ScrumRole scrumrole = new ScrumRole(project.getName(), role);
		scrumrole = setAttribute(scrumrole, permissionsList);
		ScrumRoleMapper scrumRoleMapper = new ScrumRoleMapper();
		try {
			scrumRoleMapper.updateScrumRole(project.getId(), scrumrole);
		} catch (Exception e) {
			System.out
					.println("class: AccountHelperTest, method: updatePermission, exception: "
							+ e.toString());
			e.printStackTrace();
		}
	}

	private ScrumRole setAttribute(ScrumRole role, List<String> attributeList) {
		for (String attribute : attributeList) {
			if (attribute.equals(ScrumEnum.ACCESS_PRODUCTBACKLOG))
				role.setAccessProductBacklog(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_RELEASEPLAN))
				role.setAccessReleasePlan(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_SPRINTPLAN))
				role.setAccessSprintPlan(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_SPRINTBACKLOG))
				role.setAccessSprintBacklog(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_TASKBOARD))
				role.setAccessTaskBoard(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_UNPLANNED))
				role.setAccessUnplannedItem(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_RETROSPECTIVE))
				role.setAccessRetrospective(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_REPORT))
				role.setReadReport(Boolean.TRUE);
			else if (attribute.equals(ScrumEnum.ACCESS_EDITPROJECT))
				role.setEditProject(Boolean.TRUE);
		}
		return role;
	}
}
