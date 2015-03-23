package ntut.csie.ezScrum.web.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogHelperTest2 extends TestCase {
	private ProductBacklogHelper productBacklogHelper1;
	private ProductBacklogHelper productBacklogHelper2;
	private CreateProject CP;
	private CreateProductBacklog CPB;
	private int ProjectCount = 2;
	private int StoryCount = 1;
	private ProductBacklogLogic productBacklogLogic1;
	private ProductBacklogLogic productBacklogLogic2;
	
	private ProductBacklogMapper mapper1;
	private ProductBacklogMapper mapper2;
	
	private Configuration configuration;
	
	public ProductBacklogHelperTest2(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		// 新增 Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		// 新增 Story	
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		IUserSession userSession = configuration.getUserSession();
		
		IProject project1 = this.CP.getProjectList().get(0);
		IProject project2 = this.CP.getProjectList().get(1);
		
		this.productBacklogHelper1 = new ProductBacklogHelper(userSession, project1);
		this.productBacklogHelper2 = new ProductBacklogHelper(userSession, project2);

		this.productBacklogLogic1 = new ProductBacklogLogic(userSession, project1);
		this.productBacklogLogic2 = new ProductBacklogLogic(userSession, project2);

		this.mapper1 = new ProductBacklogMapper(project1, userSession);
		this.mapper2 = new ProductBacklogMapper(project2, userSession);
		
		// release
		ini = null;
    }

    protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
    	
		configuration.setTestMode(false);
		configuration.save();
    	
    	// release
    	ini = null;
    	this.productBacklogHelper1 = null;
    	this.productBacklogHelper2 = null;
    	this.CP = null;
    	this.CPB = null;
    	productBacklogLogic2 = null;
    	configuration = null;
    }
    
    public void testAddNewTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Tag");
    	productBacklogHelper2.addNewTag("Project2_Tag1");
    	productBacklogHelper2.addNewTag("Project2_Tag2");
    	
    	// 確認數量
    	assertEquals( 2, productBacklogHelper1.getTagList().size());
    	assertEquals( 3, productBacklogHelper2.getTagList().size());
    	
    	// 確認名稱
    	assertEquals( "Tag", productBacklogHelper1.getTagList().get(0).getName());
    	assertEquals( "Project1_Tag1", productBacklogHelper1.getTagList().get(1).getName());
    	
    	assertEquals( "Tag", productBacklogHelper2.getTagList().get(0).getName());
    	assertEquals( "Project2_Tag1", productBacklogHelper2.getTagList().get(1).getName());
    	assertEquals( "Project2_Tag2", productBacklogHelper2.getTagList().get(2).getName());
    }
    
    public void testIsTagExist() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	assertTrue(productBacklogHelper1.isTagExist("Tag"));
    	assertFalse(productBacklogHelper1.isTagExist("TagNoExist"));
    }
    
    public void testGetTagByName() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	TagObject tag1 = productBacklogHelper1.getTagList().get(0);
    	TagObject tag2 = productBacklogHelper1.getTagList().get(1);
    	
    	// 確認Tag
    	assertTrue(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper1.isTagExist("Project1_Tag1"));
    	
    	assertEquals(tag1.getName(), productBacklogHelper1.getTagByName("Tag").getName());
    	assertEquals(tag1.getId(), productBacklogHelper1.getTagByName("Tag").getId());
    	
    	assertEquals(tag2.getName(), productBacklogHelper1.getTagByName("Project1_Tag1").getName());
    	assertEquals(tag2.getId(), productBacklogHelper1.getTagByName("Project1_Tag1").getId());
    }
    
    public void testDeleteTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	// 確認存在
    	assertTrue(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper1.isTagExist("Project1_Tag1"));
    	
    	// 刪除Tag
    	productBacklogHelper1.deleteTag(productBacklogHelper1.getTagByName("Tag").getId());
    	productBacklogHelper1.deleteTag(productBacklogHelper1.getTagByName("Project1_Tag1").getId());
    	
    	// 確認不存在
    	assertFalse(productBacklogHelper1.isTagExist("Tag"));
    	assertFalse(productBacklogHelper1.isTagExist("Project1_Tag1"));    	
    }
    
    public void testGetTagList() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	// 確認數量
    	assertEquals(2, productBacklogHelper1.getTagList().size());
    	
    	// 確認名稱
    	assertEquals("Tag", productBacklogHelper1.getTagList().get(0).getName());
    	assertEquals("Project1_Tag1", productBacklogHelper1.getTagList().get(1).getName());
    }
    
    public void testUpdateTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Project2_Tag1");

    	// 將 Project1_Tag1 修改為新名稱
    	long Project1_Tag1_BId = productBacklogHelper1.getTagByName("Project1_Tag1").getId();
    	mapper1.updateTag(Project1_Tag1_BId, "Project1_Tag");

    	// 判斷原本的Tag不存在 修改後的Tag存在
    	assertNull(productBacklogHelper1.getTagByName("Project1_Tag1"));
    	assertNotNull(productBacklogHelper1.getTagByName("Project1_Tag"));
    	
    	// 將 Project2 中的Project2_Tag1 修改為與Project1中的Tag名稱一樣
    	// 確認兩個名稱相同的Tag id一樣
    	long Project2_Tag1_BId = productBacklogHelper2.getTagByName("Project2_Tag1").getId();
    	mapper2.updateTag(Project2_Tag1_BId, "Tag");

    	// 判斷原本的Tag不存在 修改後的Tag存在
    	assertNull(productBacklogHelper2.getTagByName("Project2_Tag1"));
    	assertNotNull(productBacklogHelper2.getTagByName("Tag"));
    }
    
    public void testAddStoryTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Project2_Tag1");
    	
    	// 將Story加上Tag
    	long tagP11_Id = productBacklogHelper1.getTagByName("Tag").getId();
    	long tagP12_Id = productBacklogHelper1.getTagByName("Project1_Tag1").getId();
    	long tagP21_Id = productBacklogHelper2.getTagByName("Project2_Tag1").getId();
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tagP11_Id);
    	productBacklogHelper1.addStoryTag(story1_Id, tagP12_Id);
    	productBacklogHelper2.addStoryTag(story2_Id, tagP21_Id);
    	
    	// 取得Story的Tag List
    	List<TagObject> story1_Tags = this.productBacklogLogic1.getStories()[0].getTags();
    	List<TagObject> story2_Tags = this.productBacklogLogic2.getStories()[0].getTags();
    	
    	// 確認Tag
    	assertEquals(2, story1_Tags.size());
    	assertEquals(1, story2_Tags.size());
    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getId(), story1_Tags.get(0).getId());
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getName(), story1_Tags.get(0).getName());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getId(), story1_Tags.get(1).getId());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getName(), story1_Tags.get(1).getName());
    	
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getId(), story2_Tags.get(0).getId());
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getName(), story2_Tags.get(0).getName());
    }
    
    public void testRemoveStoryTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Project2_Tag1");
    	
    	// 將Story加上Tag
    	long tagP11_Id = productBacklogHelper1.getTagByName("Tag").getId();
    	long tagP12_Id = productBacklogHelper1.getTagByName("Project1_Tag1").getId();
    	long tagP21_Id = productBacklogHelper2.getTagByName("Project2_Tag1").getId();
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tagP11_Id);
    	productBacklogHelper1.addStoryTag(story1_Id, tagP12_Id);
    	productBacklogHelper1.addStoryTag(story2_Id, tagP21_Id);
    	
    	// 取得Story的Tag List
    	List<TagObject> story1_Tags = this.productBacklogLogic1.getStories()[0].getTags();
    	List<TagObject> story2_Tags = this.productBacklogLogic2.getStories()[0].getTags();
    	
    	// 確認Tag
    	assertEquals(2, story1_Tags.size());
    	assertEquals(1, story2_Tags.size());
    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getId(), story1_Tags.get(0).getId());
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getName(), story1_Tags.get(0).getName());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getId(), story1_Tags.get(1).getId());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getName(), story1_Tags.get(1).getName());
    	
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getId(), story2_Tags.get(0).getId());
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getName(), story2_Tags.get(0).getName());
    	
    	// 移除Story的Tag
    	productBacklogHelper1.removeStoryTag(story1_Id, tagP12_Id);
    	productBacklogHelper1.removeStoryTag(story2_Id, tagP21_Id);
    	
    	// 取得Story的Tag List
    	story1_Tags.clear();
    	story1_Tags = this.productBacklogLogic1.getStories()[0].getTags();
    	story2_Tags.clear();
    	story2_Tags = this.productBacklogLogic2.getStories()[0].getTags();
    	
    	// 確認Tag
    	assertEquals(1, story1_Tags.size());
    	assertEquals(0, story2_Tags.size());
    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getId(), story1_Tags.get(0).getId());
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getName(), story1_Tags.get(0).getName());
    }
    
    // 兩個專案各有相同名稱之Tag，並將Story都標記此Tag
    // 在Project1修改Tag名稱後，確認Proejct1的Story Tag名稱變更
    // 並確認Project2的Tag存在且與Story的Tag不會被變更
    public void testTagScenario1(){
    	productBacklogHelper1.addNewTag("Tag");    	
    	productBacklogHelper2.addNewTag("Tag");

    	// 將Story加上Tag
    	long tag_Id = productBacklogHelper1.getTagByName("Tag").getId();
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tag_Id);
    	productBacklogHelper2.addStoryTag(story2_Id, tag_Id);
    	
    	// 修改Project1的Tag
    	mapper1.updateTag(tag_Id, "ModifyTag");
    	
    	// 確認Proejct1的Story Tag名稱變更
    	TagObject m_Tag = productBacklogHelper1.getTagByName("ModifyTag");
    	TagObject story1_Tag = this.productBacklogLogic2.getStories()[0].getTags().get(0);
    	
    	assertFalse(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper1.isTagExist("ModifyTag"));
    	
    	assertEquals(m_Tag.getId(), story1_Tag.getId());
    	assertEquals(m_Tag.getName(), story1_Tag.getName());
    	
    	// 確認Project2的Tag存在且與Story的Tag不會被變更
    	TagObject story2_Tag = this.productBacklogLogic2.getStories()[0].getTags().get(0);
    	
    	assertTrue(productBacklogHelper2.isTagExist("Tag"));
    	
    	assertEquals(story2_Tag.getId(), story2_Tag.getId());
    	assertEquals(story2_Tag.getName(), story2_Tag.getName());
    }
    
    // 兩個專案各有相同名稱之Tag，並將Story都標記此Tag
    // 在Project1刪除Tag名稱後，確認Proejct1的Story Tag被移除
    // 並確認Project2的Tag存在且與Story的Tag不會被變更
    public void testTagScenario2(){
    	productBacklogHelper1.addNewTag("Tag");    	
    	productBacklogHelper2.addNewTag("Tag");
    	
    	// 將Story加上Tag
    	long tag_Id1 = productBacklogHelper1.getTagByName("Tag").getId();
    	long tag_Id2 = productBacklogHelper2.getTagByName("Tag").getId();
    	
    	IStory story1 = this.productBacklogLogic1.getStories()[0];
    	IStory story2 = this.productBacklogLogic2.getStories()[0];
    	
    	String story1_Id = Long.toString(story1.getIssueID());
    	String story2_Id = Long.toString(story2.getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tag_Id1);
    	productBacklogHelper2.addStoryTag(story2_Id, tag_Id2);
    	
    	// 修改Project1的Tag
    	productBacklogHelper1.deleteTag(tag_Id1);
    	
    	// 確認Proejct1的Tag被移除 Project2的存在 
    	assertFalse(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper2.isTagExist("Tag"));
    	
    	// 確認Project1的Story Tag 被移除
    	List<TagObject> story1_Tag = this.productBacklogLogic1.getStories()[0].getTags();
    	assertEquals(0, story1_Tag.size());
    	
    	// 確認Project2的Story的Tag不會被變更
    	List<TagObject> story2_Tag = this.productBacklogLogic2.getStories()[0].getTags();
    	TagObject Tag = productBacklogHelper2.getTagByName("Tag");
    	
    	assertEquals(1, story2_Tag.size());	
    	
    	assertEquals(Tag.getId(), story2_Tag.get(0).getId());
    	assertEquals(Tag.getName(), story2_Tag.get(0).getName());
    }

    // 驗證 Story 狀態為 Done 時，不顯示
    public void testgetAddableStories1() throws Exception {
    	initialSQLData();
    	
		this.CP = new CreateProject(1);
		this.CP.exeCreate();	// 新增一專案
		
		this.CPB = new CreateProductBacklog(10, this.CP);
		this.CPB.exe();	// 新增十筆 Story
		
		CreateSprint CS = new CreateSprint(1, this.CP);
		CS.exe();		// 新增一 Sprint 
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(this.CP.getProjectList().get(0), configuration.getUserSession(), null);
		
		// 將第一筆 Story Done
		sprintBacklogLogic.closeStory(this.CPB.getIssueList().get(0).getIssueID(), this.CPB.TEST_STORY_NOTES + "1", null);
		
		List<IStory> AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(9, AvailabelIssue.size());

		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		// 將第十筆 Story Done
		sprintBacklogLogic.closeStory(this.CPB.getIssueList().get(9).getIssueID(), this.CPB.TEST_STORY_NOTES + "10", null);
		
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(8, AvailabelIssue.size());

		// 從 ID 第二筆開始驗證到第九筆
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
    }
    
    // 驗證 Story 存在於 Release 內，被查詢時不能出現
    public void testgetAddableStories2() throws Exception {
    	initialSQLData();

		this.CP = new CreateProject(1);
		this.CP.exeCreate();	// 新增一專案
		
		this.CPB = new CreateProductBacklog(10, this.CP);
		this.CPB.exe();	// 新增十筆 Story
		
		CreateRelease CR = new CreateRelease(1, this.CP);
		CR.exe();		// 新增一筆 ReleasePlan
		
		// 將第一筆 Story 加入 Release 1 內
		ArrayList<Long> StoryID = new ArrayList<Long>();
		StoryID.add(this.CPB.getIssueList().get(0).getIssueID());
		this.productBacklogLogic1.addReleaseTagToIssue(StoryID, Integer.toString(1));
				
		List<IStory> AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(9, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		// 再將三筆 Story 加入 Release 1 內
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(2).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(3).getIssueID());
//		this.helper.addRelease(StoryID, Integer.toString(1));
		this.productBacklogLogic1.addReleaseTagToIssue(StoryID, Integer.toString(1));
		
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(6, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+5), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+5), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+5), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+5), AvailabelIssue.get(i).getNotes());
		}
    }
    
    // 驗證 Story 存在於 Sprint 內，被查詢時不能出現
    public void testgetAddableStories3() throws Exception {
    	initialSQLData();
    	
		this.CP = new CreateProject(1);
    	this.CP.exeCreate();	// 新增一專案
    			
    	this.CPB = new CreateProductBacklog(10, this.CP);
    	this.CPB.exe();	// 新增十筆 Story
    			
    	CreateSprint CS = new CreateSprint(1, this.CP);
    	CS.exe();		// 新增一筆 SprintPlan
    	
    	// 將一筆 Story 加入 Sprint
    	ArrayList<Long> StoryID = new ArrayList<Long>();
    	StoryID.add(this.CPB.getIssueList().get(0).getIssueID());
//    	this.helper.add(StoryID, Integer.toString(1));
    	this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(1));
    	
		List<IStory> AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(9, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		// 再將三筆 Story 加入 Sprint 1 內
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(2).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(3).getIssueID());
//		this.helper.add(StoryID, Integer.toString(1));
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(1));
		
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(6, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+5), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+5), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+5), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+5), AvailabelIssue.get(i).getNotes());
		}
    }
    
    // 驗證 Story 存在於 Sprint 或 Release 內，被查詢時不能出現
    public void testgetAddableStories4() throws Exception {
    	initialSQLData();
    	
		this.CP = new CreateProject(1);
    	this.CP.exeCreate();	// 新增一專案
    			
    	this.CPB = new CreateProductBacklog(10, this.CP);
    	this.CPB.exe();	// 新增十筆 Story
    			
    	CreateSprint CS = new CreateSprint(1, this.CP);
    	CS.exe();		// 新增一筆 SprintPlan  
    	
		CreateRelease CR = new CreateRelease(1, this.CP);
		CR.exe();		// 新增一筆 ReleasePlan
		
		// 將第一筆 Story 加入 Sprint 1 內
		ArrayList<Long> StoryID = new ArrayList<Long>();
		StoryID.add(this.CPB.getIssueList().get(0).getIssueID());
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(1));
		
		List<IStory> AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(9, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		
		// 將第二筆 Story 加入 Release 1 內
		StoryID.remove(0);
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
		this.productBacklogLogic1.addReleaseTagToIssue(StoryID, Integer.toString(1));
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(8, AvailabelIssue.size());
		
		// 從 ID 第三筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+3), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+3), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+3), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+3), AvailabelIssue.get(i).getNotes());
		}
		
		
		// 將第十筆 Story 加入 Release 1 & Sprint 1 內
		StoryID.remove(0);
		StoryID.add(this.CPB.getIssueList().get(9).getIssueID());
		this.productBacklogLogic1.addReleaseTagToIssue(StoryID, Integer.toString(1));
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(1));
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(7, AvailabelIssue.size());
		
		// 從 ID 第三筆開始驗證到第九筆
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+3), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+3), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+3), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+3), AvailabelIssue.get(i).getNotes());
		}
		
		
		// 將剩下不為 Release 亦 不為 Sprint 的 Story 狀態設定為 Done
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(this.CP.getProjectList().get(0), configuration.getUserSession(), null);
		
		// 將第三筆 ~ 九筆 Story Done
		for (int i=2 ; i<9 ; i++) {
			Long ID = this.CPB.getIssueList().get(i).getIssueID();
			sprintBacklogLogic.closeStory(ID, this.CPB.TEST_STORY_NOTES + "1", null);
		}
		
		// 驗證取出 0 筆資料
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(0, AvailabelIssue.size());
    }
    
    // 驗證 Story 存在於 Sprint 而 Sprint 存在於 Release 內，被查詢時不能出現
    public void testgetAddableStories5() throws Exception {
    	initialSQLData();
    	
		this.CP = new CreateProject(1);
    	this.CP.exeCreate();	// 新增一專案
    			
    	this.CPB = new CreateProductBacklog(10, this.CP);
    	this.CPB.exe();	// 新增十筆 Story
    			
		CreateRelease CR = new CreateRelease(2, this.CP);
		CR.exe();		// 新增一筆 ReleasePlan
		
		AddSprintToRelease ASTR = new AddSprintToRelease(2, CR, this.CP);
		ASTR.exe();		// 將兩筆 Sprint 加入 Release 內
		
		// 將五筆 Story 加入 Sprint 1
		ArrayList<Long> StoryID = new ArrayList<Long>();
		StoryID.add(this.CPB.getIssueList().get(0).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(2).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(3).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(4).getIssueID());
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(1));
		
		// 驗證顯示剩下的 6-10 筆 StoryID
		List<IStory> AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(5, AvailabelIssue.size());
		
		// 從 ID 第六筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+6), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+6), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+6), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+6), AvailabelIssue.get(i).getNotes());
		}
		
		// 將三筆 Story (ID = 6, 7, 8) 加入 Sprint 2
		StoryID.clear();
		StoryID.add(this.CPB.getIssueList().get(5).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(6).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(7).getIssueID());
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(2));
		
		// 驗證顯示剩下的 9, 10 筆 StoryID
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(2, AvailabelIssue.size());
		
		// 從 ID 第九筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+9), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+9), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+9), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+9), AvailabelIssue.get(i).getNotes());
		}
		
		// 將兩筆 Story (ID = 9, 10) 加入 Release 1
		StoryID.clear();
		StoryID.add(this.CPB.getIssueList().get(8).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(9).getIssueID());
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
		this.productBacklogLogic1.addReleaseTagToIssue(StoryID, Integer.toString(1));
		
		// 驗證顯示 0 筆資料
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(0, AvailabelIssue.size());
		
		// 將兩筆 Story (ID = 1, 2, 9, 10) 加入 Release 2
		StoryID.add((long)1);
		StoryID.add((long)2);
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
		this.productBacklogLogic1.addReleaseTagToIssue(StoryID, Integer.toString(2));
		
		// 驗證顯示 0 筆資料
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(0, AvailabelIssue.size());
		
		
		// 將兩筆 Story (ID = 9, 10) 加入 Sprint 3 (Sprint 3 不存在)
		StoryID.clear();
		StoryID.add((long)9);
		StoryID.add((long)10);
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(3));
		
		// 驗證顯示 0 筆資料
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(0, AvailabelIssue.size());

		// 將兩筆 Story (ID = 7, 8) 移除 Sprint 2
		StoryID.clear();
		StoryID.add((long)7);
		StoryID.add((long)8);
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
		this.productBacklogLogic1.addStoriesToSprint(StoryID, Integer.toString(0));
		
		// 驗證顯示 2 筆資料
		AvailabelIssue = this.productBacklogLogic1.getAddableStories();
		assertEquals(2, AvailabelIssue.size());
		
		// 從 ID 第七筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+7), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+7), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.mTestStoryName + Long.toString(i+7), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+7), AvailabelIssue.get(i).getNotes());
		}
    }
    
	// 資料復原
	private void initialSQLData() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
	}
}
