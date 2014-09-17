package ntut.csie.ezScrum.web.control;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class ReleaseBacklogTest extends TestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private CreateSprint CS;
	private ReleaseBacklog releaseBacklog;
	private IUserSession userSession = null;
	private IProject project = null;
	private Configuration configuration = null;

	public ReleaseBacklogTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		// 新增一個Project
		CP = new CreateProject(1);
		CP.exeCreate();

		CR = new CreateRelease(1, this.CP); // 新增一個Release
		CR.exe();

		userSession = configuration.getUserSession();
		project = CP.getProjectList().get(0);

		super.setUp();

		// release
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		// 初始化 SQL
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();

		// release
		ini = null;
		CP = null;
		projectManager = null;
		releaseBacklog = null;
		project = null;
		userSession = null;
		configuration = null;
	}

	/**
	 * 把除了最後一筆story以外的story都設成done，所以releaseBacklog的getReleaseAllStoryDone應該要為"1.0"
	 */
	public void testReleaseBacklog_1() throws Exception {
		CS = new CreateSprint(3, CP); // 新增三筆 sprints
		CS.exe();
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, CS, CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); // 每個Sprint中新增2筆Story

		String releaseId = "1";
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project, userSession);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(userSession, project);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		IReleasePlanDesc plan = releasePlanHelper.getReleasePlan(releaseId);

		IStory[] stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIDList = new ArrayList<Long>();
		for (IStory story : stories) {
			storyIDList.add(story.getIssueID());
		}
		productBacklogLogic.addReleaseTagToIssue(storyIDList, releaseId); // 把Story加入release plan 1 中
		releaseBacklog = new ReleaseBacklog(project, plan, productBacklogHelper.getStoriesByRelease(plan));
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, CS.getSprintIDList().get(0));
		for (int i = 0; i < stories.length; i++) {
			// 把除了最後一筆story以外的story都設成done
			if (stories[i].getStoryId() != stories.length) {
				sprintBacklogLogic.doneIssue(stories[i].getStoryId(), stories[i].getSummary(), stories[i].getNotes(), "", stories[i].getActualHour());
				// 每做完一筆story done就更新release裡面story的資訊
				releaseBacklog = new ReleaseBacklog(project, plan, productBacklogHelper.getStoriesByRelease(plan));
			}
		}
		assertEquals(1.0, releaseBacklog.getReleaseAllStoryDone());
	}

	/**
	 * 讓story在sprint結束後才closed，getReleaseAllStoryDone也應該要把story視為done，並且real point也要扣掉
	 */
	public void testReleaseBacklog_2() throws Exception {
		CS = new CreateSprint(3, CP);		// 新增三筆 sprints
		CS.exe();
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, CS, CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe();							// 每個Sprint中新增2筆Story

		String releaseId = "1";
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project, userSession);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(userSession, project);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		IReleasePlanDesc plan = releasePlanHelper.getReleasePlan(releaseId);
		releaseBacklog = new ReleaseBacklog(project, plan, productBacklogHelper.getStoriesByRelease(plan));

		SprintPlanMapper spMapper = new SprintPlanMapper(project);
		List<ISprintPlanDesc> descs = spMapper.getSprintPlanList();
		String theDate = descs.get(CS.getSprintCount() - 1).getEndDate(); // 用來設定最後一個story的close date

		IStory[] stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIDList = new ArrayList<Long>();
		for (IStory story : stories) {
			storyIDList.add(story.getIssueID());
		}
		productBacklogLogic.addReleaseTagToIssue(storyIDList, releaseId);// 把Story加入relaseplan 1 中
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, CS.getSprintIDList().get(0));
		IStory LastStory = null;
		for (int i = 0; i < stories.length; i++) {
			// 把除了最後一筆story以外的story都設成done
			if (stories[i].getStoryId() != stories.length) {
				sprintBacklogLogic.doneIssue(stories[i].getStoryId(), stories[i].getSummary(), stories[i].getNotes(), "", stories[i].getActualHour());
				// 每做完一筆story done就更新release裡面story的資訊
				releaseBacklog = new ReleaseBacklog(project, plan, productBacklogHelper.getStoriesByRelease(plan));
			}
			else {
				LastStory = stories[i];
			}
		}
		assertEquals(1.0, releaseBacklog.getReleaseAllStoryDone());

		// 讓story的close date為sprint endDate的後一天
		sprintBacklogLogic.doneIssue(LastStory.getStoryId(), LastStory.getSummary(), LastStory.getNotes(), getDate(theDate, 1), LastStory.getActualHour());
		// 更新release裡面story的資訊
		releaseBacklog = new ReleaseBacklog(project, plan, productBacklogHelper.getStoriesByRelease(plan));
		assertEquals(0.0, releaseBacklog.getReleaseAllStoryDone());
	}

	/**
	 * 用來取得從date之後經過duration天的日期時間 (return = date + duration)
	 * 
	 * @param date
	 * @param duration
	 * @return
	 * @throws ParseException
	 */
	private String getDate(String date, int duration) throws ParseException {
		SimpleDateFormat format_1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat format_2 = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		Date theDate = format_1.parse(date);
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		cal_start.setTime(theDate);
		cal_end.setTime(theDate);
		cal_end.add(Calendar.DAY_OF_YEAR, duration);
		return format_2.format(cal_end.getTime());
	}
}
