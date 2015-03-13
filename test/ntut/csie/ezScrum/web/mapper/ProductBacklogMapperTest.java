package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogMapperTest {
	private CreateProject mCP;
	private CreateProductBacklog mCPB;
	private int mProjectCount = 1;
	private int mStoryCount = 2;
	private ProductBacklogMapper mProductBacklogMapper = null;
	private Configuration mConfig = null;
	private MySQLControl mControl = null;
	
	private final String mFILE_NAME = "Initial.sql";
	private final String mFILE_TYPE = "sql/plain";
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
		// 新增 Story	
		mCPB = new CreateProductBacklog(mStoryCount, mCP);
		mCPB.exe();
		
		// 建立 productbacklog 物件
		ProjectObject project = mCP.getAllProjects().get(0);
		mProductBacklogMapper = new ProductBacklogMapper(project, mConfig.getUserSession());
		
		// ============= release ==============
		ini = null;
		project = null;
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	mCPB = null;
    	mProductBacklogMapper = null;
    	projectManager = null;
    	mConfig = null;
    	mControl = null;
	}
	
	@Test
	public void testAddStory() throws SQLException {
		StoryInfo storyInfo = new StoryInfo();
		storyInfo.setName("TEST_STORY");
		storyInfo.setDescription("TEST_STORY_DESC");
		
		IIssue issue = mProductBacklogMapper.addStory(storyInfo);
		
		HistoryDAO historyDao = HistoryDAO.getInstance();
		ArrayList<HistoryObject> histories = historyDao.getHistoriesByIssue(issue.getIssueID(), IssueTypeEnum.TYPE_STORY);
		assertEquals(1, histories.size());
	}
	
	@Test // 測試上傳檔案到一筆 Story 是否成功
	public void testAddAttachFile_Story() {
		StoryObject story = mCPB.getStories().get(0);
		
		addAttachFile(mProductBacklogMapper, story.getId(), IssueTypeEnum.TYPE_STORY);
		
		story.reload();
		AttachFileObject ActualFile = story.getAttachFiles().get(0);
		
		assertEquals(1, story.getAttachFiles().size());
		assertEquals(mFILE_NAME, ActualFile.getName());
		assertEquals(mFILE_TYPE, ActualFile.getContentType());
		assertEquals(story.getId(), ActualFile.getId());
		
		// ============= release ==============
		story = null;
		ActualFile = null;
	}
	
	@Test // 測試刪除一筆 Story 的檔案
	public void testDeleteAttachFile_Story() {
		StoryObject story = mCPB.getStories().get(0);		
		
		addAttachFile(mProductBacklogMapper, story.getId(), IssueTypeEnum.TYPE_STORY);
		
		story.reload();
		AttachFileObject ActualFile = story.getAttachFiles().get(0);
		
		assertEquals(1, story.getAttachFiles().size());
		assertEquals(mFILE_NAME, ActualFile.getName());
		assertEquals(mFILE_TYPE, ActualFile.getContentType());
		assertEquals(story.getId(), ActualFile.getId());
		
		// 刪除此 issue 的檔案
		mProductBacklogMapper.deleteAttachFile(ActualFile.getId());
		story.reload();
		assertEquals(0, story.getAttachFiles().size());
		
		// ============= release ==============
		story = null;
		ActualFile = null;		
	}
	
	@Test // 測試不用透過 mantis 直接取得檔案的方法
	public void testGetAttachfile_Story() {
		StoryObject story = mCPB.getStories().get(0);
		
		addAttachFile(mProductBacklogMapper, story.getId(), IssueTypeEnum.TYPE_STORY);
		
		story.reload();
		AttachFileObject IssueFile = story.getAttachFiles().get(0);
		
		assertEquals(1, story.getAttachFiles().size());
		assertEquals(mFILE_NAME, IssueFile.getName());
		assertEquals(mFILE_TYPE, IssueFile.getContentType());
		assertEquals(story.getId(), IssueFile.getId());

		// ============= release ==============
		story = null;
	}
	
	private void addAttachFile(ProductBacklogMapper mapper, long issueId, int issutType) {
		AttachFileInfo attachFileInfo = new AttachFileInfo();
        attachFileInfo.issueId = issueId;
        attachFileInfo.issueType = issutType;
        attachFileInfo.name = mFILE_NAME;
        attachFileInfo.contentType = mFILE_TYPE;
        attachFileInfo.projectName = mCP.getProjectList().get(0).getName();
        mapper.addAttachFile(attachFileInfo);
	}
}
