package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.StoryTagRelationEnum;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;

public class MantisTagServiceTest extends TestCase {
	private ISQLControl control;
	private MantisTagService tagService;
	
	private CreateProject CP;
	private CreateProductBacklog CPB;
	private CreateTag CT;
	private int ProjectCount = 1;
	private int StoryCount = 1;
	private int TagCount = 2;
	private String TEST_TAG_NAME = "TEST_TAG_";	// Tag Name
	private Configuration configuration;
	
	ProductBacklogMapper productBacklogMapper = null;
	
	public MantisTagServiceTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		configuration = new Configuration(new UserSession(AccountObject.get("admin")));
		
		InitialSQL ini = new InitialSQL(configuration);
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
		MantisService mantisService = new MantisService(configuration);
		control = mantisService.getControl();
		control.setUser(configuration.getDBAccount());
		control.setPassword(configuration.getDBPassword());
		control.connection();
		
		tagService = new MantisTagService(control, configuration);
		productBacklogMapper = new ProductBacklogMapper(this.CP.getProjectList().get(0), configuration.getUserSession());
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();										// 初始化 SQL
		control.close();

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();
    	
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	this.CPB = null;
    	this.CT = null;
    	this.tagService = null;
    	this.productBacklogMapper = null;
    	projectManager = null;
    	configuration = null;
    	
    	super.tearDown();
	}
	
	public void testInitTag(){
		//========= initTag 後，check sql 中 attach 於 story 的 tag 有無正確被 setTag 於對應的 issue中
		// 將 create 的 tag attach 到 story
		this.CT.attachTagToStory(this.CPB);

		IIssue story = new Issue();
		story.setIssueID(this.CPB.getIssueList().get(0).getIssueID());
		
		tagService.initTag(story);
		ArrayList<TagObject> tagList = this.productBacklogMapper.getTagList();// 從 sql 取tag list 
		
		assertEquals(story.getTags().size(), tagList.size());// TagCount = 2
		for(int listIndex = 0; listIndex < tagList.size(); listIndex++){
			assertEquals(story.getTags().get(listIndex).getId(), tagList.get(listIndex).getId());// TagID = 1,2...
			assertEquals(story.getTags().get(listIndex).getName(), tagList.get(listIndex).getName());// TagName = TEST_TAG_1,2...
		}
	}
	
	public void testUpdateTag(){
		//=========== check 更新 tag name 後，資料是否正確 ==========
		ArrayList<TagObject> tagList = this.productBacklogMapper.getTagList();
		// check update 之前的資料正確與否
		for(int listIndex = 0; listIndex < tagList.size(); listIndex++){
			assertEquals(listIndex+1, tagList.get(listIndex).getId());// TagID = 1,2...
			assertEquals(TEST_TAG_NAME+Integer.toString(listIndex+1),// TagName = TEST_TAG_1,2... 
					tagList.get(listIndex).getName());
		}
		
		// update tag name
		for(int listIndex = 0; listIndex < tagList.size(); listIndex++){
			tagService.updateTag(tagList.get(listIndex).getId(),
								"TEST_UPDATE_TAG_"+Integer.toString(listIndex+1),this.CP.getProjectList().get(0).getName());
		}

		// check update 之後 ezscrum_tag_table 的資料正確與否
		tagList = this.productBacklogMapper.getTagList();
		for(int listIndex = 0; listIndex < tagList.size(); listIndex++){
			assertEquals("TEST_UPDATE_TAG_"+Integer.toString(listIndex+1), tagList.get(listIndex).getName());
		}
	}

	public void testAddNewTag() {
		//============test 新增沒有相同名稱於其他專案之tag=============
		// setUp 預設 create 2筆 tag，所以新增第3筆來比對
		long resultID = tagService.addTag((TEST_TAG_NAME + 3), this.CP.getProjectList().get(0).getName());
		ArrayList<TagObject> tagList = this.productBacklogMapper.getTagList();
		assertEquals(resultID, tagList.get(2).getId());// resultID = 3
		assertEquals(TEST_TAG_NAME + resultID, tagList.get(2).getName());// TEST_TAG_X
	}
	
	public void testDeleteTag(){
		//=========== (for MySQL) 測試刪除tag ===========
		//=========== Local DB 不吃tagService.deleteTag的SQL指令，有sytax error 的訊息 =========
		// check 預設有兩筆 tag
		ArrayList<TagObject> tagList = this.productBacklogMapper.getTagList();
		assertEquals(TagCount, tagList.size());//TagCount = 2
		long deleteTagId = tagList.get(0).getId();
		
		tagService.addStoryTag("1", 1);
		
		//delete TEST_TAG_1
		tagService.deleteTag(deleteTagId, this.CP.getProjectList().get(0).getName());
		tagList = this.productBacklogMapper.getTagList();
		assertEquals(TagCount-1, tagList.size());//TagCount-1 = 1
		
		// QUERY delete 掉的 tag
		String query = "SELECT * FROM `tag` WHERE ID  = " + deleteTagId ;
		ResultSet result = tagService.getControl().executeQuery(query);
		
		try{// check resultSet 中找不到 delete 掉的 tag 
			assertTrue(!result.next());
		}catch(SQLException e){e.printStackTrace();}
	}
	
	public void testGetTagByName(){
		//========== check createTag 的id, name 是否正確 ==========
		// get tag list from sql
		String projectName = this.CP.getProjectList().get(0).getName();
		ArrayList<TagObject> tagList = this.productBacklogMapper.getTagList();
		// get single tag from sql by name
		TagObject resultTag;
		for(int listIndex = 0; listIndex < tagList.size(); listIndex++){
			resultTag = tagService.getTagByName(tagList.get(listIndex).getName(), projectName);
			assertEquals(tagList.get(listIndex).getId(), resultTag.getId());
			assertEquals(tagList.get(listIndex).getName(), resultTag.getName());
		}
	}

	public void testGetTagList(){
		//========= check createTag 產生的 2個 tag 的id, name 是否正確 =========
		// get tag list from sql
		String projectName = this.CP.getProjectList().get(0).getName();
		ArrayList<TagObject> resultTagList = tagService.getTagList(projectName);
		assertEquals(TagCount, resultTagList.size());
		// check each tag's info. correct or not.
		for(int listIndex = 0; listIndex < resultTagList.size(); listIndex++){
			assertEquals(Integer.toString(listIndex+1), Long.toString(resultTagList.get(listIndex).getId()));
			assertEquals(TEST_TAG_NAME+Integer.toString(listIndex+1), resultTagList.get(listIndex).getName());// TEST_TAG_X
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
		long tagID;
		for(int index = 0; index < TagCount; index++){
			tagID = this.CT.getTagList().get(index).getId();
			// attach tag to story
			tagService.addStoryTag(storyID, tagID);
			// check the relation correct or not.
			checkAddStoryTagRelation(storyID, tagID);
		}
	}
	
	// help testAddStoryTag
	private void checkAddStoryTagRelation(String storyID, long tagID){
		String query = "SELECT * FROM `story_tag_relation` WHERE Story_ID  = " + storyID + " AND Tag_ID = " + tagID;
		ResultSet result = tagService.getControl().executeQuery(query);
		// assert the tag id correct or not.
		try {
			while (result.next()) {
				assertEquals(tagID, result.getLong(StoryTagRelationEnum.TAG_ID));
			}
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void testRemoveStoryTag(){
		//========= check tag 是否有正確 remove from story ===========
		// 先將 tag attach to story 
		this.CT.attachTagToStory(this.CPB);
		
		String storyID = Long.toString(this.CPB.getIssueList().get(0).getIssueID());
		String query;
		long tagID;
		for(int index = 0; index < TagCount; index++){
			tagID = this.CT.getTagList().get(index).getId();
			// remove relation between tag and story
			tagService.removeStoryTag(storyID, tagID);
			query = "SELECT * FROM `story_tag_relation` WHERE Story_ID = " + storyID + " AND Tag_ID = " + tagID ; 
			ResultSet result = tagService.getControl().executeQuery(query);
			try {// check story_tag_relation table 中 story 與 tag 的 relation 拿掉與否
				assertTrue(!result.next());
			}catch(SQLException e){e.printStackTrace();}
		}
	}
}