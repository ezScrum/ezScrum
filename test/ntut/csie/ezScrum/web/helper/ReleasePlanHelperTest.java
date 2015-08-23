package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleasePlanHelperTest {
	private ReleasePlanHelper mReleasePlanHelper;
	private CreateProject mCP;
	private CreateRelease mCR;
	private int mReleaseCount = 3;
	private int mProjectCount = 1;
	private Configuration mConfig = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(mProjectCount); // 新增一個 Project
		mCP.exeCreate(); // 執行

		mCR = new CreateRelease(mReleaseCount, mCP); // 專案新增三個
		// Release
		mCR.exe(); // 執行
	}

	@After
	public void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		// release
		mReleasePlanHelper = null;
		projectManager = null;
		mCP = null;
		mCR = null;
		mConfig = null;
	}

	@Test
	public void testLoadReleasePlans() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		ReleaseObject[] ReleasePlans = mReleasePlanHelper.getReleaseArray();
		assertEquals(mReleaseCount, ReleasePlans.length);

		for (int i = 0; i < ReleasePlans.length; i++) {
			assertEquals((i + 1), ReleasePlans[i].getId());
			assertEquals(mCR.getDefault_RELEASE_NAME(i + 1), ReleasePlans[i].getName());
			assertEquals(mCR.getDefault_RELEASE_DESC(i + 1), ReleasePlans[i].getDescription());
		}
	}

	@Test
	public void testLoadReleasePlansList() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<ReleaseObject> ReleasePlans = mReleasePlanHelper.getReleases();
		assertEquals(mReleaseCount, ReleasePlans.size());

		for (int i = 0; i < ReleasePlans.size(); i++) {
			assertEquals(i + 1, ReleasePlans.get(i).getId());
			assertEquals(mCR.getDefault_RELEASE_NAME(i + 1), ReleasePlans.get(i).getName());
			assertEquals(mCR.getDefault_RELEASE_DESC(i + 1), ReleasePlans.get(i).getDescription());
		}
	}

	@Test
	public void testGetLastReleasePlanNumber() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<ReleaseObject> ReleasePlans = mReleasePlanHelper.getReleases();
		assertEquals(mReleaseCount, ReleasePlans.size());
		assertEquals(mReleaseCount, mReleasePlanHelper.getLastReleasePlanNumber());
	}

	@Test
	public void testDeleteReleasePlan() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<ReleaseObject> releases = mReleasePlanHelper.getReleases();
		assertEquals(mReleaseCount, releases.size());
		assertEquals(mReleaseCount, mReleasePlanHelper.getLastReleasePlanNumber());

		mReleasePlanHelper.deleteReleasePlan(releases.get(2).getId()); // 刪除 ID = 3

		assertEquals(mReleaseCount - 1, mReleasePlanHelper.getReleaseArray().length);
		assertEquals(mReleaseCount - 1, mReleasePlanHelper.getLastReleasePlanNumber());

		for (int i = 0; i < releases.size(); i++) {
			assertEquals(i + 1, releases.get(i).getId());
			assertEquals(mCR.getDefault_RELEASE_NAME(i + 1), releases.get(i).getName());
			assertEquals(mCR.getDefault_RELEASE_DESC(i + 1), releases.get(i).getDescription());
		}
	}

	@Test
	public void testEditReleasePlan() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<ReleaseObject> ReleasePlans = mReleasePlanHelper.getReleases();
		assertEquals(mReleaseCount, ReleasePlans.size());
		assertEquals(mReleaseCount, mReleasePlanHelper.getLastReleasePlanNumber());

		// 設定修改的 ReleasePlan 日期
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		Date NewStartDate = cal.getTime(); // set New Start Date
		cal.add(Calendar.DAY_OF_YEAR, 180);
		Date NewDueDate = cal.getTime(); // set New End Date

		// edit 判斷式的測試
		long lastId = mReleasePlanHelper.getLastReleasePlanNumber(); // 修改最後一筆資訊
		String NewName = "ReleaseName";
		String NewDesc = "ReleaseDesc";

		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.id = lastId;
		releaseInfo.name = NewName;
		releaseInfo.startDate = format.format(NewStartDate).toString();
		releaseInfo.dueDate = format.format(NewDueDate).toString();
		releaseInfo.description = NewDesc;
		mReleasePlanHelper.editReleasePlan(releaseInfo);

		ReleaseObject editRelease = mReleasePlanHelper.getReleasePlan(lastId);
		assertEquals(lastId, editRelease.getId());
		assertEquals(NewName, editRelease.getName());
		assertEquals(NewDesc, editRelease.getDescription());
		assertEquals(format.format(NewStartDate).toString(), editRelease.getStartDateString());
		assertEquals(format.format(NewDueDate).toString(), editRelease.getDueDateString());

		// save 判斷式的測試
		lastId++;
		releaseInfo = new ReleaseInfo();
		releaseInfo.id = lastId;
		releaseInfo.name = "ReleaseName-New";
		releaseInfo.startDate = format.format(NewStartDate).toString();
		releaseInfo.dueDate = format.format(NewDueDate).toString();
		releaseInfo.description = "ReleaseDesc-New";
		mReleasePlanHelper.createReleasePlan(releaseInfo);
		ReleaseObject saveRelease = mReleasePlanHelper.getReleasePlan(lastId);
		assertEquals(mCR.getReleaseCount() + 1, mReleasePlanHelper.getReleaseArray().length);
		assertEquals(lastId, saveRelease.getId());
		assertEquals("ReleaseName-New", saveRelease.getName());
		assertEquals("ReleaseDesc-New", saveRelease.getDescription());
		assertEquals(format.format(NewStartDate).toString(), saveRelease.getStartDateString());
		assertEquals(format.format(NewDueDate).toString(), saveRelease.getDueDateString());

		// default 除錯測試
		mReleasePlanHelper.editReleasePlan(releaseInfo);
		ReleaseObject otherRelease = mReleasePlanHelper.getReleasePlan(lastId);
		assertEquals(lastId, otherRelease.getId());
		assertEquals("ReleaseName-New", otherRelease.getName());
		assertEquals("ReleaseDesc-New", otherRelease.getDescription());
		assertEquals(format.format(NewStartDate).toString(), otherRelease.getStartDateString());
		assertEquals(format.format(NewDueDate).toString(), otherRelease.getDueDateString());
	}
	
	@Test
	public void testGetReleasePlansByIDs() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		// no release plan select
		String releasePlanIDs = "";
		List<ReleaseObject> plans = mReleasePlanHelper.getReleasePlansByIds(releasePlanIDs);
		assertEquals(0, plans.size());
		
		// select wrong releaseID
		releasePlanIDs = "4";
		plans = mReleasePlanHelper.getReleasePlansByIds(releasePlanIDs);
		assertEquals(null, plans.get(0));
		
		// select releaseID = 1,2,3
		releasePlanIDs = "1,2,3";
		plans = mReleasePlanHelper.getReleasePlansByIds(releasePlanIDs);
		assertEquals(3, plans.size());
	}
	
	@Test
	public void testGetSprintVelocityToJSon() throws Exception {
		/**
		 * no release plan select
		 */
		String releases = "";
		// 取得ReleasePlans
		ProjectObject project = mCP.getAllProjects().get(0);
		mReleasePlanHelper = new ReleasePlanHelper(project);
		List<ReleaseObject> releaseDescs = mReleasePlanHelper.getReleasePlansByIds(releases);
		// 取得SprintBacklog
		SprintBacklogHelper SBhelper = new SprintBacklogHelper(project);
		// assert no release plan string value
		String actualTest = mReleasePlanHelper.getSprintVelocityToJSon(releaseDescs, SBhelper);
		StringBuilder expectTest = new StringBuilder();
		expectTest.append("{")
				  .append("\"Sprints\":[],")
				  .append("\"Average\":\"\"")
				  .append("}");
		assertEquals(expectTest.toString(), actualTest);
		/**
		 * select releaseID = 1,2,3
		 */
		// create sprint
		CreateSprint CS = new CreateSprint(3, mCP);
		CS.exe();
		// create story to sprint
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, CS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe(); // 每個Sprint中新增2筆Story
		// 取得ReleasePlans
		releases = "1,2,3";
		releaseDescs = mReleasePlanHelper.getReleasePlansByIds(releases);
		// update SprintBacklog mapper info
		SBhelper = new SprintBacklogHelper(project);
		// assert release plan string value
		actualTest = mReleasePlanHelper.getSprintVelocityToJSon(releaseDescs, SBhelper);
		expectTest.replace(0, actualTest.length(), ""); // clear builder
		/**
		 * {"Sprints":[{"ID":"1","Name":"Sprint1","Velocity":0},
		 * 			   {"ID":"2","Name":"Sprint2","Velocity":0},
		 * 			   {"ID":"3","Name":"Sprint3","Velocity":0}],
		 * 	"Average":0}
		 */
		expectTest.append("{")
		        .append("\"Sprints\":[{")
		        .append("\"ID\":\"1\",")
		        .append("\"Name\":\"Sprint1\",")
		        .append("\"Velocity\":0},")
		        .append("{\"ID\":\"2\",")
		        .append("\"Name\":\"Sprint2\",")
		        .append("\"Velocity\":0},")
		        .append("{\"ID\":\"3\",")
		        .append("\"Name\":\"Sprint3\",")
		        .append("\"Velocity\":0}],")
		        .append("\"Average\":0}");
		assertEquals(expectTest.toString(), actualTest);
	}
}
