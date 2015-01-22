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
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class ReleaseBacklogTest extends TestCase {
	private CreateProject mCreateProject;
	private CreateRelease mCreateRelease;
	private CreateSprint mCreateSprint;
	private ReleaseBacklog mReleaseBacklog;
	private IUserSession mUserSession = null;
	private IProject mProject = null;
	private Configuration mConfig = null;

	public ReleaseBacklogTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一個Project
		mCreateProject = new CreateProject(1);
		mCreateProject.exeCreate();

		// 新增一個Release
		mCreateRelease = new CreateRelease(1, mCreateProject);
		mCreateRelease.exe();

		mUserSession = mConfig.getUserSession();
		mProject = mCreateProject.getProjectList().get(0);

		super.setUp();
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
		mCreateProject = null;
		projectManager = null;
		mReleaseBacklog = null;
		mProject = null;
		mUserSession = null;
		mConfig = null;
	}

	/**
	 * 把除了最後一筆 story 以外的 story 都設成 done，所以 releaseBacklog 的 getReleaseAllStoryDone 應該要為 "1.0"
	 */
	public void testReleaseBacklog_1() throws Exception {
		// 新增三筆 sprints
		mCreateSprint = new CreateSprint(3, mCreateProject);
		mCreateSprint.exe();
		// 每個Sprint中新增2筆Story
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, mCreateSprint, mCreateProject, "EST");
		ASS.exe();

		String releaseId = "1";
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mUserSession, mProject);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mUserSession, mProject);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc plan = releasePlanHelper.getReleasePlan(releaseId);

		IStory[] stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIdList = new ArrayList<Long>();
		for (IStory story : stories) {
			storyIdList.add(story.getIssueID());
		}

		// 把 Story 加入 release plan 1 中
		productBacklogLogic.addReleaseTagToIssue(storyIdList, releaseId);
		mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession, mCreateSprint.getSprintIDList().get(0));
		for (int i = 0; i < stories.length; i++) {
			// 把除了最後一筆 story 以外的 story 都設成 done
			if (stories[i].getStoryId() != stories.length) {
				sprintBacklogLogic.doneIssue(stories[i].getStoryId(), stories[i].getSummary(), stories[i].getNotes(), "", stories[i].getActualHour());
				// 每做完一筆 story done 就更新 release 裡面 story 的資訊
				mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
			}
		}
		assertEquals(1.0, mReleaseBacklog.getReleaseAllStoryDone());
	}

	/**
	 * 讓 story 在 sprint 結束後才 closed，getReleaseAllStoryDone 也應該要把 story 視為 done，並且 real point 也要扣掉
	 */
	public void testReleaseBacklog_2() throws Exception {
		// 新增三筆 sprints
		mCreateSprint = new CreateSprint(3, mCreateProject);
		mCreateSprint.exe();
		// 每個 Sprint 中新增2筆 Story
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, mCreateSprint, mCreateProject, "EST");
		ASS.exe();

		String releaseId = "1";
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mUserSession, mProject);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mUserSession, mProject);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc plan = releasePlanHelper.getReleasePlan(releaseId);
		mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));

		SprintPlanMapper spMapper = new SprintPlanMapper(mProject);
		List<ISprintPlanDesc> descs = spMapper.getSprintPlanList();
		// 用來設定最後一個 story 的 close date
		String theDate = descs.get(mCreateSprint.getSprintCount() - 1).getEndDate();

		IStory[] stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIDList = new ArrayList<Long>();
		for (IStory story : stories) {
			storyIDList.add(story.getIssueID());
		}

		// 把 Story 加入 relaseplan 1 中
		productBacklogLogic.addReleaseTagToIssue(storyIDList, releaseId);
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession, mCreateSprint.getSprintIDList().get(0));
		IStory lastStory = null;
		for (int i = 0; i < stories.length; i++) {
			// 把除了最後一筆 story 以外的 story 都設成 done
			if (stories[i].getStoryId() != stories.length) {
				sprintBacklogLogic.doneIssue(stories[i].getStoryId(), stories[i].getSummary(), stories[i].getNotes(), "", stories[i].getActualHour());
				// 每做完一筆 story done 就更新 release 裡面 story 的資訊
				mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
			}
			else {
				lastStory = stories[i];
			}
		}
		assertEquals(1.0, mReleaseBacklog.getReleaseAllStoryDone());

		// 讓 story 的 close date 為 sprint endDate 的後一天
		sprintBacklogLogic.doneIssue(lastStory.getStoryId(), lastStory.getSummary(), lastStory.getNotes(), getDate(theDate, 1), lastStory.getActualHour());
		// 更新 release 裡面 story 的資訊
		mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
		assertEquals(0.0, mReleaseBacklog.getReleaseAllStoryDone());
	}

	/**
	 * 用來取得從date之後經過duration天的日期時間 (return = date + duration)
	 * @param date
	 * @param duration
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
