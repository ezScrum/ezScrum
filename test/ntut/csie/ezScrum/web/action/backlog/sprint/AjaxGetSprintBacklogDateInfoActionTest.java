package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;
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
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	private final String ACTION_PATH = "/AjaxGetSprintBacklogDateInfo";
	private IProject project;
	private String sprintID;
	private IUserSession userSession;
	
	public AjaxGetSprintBacklogDateInfoActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();
		this.sprintID = "1";
		
		userSession = configuration.getUserSession();
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(configuration.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		configuration = null;
	}
	
	public void testGetSprintBacklogDateInfoAction(){
		List<String> idList = this.CS.getSprintIDList();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("SprintID", idList.get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		int availableDays = new SprintBacklogLogic(this.project, this.userSession, this.sprintID).getSprintAvailableDays(this.sprintID);
		List<String> dateList = this.getWorkDate(this.CS.Today, availableDays);
		String expectedResponseText = "{\"Dates\":[";
		for(int i = 0; i < dateList.size(); i++){
			expectedResponseText += "{\"Id\":\"Date_" + (i+1) + "\",\"Name\":\""+dateList.get(i)+"\"}";
			
			if(i != dateList.size()-1 ){
				expectedResponseText += ",";
			}
		}
		expectedResponseText += "]}";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
	}
	
	/**
	 * 取得工作天數
	 * @param startDate
	 * @param availableDays
	 * @return
	 */
	private List<String> getWorkDate(Date startDate, int availableDays){
		List<String> dateList = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);		// 設定為現在時間
		while(availableDays-- > 0) {
			while (DateUtil.isHoliday(cal.getTime())) {	// 判斷假日
				cal.add(Calendar.DATE, 1);		// 跳過此一工作天
			}
			
			SimpleDateFormat format = new SimpleDateFormat("MM/dd");
			String date = format.format(cal.getTime());
			
			
			dateList.add(date);
			cal.add(Calendar.DATE, 1);			// 加一工作天
		}
		return dateList;
	}
}
