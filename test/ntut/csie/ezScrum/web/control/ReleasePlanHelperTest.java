package ntut.csie.ezScrum.web.control;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.resource.core.IProject;

public class ReleasePlanHelperTest extends TestCase {
	private ReleasePlanHelper helper;
	private CreateProject CP;
	private CreateRelease CR;
	private int ReleaseCount = 3;
	private int ProjectCount = 1;
	private Configuration configuration = null;
	
	public ReleasePlanHelperTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(this.ProjectCount); // 新增一個 Project
		this.CP.exeCreate(); // 執行

		this.CR = new CreateRelease(this.ReleaseCount, this.CP); // 專案新增三個
		// Release
		this.CR.exe(); // 執行
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.store();
		
		// release
		copyProject = null;
		configuration = null;
	}

	public void testloadReleasePlans() {
		this.helper = new ReleasePlanHelper(this.CP.getProjectList().get(0));
		IReleasePlanDesc[] ReleasePlans = this.helper.loadReleasePlans();
		assertEquals(this.ReleaseCount, ReleasePlans.length);

		for (int i = 0; i < ReleasePlans.length; i++) {
			assertEquals(Integer.toString(i + 1), ReleasePlans[i].getID());
			assertEquals(this.CR.getDefault_RELEASE_NAME(i + 1),
					ReleasePlans[i].getName());
			assertEquals(this.CR.getDefault_RELEASE_DESC(i + 1),
					ReleasePlans[i].getDescription());
		}
	}

	public void testloadReleasePlansList() {
		this.helper = new ReleasePlanHelper(this.CP.getProjectList().get(0));
		List<IReleasePlanDesc> ReleasePlans = this.helper
				.loadReleasePlansList();
		assertEquals(this.ReleaseCount, ReleasePlans.size());

		for (int i = 0; i < ReleasePlans.size(); i++) {
			assertEquals(Integer.toString(i + 1), ReleasePlans.get(i).getID());
			assertEquals(this.CR.getDefault_RELEASE_NAME(i + 1), ReleasePlans
					.get(i).getName());
			assertEquals(this.CR.getDefault_RELEASE_DESC(i + 1), ReleasePlans
					.get(i).getDescription());
		}
	}

	public void testgetLastReleasePlanNumber() {
		this.helper = new ReleasePlanHelper(this.CP.getProjectList().get(0));
		List<IReleasePlanDesc> ReleasePlans = this.helper
				.loadReleasePlansList();
		assertEquals(this.ReleaseCount, ReleasePlans.size());

		assertEquals(this.ReleaseCount, this.helper.getLastReleasePlanNumber());
	}

	public void testdeleteReleasePlan() {
		this.helper = new ReleasePlanHelper(this.CP.getProjectList().get(0));
		List<IReleasePlanDesc> ReleasePlans = this.helper
				.loadReleasePlansList();
		assertEquals(this.ReleaseCount, ReleasePlans.size());
		assertEquals(this.ReleaseCount, this.helper.getLastReleasePlanNumber());

		this.helper.deleteReleasePlan(Integer.toString(this.ReleaseCount)); // 刪除 ID = 3

		assertEquals(this.ReleaseCount - 1,
				this.helper.loadReleasePlans().length);
		assertEquals(this.ReleaseCount - 1, this.helper
				.getLastReleasePlanNumber());

		for (int i = 0; i < ReleasePlans.size(); i++) {
			assertEquals(Integer.toString(i + 1), ReleasePlans.get(i).getID());
			assertEquals(this.CR.getDefault_RELEASE_NAME(i + 1), ReleasePlans
					.get(i).getName());
			assertEquals(this.CR.getDefault_RELEASE_DESC(i + 1), ReleasePlans
					.get(i).getDescription());
		}
	}

	public void testeditReleasePlan() {
		this.helper = new ReleasePlanHelper(this.CP.getProjectList().get(0));
		List<IReleasePlanDesc> ReleasePlans = this.helper
				.loadReleasePlansList();
		assertEquals(this.ReleaseCount, ReleasePlans.size());
		assertEquals(this.ReleaseCount, this.helper.getLastReleasePlanNumber());

		// 設定修改的 ReleasePlan 日期
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		Date NewSD = cal.getTime(); // set New Start Date
		cal.add(Calendar.DAY_OF_YEAR, 180);
		Date NewED = cal.getTime(); // set New End Date

		// edit 判斷式的測試
		int lastID = helper.getLastReleasePlanNumber(); // 修改最後一筆資訊
		String NewName = "ReleaseName";
		String NewDesc = "ReleaseDesc";

		this.helper.editReleasePlan(Integer.toString(lastID), NewName, format
				.format(NewSD).toString(), format.format(NewED).toString(),
				NewDesc, "edit");

		IReleasePlanDesc editRelease = this.helper.getReleasePlan(Integer
				.toString(lastID));
		assertEquals(Integer.toString(lastID), editRelease.getID());
		assertEquals(NewName, editRelease.getName());
		assertEquals(NewDesc, editRelease.getDescription());
		assertEquals(format.format(NewSD).toString(), editRelease
				.getStartDate());
		assertEquals(format.format(NewED).toString(), editRelease.getEndDate());

		// save 判斷式的測試
		lastID++;
		NewName = "ReleaseName-New";
		NewDesc = "ReleaseDesc-New";
		this.helper.editReleasePlan(Integer.toString(lastID), NewName, format
				.format(NewSD).toString(), format.format(NewED).toString(),
				NewDesc, "save");
		IReleasePlanDesc saveRelease = this.helper.getReleasePlan(Integer
				.toString(lastID));
		assertEquals(this.CR.getReleaseCount() + 1, this.helper
				.loadReleasePlans().length);
		assertEquals(Integer.toString(lastID), saveRelease.getID());
		assertEquals(NewName, saveRelease.getName());
		assertEquals(NewDesc, saveRelease.getDescription());
		assertEquals(format.format(NewSD).toString(), saveRelease
				.getStartDate());
		assertEquals(format.format(NewED).toString(), saveRelease.getEndDate());

		// default 除錯測試
		this.helper.editReleasePlan(Integer.toString(lastID), NewName, format
				.format(NewSD).toString(), format.format(NewED).toString(),
				NewDesc, "xxxx");
		IReleasePlanDesc otherRelease = this.helper.getReleasePlan(Integer
				.toString(lastID));
		assertEquals(Integer.toString(lastID), otherRelease.getID());
		assertEquals(NewName, otherRelease.getName());
		assertEquals(NewDesc, otherRelease.getDescription());
		assertEquals(format.format(NewSD).toString(), otherRelease
				.getStartDate());
		assertEquals(format.format(NewED).toString(), otherRelease.getEndDate());
	}
	
	public void testgetReleasePlansByIDs() {
		this.helper = new ReleasePlanHelper(this.CP.getProjectList().get(0));
		// no release plan select
		String releasePlanIDs = "";
		List<IReleasePlanDesc> plans = helper.getReleasePlansByIDs(releasePlanIDs);
		assertEquals(0, plans.size());
		
		// select wrong releaseID
		releasePlanIDs = "4";
		plans = helper.getReleasePlansByIDs(releasePlanIDs);
		assertEquals(null, plans.get(0));
		
		// select releaseID = 1,2,3
		releasePlanIDs = "1,2,3";
		plans = helper.getReleasePlansByIDs(releasePlanIDs);
		assertEquals(3, plans.size());
	}
	
	public void testgetSprintVelocityToJSon() throws Exception {
		/**
		 * no release plan select
		 */
		String releases = "";
		// 取得ReleasePlans
		IProject project = this.CP.getProjectList().get(0);
		this.helper = new ReleasePlanHelper(project);
		List<IReleasePlanDesc> releaseDescs = helper.getReleasePlansByIDs(releases);
		// 取得SprintBacklog
		Configuration configuration = new Configuration();
		IUserSession userSession = configuration.getUserSession();
		SprintBacklogHelper SBhelper = new SprintBacklogHelper(project, userSession);
		// assert no release plan string value
		String actualTest = helper.getSprintVelocityToJSon(releaseDescs, SBhelper);
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
		CreateSprint CS = new CreateSprint(3, CP);
		CS.exe();
		// create story to sprint
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, CS, CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); // 每個Sprint中新增2筆Story
		// 取得ReleasePlans
		releases = "1,2,3";
		releaseDescs = helper.getReleasePlansByIDs(releases);
		// update SprintBacklog mapper info
		SBhelper = new SprintBacklogHelper(project, userSession);
		// assert release plan string value
		actualTest = helper.getSprintVelocityToJSon(releaseDescs, SBhelper);
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
