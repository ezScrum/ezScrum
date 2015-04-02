package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetSprintBacklogDateInfoActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private IProject mIProject;
	private long mSprintId;
	private IUserSession mUserSession;
	private final String mActionPath = "/AjaxGetSprintBacklogDateInfo";

	public AjaxGetSprintBacklogDateInfoActionTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		mSprintId = 1;
		mIProject = mCP.getProjectList().get(0);

		mUserSession = mConfig.getUserSession();
		super.setUp();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		ini = null;
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		mIProject = null;
		mUserSession = null;
	}

	public void testGetSprintBacklogDateInfoAction() {
		List<String> sprintIdList = mCS.getSprintIdList();

		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("SprintID", sprintIdList.get(0));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		int availableDays = new SprintBacklogLogic(mIProject, mUserSession,
				String.valueOf(mSprintId)).getSprintAvailableDays(mSprintId);
		List<String> dateList = getWorkDate(mCS.mToday, availableDays);
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"Dates\":[");
		for (int i = 0; i < dateList.size(); i++) {
			expectedResponseText.append("{\"Id\":\"Date_").append(i + 1)
					.append("\",").append("\"Name\":\"")
					.append(dateList.get(i)).append("\"}");
			if (i != dateList.size() - 1) {
				expectedResponseText.append(",");
			}
		}
		expectedResponseText.append("]}");

		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	/**
	 * 取得工作天數
	 * 
	 * @param startDate
	 * @param availableDays
	 * @return
	 */
	private List<String> getWorkDate(Date startDate, int availableDays) {
		List<String> dateList = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate); // 設定為現在時間
		while (availableDays-- > 0) {
			while (DateUtil.isHoliday(cal.getTime())) { // 判斷假日
				cal.add(Calendar.DATE, 1); // 跳過此一工作天
			}

			SimpleDateFormat format = new SimpleDateFormat("MM/dd");
			String date = format.format(cal.getTime());
			dateList.add(date);
			cal.add(Calendar.DATE, 1); // 加一工作天
		}
		return dateList;
	}
}
