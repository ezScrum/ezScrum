package ntut.csie.ezScrum.web.control;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleaseBacklogTest {
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private ReleaseBacklog mReleaseBacklog;
	private IUserSession mUserSession = null;
	private ProjectObject mProject = null;
	private Configuration mConfig = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一個Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// 新增一個Release
		mCR = new CreateRelease(1, mCP);
		mCR.exe();

		mUserSession = mConfig.getUserSession();
		mProject = mCP.getAllProjects().get(0);
		ini = null;
	}

	@After
	public void tearDown() throws IOException, Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mCP = null;
		mCR = null;
		mCS = null;
		projectManager = null;
		mReleaseBacklog = null;
		mProject = null;
		mUserSession = null;
		mConfig = null;
	}

	/**
	 * 把除了最後一筆 story 以外的 story 都設成 done，所以 releaseBacklog 的 getReleaseAllStoryDone 應該要為 "1.0"
	 */
	@Test
	public void testReleaseBacklog_1() throws Exception {
		// 新增三筆 sprints
		mCS = new CreateSprint(3, mCP);
		mCS.exe();
		// 每個Sprint中新增2筆Story
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, mCS, mCP, "EST");
		ASS.exe();

		String releaseId = "1";
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mProject);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mProject);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc plan = releasePlanHelper.getReleasePlan(releaseId);

		ArrayList<StoryObject> stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIdList = new ArrayList<Long>();
		for (StoryObject story : stories) {
			storyIdList.add(story.getId());
		}

		// 把 Story 加入 release plan 1 中
		mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		for (int i = 0; i < stories.size(); i++) {
			// 把除了最後一筆 story 以外的 story 都設成 done
			if (stories.get(i).getId() != stories.size()) {
				sprintBacklogLogic.closeStory(stories.get(i).getId(), stories.get(i).getName(), stories.get(i).getNotes(), "2015/02/03-16:00:00");
				// 每做完一筆 story done 就更新 release 裡面 story 的資訊
				mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
			}
		}
		assertEquals(1.0, mReleaseBacklog.getReleaseAllStoryDone());
	}

	/**
	 * 讓 story 在 sprint 結束後才 closed，getReleaseAllStoryDone 也應該要把 story 視為 done，並且 real point 也要扣掉
	 */
	@Test
	public void testReleaseBacklog_2() throws Exception {
		// 新增三筆 sprints
		mCS = new CreateSprint(3, mCP);
		mCS.exe();
		// 每個 Sprint 中新增2筆 Story
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, mCS, mCP, "EST");
		ASS.exe();

		String releaseId = "1";
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mProject);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mProject);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc plan = releasePlanHelper.getReleasePlan(releaseId);
		mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));

		SprintPlanMapper spMapper = new SprintPlanMapper(mProject);
		List<ISprintPlanDesc> descs = spMapper.getSprintPlanList();
		// 用來設定最後一個 story 的 close date
		String theDate = descs.get(mCS.getSprintCount() - 1).getEndDate();

		ArrayList<StoryObject> stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIDList = new ArrayList<Long>();
		for (StoryObject story : stories) {
			storyIDList.add(story.getId());
		}

		// 把 Story 加入 relaseplan 1 中
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		StoryObject lastStory = null;
		for (int i = 0; i < stories.size(); i++) {
			// 把除了最後一筆 story 以外的 story 都設成 done
			if (stories.get(i).getId() != stories.size()) {
				sprintBacklogLogic.closeStory(stories.get(i).getId(), stories.get(i).getName(), stories.get(i).getNotes(), "2015/01/29-16:00:00");
				// 每做完一筆 story done 就更新 release 裡面 story 的資訊
				mReleaseBacklog = new ReleaseBacklog(mProject, plan, productBacklogHelper.getStoriesByRelease(plan));
			}
			else {
				lastStory = stories.get(i);
			}
		}
		assertEquals(1.0, mReleaseBacklog.getReleaseAllStoryDone());

		// 讓 story 的 close date 為 sprint endDate 的後一天
		sprintBacklogLogic.closeStory(lastStory.getId(), lastStory.getName(), lastStory.getNotes(), getDate(theDate, 1));
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
