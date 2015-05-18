package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;

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
		IReleasePlanDesc[] ReleasePlans = mReleasePlanHelper.loadReleasePlans();
		assertEquals(this.mReleaseCount, ReleasePlans.length);

		for (int i = 0; i < ReleasePlans.length; i++) {
			assertEquals(Integer.toString(i + 1), ReleasePlans[i].getID());
			assertEquals(mCR.getDefault_RELEASE_NAME(i + 1), ReleasePlans[i].getName());
			assertEquals(mCR.getDefault_RELEASE_DESC(i + 1), ReleasePlans[i].getDescription());
		}
	}

	@Test
	public void testLoadReleasePlansList() {
		this.mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<IReleasePlanDesc> ReleasePlans = mReleasePlanHelper.loadReleasePlansList();
		assertEquals(mReleaseCount, ReleasePlans.size());

		for (int i = 0; i < ReleasePlans.size(); i++) {
			assertEquals(Integer.toString(i + 1), ReleasePlans.get(i).getID());
			assertEquals(mCR.getDefault_RELEASE_NAME(i + 1), ReleasePlans.get(i).getName());
			assertEquals(mCR.getDefault_RELEASE_DESC(i + 1), ReleasePlans.get(i).getDescription());
		}
	}

	@Test
	public void testGetLastReleasePlanNumber() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<IReleasePlanDesc> ReleasePlans = mReleasePlanHelper.loadReleasePlansList();
		assertEquals(mReleaseCount, ReleasePlans.size());
		assertEquals(mReleaseCount, mReleasePlanHelper.getLastReleasePlanNumber());
	}

	@Test
	public void testDeleteReleasePlan() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<IReleasePlanDesc> ReleasePlans = mReleasePlanHelper.loadReleasePlansList();
		assertEquals(mReleaseCount, ReleasePlans.size());
		assertEquals(mReleaseCount, mReleasePlanHelper.getLastReleasePlanNumber());

		mReleasePlanHelper.deleteReleasePlan(Integer.toString(mReleaseCount)); // 刪除 ID = 3

		assertEquals(mReleaseCount - 1, mReleasePlanHelper.loadReleasePlans().length);
		assertEquals(mReleaseCount - 1, mReleasePlanHelper.getLastReleasePlanNumber());

		for (int i = 0; i < ReleasePlans.size(); i++) {
			assertEquals(Integer.toString(i + 1), ReleasePlans.get(i).getID());
			assertEquals(mCR.getDefault_RELEASE_NAME(i + 1), ReleasePlans.get(i).getName());
			assertEquals(mCR.getDefault_RELEASE_DESC(i + 1), ReleasePlans.get(i).getDescription());
		}
	}

	@Test
	public void testEditReleasePlan() {
		mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		List<IReleasePlanDesc> ReleasePlans = mReleasePlanHelper.loadReleasePlansList();
		assertEquals(mReleaseCount, ReleasePlans.size());
		assertEquals(mReleaseCount, mReleasePlanHelper.getLastReleasePlanNumber());

		// 設定修改的 ReleasePlan 日期
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		Date NewSD = cal.getTime(); // set New Start Date
		cal.add(Calendar.DAY_OF_YEAR, 180);
		Date NewED = cal.getTime(); // set New End Date

		// edit 判斷式的測試
		int lastID = mReleasePlanHelper.getLastReleasePlanNumber(); // 修改最後一筆資訊
		String NewName = "ReleaseName";
		String NewDesc = "ReleaseDesc";

		mReleasePlanHelper.editReleasePlan(Integer.toString(lastID), NewName, format.format(NewSD).toString(), format.format(NewED).toString(), NewDesc, "edit");

		IReleasePlanDesc editRelease = this.mReleasePlanHelper.getReleasePlan(Integer.toString(lastID));
		assertEquals(Integer.toString(lastID), editRelease.getID());
		assertEquals(NewName, editRelease.getName());
		assertEquals(NewDesc, editRelease.getDescription());
		assertEquals(format.format(NewSD).toString(), editRelease.getStartDate());
		assertEquals(format.format(NewED).toString(), editRelease.getEndDate());

		// save 判斷式的測試
		lastID++;
		NewName = "ReleaseName-New";
		NewDesc = "ReleaseDesc-New";
		mReleasePlanHelper.editReleasePlan(Integer.toString(lastID), NewName, format.format(NewSD).toString(), format.format(NewED).toString(),NewDesc, "save");
		IReleasePlanDesc saveRelease = mReleasePlanHelper.getReleasePlan(Integer.toString(lastID));
		assertEquals(mCR.getReleaseCount() + 1, mReleasePlanHelper.loadReleasePlans().length);
		assertEquals(Integer.toString(lastID), saveRelease.getID());
		assertEquals(NewName, saveRelease.getName());
		assertEquals(NewDesc, saveRelease.getDescription());
		assertEquals(format.format(NewSD).toString(), saveRelease.getStartDate());
		assertEquals(format.format(NewED).toString(), saveRelease.getEndDate());

		// default 除錯測試
		mReleasePlanHelper.editReleasePlan(Integer.toString(lastID), NewName, format.format(NewSD).toString(), format.format(NewED).toString(), NewDesc, "xxxx");
		IReleasePlanDesc otherRelease = mReleasePlanHelper.getReleasePlan(Integer.toString(lastID));
		assertEquals(Integer.toString(lastID), otherRelease.getID());
		assertEquals(NewName, otherRelease.getName());
		assertEquals(NewDesc, otherRelease.getDescription());
		assertEquals(format.format(NewSD).toString(), otherRelease.getStartDate());
		assertEquals(format.format(NewED).toString(), otherRelease.getEndDate());
	}
	
	@Test
	public void testGetReleasePlansByIDs() {
		this.mReleasePlanHelper = new ReleasePlanHelper(mCP.getAllProjects().get(0));
		// no release plan select
		String releasePlanIDs = "";
		List<IReleasePlanDesc> plans = mReleasePlanHelper.getReleasePlansByIDs(releasePlanIDs);
		assertEquals(0, plans.size());
		
		// select wrong releaseID
		releasePlanIDs = "4";
		plans = mReleasePlanHelper.getReleasePlansByIDs(releasePlanIDs);
		assertEquals(null, plans.get(0));
		
		// select releaseID = 1,2,3
		releasePlanIDs = "1,2,3";
		plans = mReleasePlanHelper.getReleasePlansByIDs(releasePlanIDs);
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
		List<IReleasePlanDesc> releaseDescs = mReleasePlanHelper.getReleasePlansByIDs(releases);
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
		releaseDescs = mReleasePlanHelper.getReleasePlansByIDs(releases);
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

	/************************************************************
	 * 測試新增Sprint至Release
	 *************************************************************/
//	public void testaddReleaseSprints() {
//		this.helper = new ReleasePlanHelper(this.CP.getIProjectList().get(0));
//		int lastID = this.helper.getLastReleasePlanNumber(); // 修改最後一筆資訊
//
//		CreateSprint CS = new CreateSprint(3, this.CP);
//		CS.exe(); // 新增三筆 sprints
//
//		this.helper.addReleaseSprints(Integer.toString(lastID), CS
//				.getSprintIDList()); // last release 加入三筆 sprints
//
//		IReleasePlanDesc Release = this.helper.getReleasePlan(Integer
//				.toString(lastID));
//
//		assertEquals(CS.getSprintCount(), Release.getSprints().size());
//
//		for (int i = 0; i < CS.getSprintCount(); i++) {
//			assertEquals(Integer.toString(i + 1), Release.getSprints().get(i));
//		}
//	}
//
////	public void testStory_And_Release_Relation() throws Exception {
////		helper = new ReleasePlanHelper(CP.getIProjectList().get(0));
////		String lastID = helper.getLastReleasePlanNumber() + "";
////
////		// 建立一筆Sprint
////		CreateSprint CS = new CreateSprint(2, this.CP);
////
////		// 在每個Sprint中加入5個Story
////		AddStoryToSprint addStory = new AddStoryToSprint(5, 1, CS, CP,
////				ezScrumInfo.ESTIMATION);
////		addStory.exe();
////
////		// 將所有Sprint加入最後一個Release
////		helper.addReleaseSprints(lastID, CS.getSprintIDList());
////
////		IReleasePlanDesc desc = helper.getReleasePlan(lastID);
////
////		// 從ProductBacklog中取出所有Story
////		ProductBacklogHelper productBacklog = new ProductBacklogHelper(CP
////				.getIProjectList().get(0), ezScrumInfo.CreateUserSession());
////		IStory[] stories = productBacklog.getStories();
////		// 檢查是否每一個Story的Release ID皆為最後一個ReleaseID
////		for (IStory story : stories) {
////			assertEquals(lastID, story.getTagValue(ScrumEnum.RELEASE_TAG));
////		}
////
////		// 從Release中刪除第一個Sprint
////		helper.deleteSpritnOfRelease(lastID, "1");
////		stories = productBacklog.getStories();
////
////		// 檢查是否每一個Story的Sprint為1的Release ID是否為null
////		// Sprint為2的Release ID是否為最後一個ReleaseID
////		for (IStory story : stories) {
////			if (story.getTagValue(ScrumEnum.SPRINT_TAG).equals("1"))
////				assertEquals(null, story.getTagValue(ScrumEnum.RELEASE_TAG));
////			else
////				assertEquals(lastID, story.getTagValue(ScrumEnum.RELEASE_TAG));
////		}
////	}
//
//	/************************************************************
//	 * 測試從Release 中刪除Sprint
//	 *************************************************************/
//	public void testdeleteSpritnOfRelease() {
//		this.helper = new ReleasePlanHelper(this.CP.getIProjectList().get(0));
//		int lastID = this.helper.getLastReleasePlanNumber(); // 修改最後一筆資訊
//
//		CreateSprint CS = new CreateSprint(3, this.CP);
//		CS.exe(); // 新增三筆 sprints
//
//		this.helper.addReleaseSprints(Integer.toString(lastID), CS
//				.getSprintIDList()); // last release 加入三筆 sprints
//
//		IReleasePlanDesc Release = this.helper.getReleasePlan(Integer
//				.toString(lastID));
//		assertEquals(CS.getSprintCount(), Release.getSprints().size());
//		for (int i = 0; i < CS.getSprintCount(); i++) {
//			assertEquals(Integer.toString(i + 1), Release.getSprints().get(i));
//		}
//
//		this.helper.deleteSpritnOfRelease(Integer.toString(lastID), Release
//				.getSprints().get(0)); // 刪除 ID = 1 的 sprint
//		Release = this.helper.getReleasePlan(Integer.toString(lastID));
//		assertEquals(CS.getSprintCount() - 1, Release.getSprints().size());
//		for (int i = 1; i < CS.getSprintCount() - 1; i++) {
//			// 因為 ID=1 已經被刪除，所以從 index 從 1 開始
//			assertEquals(Integer.toString(i + 1), Release.getSprints().get(
//					i - 1));
//		}
//	}
//
//	public void testgetReleasePlan() {
//		this.helper = new ReleasePlanHelper(this.CP.getIProjectList().get(0));
//		int lastID = this.helper.getLastReleasePlanNumber(); // 修改最後一筆資訊
//
//		// 抓取最後一筆 ReleasePlan
//		IReleasePlanDesc Release = this.helper.getReleasePlan(Integer
//				.toString(lastID));
//		assertEquals(Integer.toString(lastID), Release.getID());
//		assertEquals(this.CR.getDefault_RELEASE_NAME(lastID), Release.getName());
//		assertEquals(this.CR.getDefault_RELEASE_DESC(lastID), Release
//				.getDescription());
//
//		// 除錯，回傳null
//		IReleasePlanDesc NullRelease = this.helper.getReleasePlan("xx");
//		assertEquals(null, NullRelease);
//
//		IReleasePlanDesc NullRelease2 = this.helper.getReleasePlan(Integer
//				.toString(lastID + 10));
//		assertEquals(null, NullRelease2);
//	}
//
//	public void testgetReleaseID() {
//		// 初始 Release 先加入三筆 sprints 資料
//		this.helper = new ReleasePlanHelper(this.CP.getIProjectList().get(0));
//		int lastID = this.helper.getLastReleasePlanNumber(); // 修改最後一筆資訊
//
//		CreateSprint CS = new CreateSprint(3, this.CP);
//		CS.exe(); // 新增三筆 sprints
//
//		this.helper.addReleaseSprints(Integer.toString(lastID), CS
//				.getSprintIDList()); // last release 加入三筆 sprints
//
//		IReleasePlanDesc Release = this.helper.getReleasePlan(Integer
//				.toString(lastID));
//		assertEquals(CS.getSprintCount(), Release.getSprints().size());
//		for (int i = 0; i < CS.getSprintCount(); i++) {
//			assertEquals(Integer.toString(i + 1), Release.getSprints().get(i));
//		}
//
//		// 利用三筆 sprints 去取得 Release ID
//		List<String> SprintIDList = CS.getSprintIDList();
//		for (String str : SprintIDList) {
//			assertEquals(Integer.toString(lastID), this.helper
//					.getReleaseID(str));
//		}
//
//		// 除錯，輸入不在此 release 的 sprint，回傳的 release ID 為零
//		assertEquals("0", this.helper.getReleaseID("??"));
//		assertEquals("0", this.helper.getReleaseID("1000"));
//	}
//
//	public void testgetSprintsID() {
//		// 初始 Release 先加入三筆 sprints 資料
//		this.helper = new ReleasePlanHelper(this.CP.getIProjectList().get(0));
//		int lastID = this.helper.getLastReleasePlanNumber(); // 修改最後一筆資訊
//
//		CreateSprint CS = new CreateSprint(3, this.CP);
//		CS.exe(); // 新增三筆 sprints
//
//		this.helper.addReleaseSprints(Integer.toString(lastID), CS
//				.getSprintIDList()); // last release 加入三筆 sprints
//
//		IReleasePlanDesc Release = this.helper.getReleasePlan(Integer
//				.toString(lastID));
//		assertEquals(CS.getSprintCount(), Release.getSprints().size());
//		for (int i = 0; i < CS.getSprintCount(); i++) {
//			assertEquals(Integer.toString(i + 1), Release.getSprints().get(i));
//		}
//
//		// 利用三筆 sprints 去取得 Release ID
//		Hashtable<String, String> ht = this.helper.getSprintsID(Integer
//				.toString(lastID));
//		List<String> SprintIDList = CS.getSprintIDList();
//		for (String str : SprintIDList) {
//			assertEquals(ht.get(str), "true");
//		}
//
//		// 除錯，輸入的 release ID 沒有 sprint
//		Hashtable<String, String> ht2 = this.helper.getSprintsID(Integer
//				.toString(lastID - 1));
//		assertEquals(ht2, null);
//
//		// 除錯，輸入的 release ID 不存在
//		Hashtable<String, String> ht3 = this.helper.getSprintsID("1000");
//		assertEquals(ht3, null);
//		Hashtable<String, String> ht4 = this.helper.getSprintsID("XXXX");
//		assertEquals(ht4, null);
//	}

}
