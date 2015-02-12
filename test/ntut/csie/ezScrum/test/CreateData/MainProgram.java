package ntut.csie.ezScrum.test.CreateData;

import java.util.Scanner;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;

/*
 * For create data manually
 */
public class MainProgram {
	public static void main(String[] args) throws Exception {
		Configuration configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();			// 連結到遠端 MySQL 的初始化方法，帳號密碼為擁有 Access SQL 權限的使用者
		
		// ----------------------- 自動產生指定的專案 ----------------------------
		System.out.print("請輸入要產生幾個測試專案: ");
		Scanner sc = new Scanner(System.in);
		int Project_Count = 1;//sc.nextInt();
		CreateProject createProject = new CreateProject(Project_Count);
		createProject.exeCreate();		// 執行 -建立專案
		// ----------------------- 自動產生指定的專案 ----------------------------

		
		// ----------------------- 自動產生指定的 release plan 個數 -----------------------------
//		System.out.print("請輸入每個專案要產生幾個 Release Plan: ");
//		int ReleaseCount = sc.nextInt();
//		CreateRelease createRelease = new CreateRelease(ReleaseCount, createProject);
//		createRelease.exe();		// 執行 - 新增 release plan
		// ----------------------- 自動產生指定的 release plan 個數 -----------------------------		
		

		// --------------------- 自動產生指定的 sprint plan 個數 -------------------------------
//		System.out.print("請輸入每個專案要產生幾個 Sprint Plan: ");
//		int SprintCount = 3;//sc.nextInt();
//		CreateSprint createSprint = new CreateSprint(SprintCount, createProject);
//		createSprint.exe();			// 執行 - 新增 sprint plan
		// --------------------- 自動產生指定的 sprint plan 個數 -------------------------------		
		
		
		// --------------------- 自動產生指定的 Story 個數 ------------------------------
//		System.out.print("請輸入每個專案要產生幾個 Stories: ");
//		int StoryCount = sc.nextInt();
//		CreateProductBacklog createStory = new CreateProductBacklog(StoryCount, createProject);
//		createStory.exe();			// 執行 - 新增 stories
		// --------------------- 自動產生指定的 Story 個數 ------------------------------
		
		
		// --------------------- 自動產生指定的 Task 個數，只需指定個數 -------------------------------
//		System.out.print("請輸入每個專案要產生幾個 Tasks: ");
//		int TaskCount = sc.nextInt();
//		CreateTask createTask = new CreateTask(TaskCount, createProject);
//		createTask.exe();			// 執行 - 新增 Tasks
		// --------------------- 自動產生指定的 Task 個數，只需指定個數 -------------------------------		
		
		

		// --------------------- 自動產生指定的 sprint plan 個數到各個 release plan -------------------
//		System.out.print("請輸入每個專案要產生幾個 Release Plan: ");
//		int ReleaseCount = sc.nextInt();
//		
//		System.out.print("請輸入每個 Release 要插入幾個 Sprint: ");
//		int SprintCount = sc.nextInt();
//		CreateRelease createRelease = new CreateRelease(ReleaseCount, SprintCount, createProject);
//		createRelease.exe();		// 執行 - 新增 release plan
//		
//		AddSprintToRelease sprintTorelease = new AddSprintToRelease(SprintCount, createRelease, createProject);
//		sprintTorelease.exe();		// 執行 - 新增 sprint plan 到 release plan
		// --------------------- 自動產生指定的 sprint plan 個數到各個 release plan -------------------

		
		// --------------------- 自動產生指定的 story 個數到各個 sprint plan 底下 ----------------------
//		System.out.print("請輸入每個專案要產生幾個 Sprint Plan: ");
//		int SprintCount = 2;//sc.nextInt();
//		CreateSprint createSprint = new CreateSprint(SprintCount, createProject);
//		createSprint.exe();			// 執行 - 新增 sprint plan
//		
//		System.out.print("請輸入每個 Sprint 要插入幾個 Stories: ");
//		int Sprint_Story = 5;//sc.nextInt();
//		
//		System.out.print("請輸入每個 Stories 估計的點數: ");
//		int Story_Est = 2;//sc.nextInt();
//		AddStoryToSprint storyTosprint = new AddStoryToSprint(Sprint_Story,	Story_Est, createSprint, createProject, ezScrumInfo.ESTIMATION);
//		storyTosprint.exe();		// 執行 - 將 stories 區分到每個 sprints
		// --------------------- 自動產生指定的 story 個數到各個 sprint plan 底下 ----------------------
		
		
		// --------------------- 自動產生指定的 Task 個數到各個 stories 底下 -----------------------------
//		System.out.print("請輸入每個專案要產生幾個 Sprint Plan: ");
//		int SprintCount = 2;//sc.nextInt();
//		CreateSprint createSprint = new CreateSprint(SprintCount, createProject);
//		createSprint.exe();			// 執行 - 新增 sprint plan
//		
//		System.out.print("請輸入每個 Sprint 要插入幾個 Stories: ");
//		int Sprint_Story = 5;//sc.nextInt();
//		
//		System.out.print("請輸入每個 Stories 估計的點數: ");
//		int Story_Est = 2;//sc.nextInt();
//		
//		AddStoryToSprint storyTosprint = new AddStoryToSprint(Sprint_Story,	Story_Est, createSprint, createProject, ezScrumInfo.ESTIMATION);
//		storyTosprint.exe();		// 執行 - 將 stories 區分到每個 sprints
//		
//		System.out.print("請輸入每個 Story 要插入幾個 Tasks: ");
//		int Story_Task = 2;//sc.nextInt();
//		
//		System.out.print("請輸入每個 Task 估計的點數: ");
//		int Task_Est = 1;//sc.nextInt();
//		AddTaskToStory taskTostory = new AddTaskToStory(Story_Task, Task_Est, storyTosprint, createProject);
//		taskTostory.exe();		
		// --------------------- 自動產生指定的 Task 個數到各個 stories 底下 -----------------------------		
		
		
		// --------------------- 移動指定的 IssueID 到 Check-Out or Done -------------------------------
//		ArrayList<Long> IDlist = new ArrayList<Long>();
//		IDlist.add(taskTostory.getTaskIDList().get(0));
//		IDlist.add(taskTostory.getTaskIDList().get(1));
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_YEAR, 2);
//		Date today = cal.getTime();
//		System.out.println("today = " + today.toString());
//		CheckOutIssue COI = new CheckOutIssue(IDlist, createProject, today);
//		COI.exeDone_Issues();		// 移動指定的 Task List ID 到 Done
		// --------------------- 移動指定的 IssueID 到 Check-Out or Done -------------------------------
		
		
		// --------------------- 自動新增指定個數的帳號 以及 可以指派Scrum角色  -------------------------------
//		int account_count = 5;
//		CreateAccount createAccount = new CreateAccount(account_count);
//		createAccount.exe();
//		
//		AddUserToRole adduserTorole = new AddUserToRole(createProject, createAccount);
//		adduserTorole.exe_PO();				// 設定為 ProductOwner
//		adduserTorole.exe_ST();				// 設定為 ScrumTeam
//		
//		adduserTorole.setAccountIndex(2);	// 改換指定的 Account
//		adduserTorole.exe_System();			// 設定為 system 權限
//		
//		adduserTorole.setNowAccountIsSystem();
//		adduserTorole.setProjectIndex(1);	// 改換指定的 Project
//		adduserTorole.exe_SM();				// 設定為 ScrumMaster
//		result is -----  
//	    <Assign actor="admin" role="admin" />
//	    <Assign actor="admin" role="user" />
//	    <Assign actor="admin" role="TEST_PROJECT_2_ScrumMaster" />
//	    <Assign actor="TEST_ACCOUNT_ID_3" role="admin" />
//	    <Assign actor="TEST_ACCOUNT_ID_1" role="TEST_PROJECT_1_ProductOwner" />
//	    <Assign actor="TEST_ACCOUNT_ID_1" role="TEST_PROJECT_1_ScrumTeam" />
		// --------------------- 自動新增指定個數的帳號 -------------------------------
		
		System.out.println("\n\n==============================================================");
		CopyProject copyProject = new CopyProject(createProject);
		copyProject.exeCopy_Delete_Project();			// 執行 - 將資料從 TestData/MyWorkspace 複製到使用者桌面並且刪除 TestData 之資料
	}
}