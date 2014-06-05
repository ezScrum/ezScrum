package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

public class MantisTagServiceTest extends TestCase {
	private ISQLControl control;
	private Configuration config;
	private MantisTagService tagService;
	
	private CreateProject CP;
	private CreateProductBacklog CPB;
	private CreateTag CT;
	private int ProjectCount = 1;
	private int StoryCount = 1;
	private int TagCount = 2;
	private String TEST_TAG_NAME = "TEST_TAG_";	// Tag Name
	private ezScrumInfoConfig ezScrumInfoConfig = new ezScrumInfoConfig();
	
//	ProductBacklog productBacklog = null;
	ProductBacklogMapper productBacklogMapper = null;
	
	public MantisTagServiceTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(ezScrumInfoConfig);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		// 新增Story	
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		// 新增Tag
		this.CT = new CreateTag(this.TagCount, this.CP);
		this.CT.exe();
		
		// 建立MantisTagService
		IProject project = this.CP.getProjectList().get(0);
		config = new Configuration(ezScrumInfoConfig.getUserSession(), true);
		MantisService mantisService = new MantisService(config);
		control = mantisService.getControl();
		control.setUser(config.getDBAccount());
		control.setPassword(config.getDBPassword());
		control.connection();
		
		tagService = new MantisTagService(control, config);
		productBacklogMapper = new ProductBacklogMapper(this.CP.getProjectList().get(0), ezScrumInfoConfig.getUserSession());
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
		project = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(ezScrumInfoConfig);
		ini.exe();										// 初始化 SQL
		control.close();

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.ezScrumInfoConfig.getTestDataPath());
    	
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	this.CPB = null;
    	this.CT = null;
    	this.ezScrumInfoConfig = null;
    	this.tagService = null;
    	this.productBacklogMapper = null;
    	projectManager = null;
    	
    	super.tearDown();
	}
	
	public void testInitTag(){
		//========= initTag 後，check sql 中 attach 於 story 的 tag 有無正確被 setTag 於對應的 issue中
		// 將 create 的 tag attach 到 story
		this.CT.attachTagToStory(this.CPB);

		IIssue story = new Issue();
		story.setIssueID(this.CPB.getIssueList().get(0).getIssueID());
		
		tagService.initTag(story);
		IIssueTag[] tagList = this.productBacklogMapper.getTagList();// 從 sql 取tag list 
		
		assertEquals(story.getTag().size(), tagList.length);// TagCount = 2
		for(int listIndex = 0; listIndex < tagList.length; listIndex++){
			assertEquals(story.getTag().get(listIndex).getTagId(), tagList[listIndex].getTagId());// TagID = 1,2...
			assertEquals(story.getTag().get(listIndex).getTagName(), tagList[listIndex].getTagName());// TagName = TEST_TAG_1,2...
		}
	}
	
	public void testUpdateTag(){
		//=========== check 更新 tag name 後，資料是否正確 ==========
		IIssueTag[] tagList = this.productBacklogMapper.getTagList();
		// check update 之前的資料正確與否
		for(int listIndex = 0; listIndex < tagList.length; listIndex++){
			assertEquals(listIndex+1, tagList[listIndex].getTagId());// TagID = 1,2...
			assertEquals(TEST_TAG_NAME+Integer.toString(listIndex+1),// TagName = TEST_TAG_1,2... 
						 tagList[listIndex].getTagName());
		}
		
		// update tag name
		for(int listIndex = 0; listIndex < tagList.length; listIndex++){
			tagService.updateTag(Long.toString(tagList[listIndex].getTagId()),
								"TEST_UPDATE_TAG_"+Integer.toString(listIndex+1),this.CP.getProjectList().get(0).getName());
		}

		// check update 之後 ezscrum_tag_table 的資料正確與否
		tagList = this.productBacklogMapper.getTagList();
		for(int listIndex = 0; listIndex < tagList.length; listIndex++){
			assertEquals("TEST_UPDATE_TAG_"+Integer.toString(listIndex+1), tagList[listIndex].getTagName());
		}
		
		// check update 之後 ezscrum_tag_relation 的資料正確與否
		checkTagRelationChenged(tagList);
	}
	
	// help testUpdateTag
	private void checkTagRelationChenged(IIssueTag[] tagList){
		String query = "SELECT * FROM `ezscrum_tag_relation` WHERE STORY_ID  = " 
						+ this.CPB.getIssueList().get(0).getIssueID(); 
		ResultSet result = tagService.getControl().executeQuery(query);
		
		int listIndex = 0;
		try {
			while (result.next()) {
				assertEquals(tagList[listIndex].getTagId(), result.getLong("ezscrum_tag_relation.tag_id"));
				listIndex++;
			}
		}catch(SQLException e){e.printStackTrace();}
	}
		
	public void testAddNewTag(){
		//=============(for MySQL) test 已存在相同tag名稱但不同專案==================
		//=============(local DB project有各自的DB檔，所以沒差不用測)======
		String resultID;
		
		// 新增TEST_PROJECT_2
		String query = "INSERT INTO `mantis_project_table` ( `id`, `name`, `status`,`enabled`,`view_state`,`access_min`, `description` ) "+
															"VALUES ( '2', 'TEST_PROJECT_2', '10', '1', '50', '10', '' )"; 
		tagService.getControl().execute(query);
		
		// 新增與TEST_PROJECT_1同名的TAG到TEST_PROJECT_2
		resultID = tagService.addNewTag((TEST_TAG_NAME+Integer.toString(1)), "TEST_PROJECT_2");
		
		query = "SELECT * FROM `ezscrum_tag_table` WHERE name = '"+TEST_TAG_NAME+Integer.toString(1)+"'";
		ResultSet result = tagService.getControl().executeQuery(query);
		int projectID = 1;
		try {			
			while (result.next()) {
				assertEquals(1, result.getLong("ezscrum_tag_table.id"));// 確認tag id相同
				assertEquals(projectID, result.getLong("ezscrum_tag_table.project_id"));// 確認project id 分別為1, 2
				projectID++;
			}
		}catch(SQLException e){e.printStackTrace();}		
		//=================================================================
		
		//============test 新增沒有相同名稱於其他專案之tag=============
		// setUp 預設 create 2筆 tag，所以新增第3筆來比對
		resultID = tagService.addNewTag((TEST_TAG_NAME+Integer.toString(3)), this.CP.getProjectList().get(0).getName());
		IIssueTag[] tagList = this.productBacklogMapper.getTagList();
		assertEquals(resultID, Long.toString(tagList[2].getTagId()));// resultID = 3
		assertEquals(TEST_TAG_NAME+resultID, tagList[2].getTagName());// TEST_TAG_X
		
	}
	
	public void testDeleteTag(){
		//=========== (for MySQL) 測試刪除tag ===========
		//=========== Local DB 不吃tagService.deleteTag的SQL指令，有sytax error 的訊息 =========
		// check 預設有兩筆 tag
		IIssueTag[] tagList = this.productBacklogMapper.getTagList();
		assertEquals(TagCount, tagList.length);//TagCount = 2
		String deleteTagId = Long.toString(tagList[0].getTagId()); 
		
		//delete TEST_TAG_1
		tagService.deleteTag(deleteTagId, this.CP.getProjectList().get(0).getName());
		tagList = this.productBacklogMapper.getTagList();
		assertEquals(TagCount-1, tagList.length);//TagCount-1 = 1
		
		// QUERY delete 掉的 tag
		String query = "SELECT * FROM `ezscrum_tag_table` WHERE ID  = "+deleteTagId ;
		ResultSet result = tagService.getControl().executeQuery(query);
		
		try{// check resultSet 中找不到 delete 掉的 tag 
			assertTrue(!result.next());
		}catch(SQLException e){e.printStackTrace();}
	}
	
	public void testGetTagByName(){
		//========== check createTag 的id, name 是否正確 ==========
		// get tag list from sql
		String projectName = this.CP.getProjectList().get(0).getName();
		IIssueTag[] tagList = this.productBacklogMapper.getTagList();
		// get single tag from sql by name
		IIssueTag resultTag;
		for(int listIndex = 0; listIndex < tagList.length; listIndex++){
			resultTag = tagService.getTagByName(tagList[listIndex].getTagName(), projectName);
			assertEquals(tagList[listIndex].getTagId(), resultTag.getTagId());
			assertEquals(tagList[listIndex].getTagName(), resultTag.getTagName());
		}
	}
	
	public void testGetTagList(){
		//========= check createTag 產生的 2個 tag 的id, name 是否正確 =========
		// get tag list from sql
		String projectName = this.CP.getProjectList().get(0).getName();
		IIssueTag[] resultTagList = tagService.getTagList(projectName);
		assertEquals(TagCount, resultTagList.length);
		// check each tag's info. correct or not.
		for(int listIndex = 0; listIndex < resultTagList.length; listIndex++){
			assertEquals(Integer.toString(listIndex+1), Long.toString(resultTagList[listIndex].getTagId()));
			assertEquals(TEST_TAG_NAME+Integer.toString(listIndex+1), resultTagList[listIndex].getTagName());// TEST_TAG_X
		}
	}
	
	public void testIsTagExist(){
		//========= check create 的 2 個 tag 有無存在於 sql 中 =========
		for(int index = 0; index < TagCount; index++){// TEST_TAG_X
			assertTrue(tagService.isTagExist(TEST_TAG_NAME+Integer.toString(index+1), 
											this.CP.getProjectList().get(0).getName()));
		}
	}
	
	public void testAddStoryTag(){
		//========= check tag 是否有正確 attach to story ===========
		// attach create tag 的 tag 到 create story 的 story
		addStoryTagRelation();
		//=========================================================
		
		//========== check 已存在 relation 再 attach tag to story ==========
		addStoryTagRelation();
	}
	
	// help testAddStoryTag
	private void addStoryTagRelation(){
		String storyID = Long.toString(this.CPB.getIssueList().get(0).getIssueID());
		String tagID;
		for(int index = 0; index < TagCount; index++){
			tagID = Long.toString(this.CT.getTagList().get(index).getTagId());
			// attach tag to story
			tagService.addStoryTag(storyID, tagID);
			// check the relation correct or not.
			checkAddStoryTagRelation(storyID, tagID);
		}
	}
	
	// help testAddStoryTag
	private void checkAddStoryTagRelation(String storyID, String tagID){
		String query = "SELECT * FROM `ezscrum_tag_relation` WHERE story_id  = " + storyID + " AND tag_id = " + tagID;
		ResultSet result = tagService.getControl().executeQuery(query);
		// assert the tag id correct or not.
		try {
			while (result.next()) {
				assertEquals(tagID, Long.toString(result.getLong("ezscrum_tag_relation.tag_id")));
			}
		}catch(SQLException e){e.printStackTrace();}
	}
	
	public void testRemoveStoryTag(){
		//========= check tag 是否有正確 remove from story ===========
		// 先將 tag attach to story 
		this.CT.attachTagToStory(this.CPB);
		
		String storyID = Long.toString(this.CPB.getIssueList().get(0).getIssueID());
		String tagID, query;
		for(int index = 0; index < TagCount; index++){
			tagID = Long.toString(this.CT.getTagList().get(index).getTagId());
			// remove relation between tag and story
			tagService.removeStoryTag(storyID, tagID);
			query = "SELECT * FROM `ezscrum_tag_relation` WHERE story_id = " + storyID + " AND tag_id = " + tagID ; 
			
			ResultSet result = tagService.getControl().executeQuery(query);
			try {// check ezscrum_tag_relation table 中 story 與 tag 的 relation 拿掉與否
				assertTrue(!result.next());
			}catch(SQLException e){e.printStackTrace();}
		}
	}
}