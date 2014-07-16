package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssueTag;
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
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;

public class ProductBacklogHelperTest extends TestCase {
	private ProductBacklogHelper productBacklogHelper;
	private ProductBacklogHelper productBacklogHelper1;
	private ProductBacklogHelper productBacklogHelper2;
	private CreateProject CP;
	private CreateProductBacklog CPB;
	private int ProjectCount = 2;
	private int StoryCount = 1;
	private ProductBacklogLogic productBacklogLogic;
	private ProductBacklogLogic productBacklogLogic1;
	private ProductBacklogLogic productBacklogLogic2;
	
//	private ProductBacklogMapper mapper;
	private ProductBacklogMapper mapper1;
	private ProductBacklogMapper mapper2;
	
//	private ntut.csie.ezScrum.web.control.ProductBacklogHelper helper;
//	private ntut.csie.ezScrum.web.control.ProductBacklogHelper helper1;
//	private ntut.csie.ezScrum.web.control.ProductBacklogHelper helper2;
	
	private Configuration configuration;
	
	public ProductBacklogHelperTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		// 新增Story	
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		IUserSession userSession = configuration.getUserSession();
		
		this.productBacklogHelper =  new ProductBacklogHelper(userSession, this.CP.getProjectList().get(0));
		this.productBacklogHelper1 = new ProductBacklogHelper(userSession, this.CP.getProjectList().get(0));
		this.productBacklogHelper2 = new ProductBacklogHelper(userSession, this.CP.getProjectList().get(1));
		
//		this.helper =  new ntut.csie.ezScrum.web.control.ProductBacklogHelper(this.CP.getProjectList().get(0), config.getUserSession());
//		this.helper1 = new ntut.csie.ezScrum.web.control.ProductBacklogHelper(this.CP.getProjectList().get(0), config.getUserSession());
//		this.helper2 = new ntut.csie.ezScrum.web.control.ProductBacklogHelper(this.CP.getProjectList().get(1), config.getUserSession());
		
		this.productBacklogLogic = new ProductBacklogLogic(userSession, this.CP.getProjectList().get(0));
		this.productBacklogLogic1 = new ProductBacklogLogic(userSession, this.CP.getProjectList().get(0));
		this.productBacklogLogic2 = new ProductBacklogLogic(userSession, this.CP.getProjectList().get(1));

//		this.mapper =  new ProductBacklogMapper(this.CP.getProjectList().get(0), config.getUserSession());
		this.mapper1 = new ProductBacklogMapper(this.CP.getProjectList().get(0), userSession);
		this.mapper2 = new ProductBacklogMapper(this.CP.getProjectList().get(1), userSession);
		
		// release
		ini = null;
    }

    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
//		CopyProject copyProject = new CopyProject(this.CP);
//    	copyProject.exeDelete_Project();					// 刪除測試檔案
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
    	
		configuration.setTestMode(false);
		configuration.store();
    	
    	// release
    	ini = null;
    	this.productBacklogHelper = null;
    	this.productBacklogHelper1 = null;
    	this.productBacklogHelper2 = null;
    	this.CP = null;
    	this.CPB = null;
    	productBacklogLogic1 = null;
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
    	assertEquals( 2, productBacklogHelper1.getTagList().length);
    	assertEquals( 3, productBacklogHelper2.getTagList().length);
    	
    	// 確認名稱
    	assertEquals( "Tag", productBacklogHelper1.getTagList()[0].getTagName());
    	assertEquals( "Project1_Tag1", productBacklogHelper1.getTagList()[1].getTagName());
    	
    	assertEquals( "Tag", productBacklogHelper2.getTagList()[0].getTagName());
    	assertEquals( "Project2_Tag1", productBacklogHelper2.getTagList()[1].getTagName());
    	assertEquals( "Project2_Tag2", productBacklogHelper2.getTagList()[2].getTagName());
    	
    	// 確認相同名稱的Tag id 需要一樣
    	long id1 = productBacklogHelper1.getTagByName("Tag").getTagId();
    	long id2 = productBacklogHelper2.getTagByName("Tag").getTagId();
    	assertNotNull(id1);
    	assertNotNull(id2);
    	assertTrue(id1 == id2);
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
    	
    	IIssueTag tag1 = productBacklogHelper1.getTagList()[0];
    	IIssueTag tag2 = productBacklogHelper1.getTagList()[1];
    	
    	// 確認Tag
    	assertTrue(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper1.isTagExist("Project1_Tag1"));
    	
    	assertEquals(tag1.getTagName(), productBacklogHelper1.getTagByName("Tag").getTagName());
    	assertEquals(tag1.getTagId(), productBacklogHelper1.getTagByName("Tag").getTagId());
    	
    	assertEquals(tag2.getTagName(), productBacklogHelper1.getTagByName("Project1_Tag1").getTagName());
    	assertEquals(tag2.getTagId(), productBacklogHelper1.getTagByName("Project1_Tag1").getTagId());
    }
    
    public void testDeleteTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	// 確認存在
    	assertTrue(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper1.isTagExist("Project1_Tag1"));
    	
    	// 刪除Tag
    	productBacklogHelper1.deleteTag(Long.toString(productBacklogHelper1.getTagByName("Tag").getTagId()));
    	productBacklogHelper1.deleteTag(Long.toString(productBacklogHelper1.getTagByName("Project1_Tag1").getTagId()));
    	
    	// 確認不存在
    	assertFalse(productBacklogHelper1.isTagExist("Tag"));
    	assertFalse(productBacklogHelper1.isTagExist("Project1_Tag1"));    	
    }
    
    public void testGetTagList() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	// 確認數量
    	assertEquals(2, productBacklogHelper1.getTagList().length);
    	
    	// 確認名稱
    	assertEquals("Tag", productBacklogHelper1.getTagList()[0].getTagName());
    	assertEquals("Project1_Tag1", productBacklogHelper1.getTagList()[1].getTagName());
    }
    
    public void testUpdateTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Project2_Tag1");

    	// 將 Project1_Tag1 修改為新名稱
    	String Project1_Tag1_BId = Long.toString(productBacklogHelper1.getTagByName("Project1_Tag1").getTagId());
//    	helper1.updateTag(Project1_Tag1_BId, "Project1_Tag");
    	mapper1.updateTag(Project1_Tag1_BId, "Project1_Tag");

    	// 判斷原本的Tag不存在 修改後的Tag存在
    	assertNull(productBacklogHelper1.getTagByName("Project1_Tag1"));
    	assertNotNull(productBacklogHelper1.getTagByName("Project1_Tag"));
    	
    	// 將 Project2 中的Project2_Tag1 修改為與Project1中的Tag名稱一樣
    	// 確認兩個名稱相同的Tag id一樣
    	String Project2_Tag1_BId = Long.toString(productBacklogHelper2.getTagByName("Project2_Tag1").getTagId());
//    	helper2.updateTag(Project2_Tag1_BId, "Tag");
    	mapper2.updateTag(Project2_Tag1_BId, "Tag");

    	// 判斷原本的Tag不存在 修改後的Tag存在
    	assertNull(productBacklogHelper2.getTagByName("Project2_Tag1"));
    	assertNotNull(productBacklogHelper2.getTagByName("Tag"));

    	// 確認兩個名稱相同的Tag id一樣    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagId(), productBacklogHelper2.getTagByName("Tag").getTagId());
    }
    
    public void testAddStoryTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Project2_Tag1");
    	
    	// 將Story加上Tag
    	String tagP11_Id = Long.toString(productBacklogHelper1.getTagByName("Tag").getTagId());
    	String tagP12_Id = Long.toString(productBacklogHelper1.getTagByName("Project1_Tag1").getTagId());
    	String tagP21_Id = Long.toString(productBacklogHelper2.getTagByName("Project2_Tag1").getTagId());
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tagP11_Id);
    	productBacklogHelper1.addStoryTag(story1_Id, tagP12_Id);
    	productBacklogHelper1.addStoryTag(story2_Id, tagP21_Id);
    	
    	// 取得Story的Tag List
    	List<IIssueTag> story1_Tags = this.productBacklogLogic1.getStories()[0].getTag();
    	List<IIssueTag> story2_Tags = this.productBacklogLogic2.getStories()[0].getTag();
    	
    	// 確認Tag
    	assertEquals(2, story1_Tags.size());
    	assertEquals(1, story2_Tags.size());
    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagId(), story1_Tags.get(0).getTagId());
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagName(), story1_Tags.get(0).getTagName());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getTagId(), story1_Tags.get(1).getTagId());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getTagName(), story1_Tags.get(1).getTagName());
    	
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getTagId(), story2_Tags.get(0).getTagId());
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getTagName(), story2_Tags.get(0).getTagName());
    }
    
    public void testRemoveStoryTag() {
    	// 新增Tag
    	productBacklogHelper1.addNewTag("Tag");
    	productBacklogHelper1.addNewTag("Project1_Tag1");
    	
    	productBacklogHelper2.addNewTag("Project2_Tag1");
    	
    	// 將Story加上Tag
    	String tagP11_Id = Long.toString(productBacklogHelper1.getTagByName("Tag").getTagId());
    	String tagP12_Id = Long.toString(productBacklogHelper1.getTagByName("Project1_Tag1").getTagId());
    	String tagP21_Id = Long.toString(productBacklogHelper2.getTagByName("Project2_Tag1").getTagId());
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tagP11_Id);
    	productBacklogHelper1.addStoryTag(story1_Id, tagP12_Id);
    	productBacklogHelper1.addStoryTag(story2_Id, tagP21_Id);
    	
    	// 取得Story的Tag List
    	List<IIssueTag> story1_Tags = this.productBacklogLogic1.getStories()[0].getTag();
    	List<IIssueTag> story2_Tags = this.productBacklogLogic2.getStories()[0].getTag();
    	
    	// 確認Tag
    	assertEquals(2, story1_Tags.size());
    	assertEquals(1, story2_Tags.size());
    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagId(), story1_Tags.get(0).getTagId());
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagName(), story1_Tags.get(0).getTagName());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getTagId(), story1_Tags.get(1).getTagId());
    	assertEquals(productBacklogHelper1.getTagByName("Project1_Tag1").getTagName(), story1_Tags.get(1).getTagName());
    	
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getTagId(), story2_Tags.get(0).getTagId());
    	assertEquals(productBacklogHelper2.getTagByName("Project2_Tag1").getTagName(), story2_Tags.get(0).getTagName());
    	
    	// 移除Story的Tag
    	productBacklogHelper1.removeStoryTag(story1_Id, tagP12_Id);
    	productBacklogHelper1.removeStoryTag(story2_Id, tagP21_Id);
    	
    	// 取得Story的Tag List
    	story1_Tags.clear();
    	story1_Tags = this.productBacklogLogic1.getStories()[0].getTag();
    	story2_Tags.clear();
    	story2_Tags = this.productBacklogLogic2.getStories()[0].getTag();
    	
    	// 確認Tag
    	assertEquals(1, story1_Tags.size());
    	assertEquals(0, story2_Tags.size());
    	
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagId(), story1_Tags.get(0).getTagId());
    	assertEquals(productBacklogHelper1.getTagByName("Tag").getTagName(), story1_Tags.get(0).getTagName());
    }
    
    // 兩個專案各有相同名稱之Tag，並將Story都標記此Tag
    // 在Project1修改Tag名稱後，確認Proejct1的Story Tag名稱變更
    // 並確認Project2的Tag存在且與Story的Tag不會被變更
    public void testTagScenario1(){
    	productBacklogHelper1.addNewTag("Tag");    	
    	productBacklogHelper2.addNewTag("Tag");

    	// 將Story加上Tag
    	String tag_Id = Long.toString(productBacklogHelper1.getTagByName("Tag").getTagId());
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tag_Id);
    	productBacklogHelper2.addStoryTag(story2_Id, tag_Id);
    	
    	// 修改Project1的Tag
//    	helper1.updateTag(tag_Id, "ModifyTag");
    	mapper1.updateTag(tag_Id, "ModifyTag");
    	
    	// 確認Proejct1的Story Tag名稱變更
    	IIssueTag m_Tag = productBacklogHelper1.getTagByName("ModifyTag");
    	IIssueTag story1_Tag = this.productBacklogLogic1.getStories()[0].getTag().get(0);
    	
    	assertFalse(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper1.isTagExist("ModifyTag"));
    	
    	assertEquals(m_Tag.getTagId(), story1_Tag.getTagId());
    	assertEquals(m_Tag.getTagName(), story1_Tag.getTagName());
    	
    	// 確認Project2的Tag存在且與Story的Tag不會被變更
    	IIssueTag story2_Tag = this.productBacklogLogic2.getStories()[0].getTag().get(0);
    	
    	assertTrue(productBacklogHelper2.isTagExist("Tag"));
    	
    	assertEquals(story2_Tag.getTagId(), story2_Tag.getTagId());
    	assertEquals(story2_Tag.getTagName(), story2_Tag.getTagName());
    }
    
    // 兩個專案各有相同名稱之Tag，並將Story都標記此Tag
    // 在Project1刪除Tag名稱後，確認Proejct1的Story Tag被移除
    // 並確認Project2的Tag存在且與Story的Tag不會被變更
    public void testTagScenario2(){
    	productBacklogHelper1.addNewTag("Tag");    	
    	productBacklogHelper2.addNewTag("Tag");
    	
    	// 將Story加上Tag
    	String tag_Id = Long.toString(productBacklogHelper1.getTagByName("Tag").getTagId());
    	
    	String story1_Id = Long.toString(this.productBacklogLogic1.getStories()[0].getIssueID());
    	String story2_Id = Long.toString(this.productBacklogLogic2.getStories()[0].getIssueID());
    	
    	productBacklogHelper1.addStoryTag(story1_Id, tag_Id);
    	productBacklogHelper2.addStoryTag(story2_Id, tag_Id);
    	
    	// 修改Project1的Tag
    	productBacklogHelper1.deleteTag(tag_Id);
    	
    	// 確認Proejct1的Tag被移除 Project2的存在
    	assertFalse(productBacklogHelper1.isTagExist("Tag"));
    	assertTrue(productBacklogHelper2.isTagExist("Tag"));
    	
    	// 確認Project1的Story Tag 被移除
    	List<IIssueTag> story1_Tag = this.productBacklogLogic1.getStories()[0].getTag();
    	assertEquals(0, story1_Tag.size());
    	
    	// 確認Project2的Story的Tag不會被變更
    	List<IIssueTag> story2_Tag = this.productBacklogLogic2.getStories()[0].getTag();
    	IIssueTag Tag = productBacklogHelper2.getTagByName("Tag");
    	
    	assertEquals(1, story2_Tag.size());
    	
    	assertEquals(Tag.getTagId(), story2_Tag.get(0).getTagId());
    	assertEquals(Tag.getTagName(), story2_Tag.get(0).getTagName());
    }
    
    
// ==========================================================================================================
// 有錯誤的 bug
//    public void testgetStories() throws Exception {
//    	this.helper = new ProductBacklogHelper(this.CP.getIProjectList().get(0), ezScrumInfo.CreateUserSession());
//    	IStory[] stories = this.helper.getStories();
//    	
//    	for (int i=0 ; i<stories.length ; i++) {
//    		assertEquals((i+1), stories[i].getIssueID());
//    		assertEquals(ScrumEnum.STORY_ISSUE_TYPE, stories[i].getCategory());			// ====================
//    		assertEquals(this.CPB.getDefault_HOW_TO_DEMO(i+1), stories[i].getHowToDemo());
//    		assertEquals(this.CPB.getDefault_STORY_NAME(i+1), stories[i].getName());
//    		assertEquals(this.CPB.getDefault_STORY_NOTES(i+1), stories[i].getNotes());
//    		assertEquals("0", stories[i].getActualHour());
//    		assertEquals("", stories[i].getAdditional());
//    		assertEquals(0, stories[i].getAssignedDate());
//    		assertEquals("", stories[i].getAssignto());
//    		assertEquals(0, stories[i].getAttachFile().size());
//    		assertEquals("", stories[i].getDescription());
//    		assertEquals(i+1, stories[i].getIssueID());
//    		assertEquals("-1", stories[i].getSprintID());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		assertEquals(ITSEnum.S_NEW_STATUS, stories[i].getStatus());
//    		assertEquals("-1", stories[i].getReleaseID());
//    	}
//    	
//    	// 清空資料
//    	initialSQLData();
//    	
//    	// 除錯測試
//    	stories = this.helper.getStories();
//		assertEquals(0, stories.length);
//    }
//    
//	public void testgetStories_by_situation() throws Exception {
//    	this.helper = new ProductBacklogHelper(this.CP.getIProjectList().get(0), ezScrumInfo.CreateUserSession());
//    	  	
//    	String situationID = ScrumEnum.ID_ATTR;		// ID Tag
//    	String situationIMP = ScrumEnum.IMPORTANCE;	// Importance Tag
//    	
//    	String situation = "-" + situationID;		// ID 遞增排序
//    	IStory[] stories = this.helper.getStories(situation);    	
//    	for (int i=0 ; i<stories.length ; i++) {
//    		assertEquals((i+1), stories[i].getIssueID());
//    		assertEquals(ScrumEnum.STORY_ISSUE_TYPE, stories[i].getCategory());			// ====================
//    		assertEquals(this.CPB.getDefault_HOW_TO_DEMO(i+1), stories[i].getHowToDemo());
//    		assertEquals(this.CPB.getDefault_STORY_NAME(i+1), stories[i].getName());
//    		assertEquals(this.CPB.getDefault_STORY_NOTES(i+1), stories[i].getNotes());
//    		assertEquals("0", stories[i].getActualHour());
//    		assertEquals("", stories[i].getAdditional());
//    		assertEquals(0, stories[i].getAssignedDate());
//    		assertEquals("", stories[i].getAssignto());
//    		assertEquals(0, stories[i].getAttachFile().size());
//    		assertEquals("", stories[i].getDescription());
//    		assertEquals(i+1, stories[i].getIssueID());
//    		assertEquals("-1", stories[i].getSprintID());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		assertEquals(ITSEnum.S_NEW_STATUS, stories[i].getStatus());
//    		assertEquals("-1", stories[i].getReleaseID());
//    	}
//    	
//    	situation = "+" + situationID;				// ID 遞減排序
//    	stories = this.helper.getStories(situation);
//    	int j = stories.length;
//    	for (int i=0 ; i<stories.length ; i++) {
//    		assertEquals((j), stories[i].getIssueID());
//    		assertEquals(ScrumEnum.STORY_ISSUE_TYPE, stories[i].getCategory());			// ====================
//    		assertEquals(this.CPB.getDefault_HOW_TO_DEMO(j), stories[i].getHowToDemo());
//    		assertEquals(this.CPB.getDefault_STORY_NAME(j), stories[i].getName());
//    		assertEquals(this.CPB.getDefault_STORY_NOTES(j), stories[i].getNotes());
//    		assertEquals("0", stories[i].getActualHour());
//    		assertEquals("", stories[i].getAdditional());
//    		assertEquals(0, stories[i].getAssignedDate());
//    		assertEquals("", stories[i].getAssignto());
//    		assertEquals(0, stories[i].getAttachFile().size());
//    		assertEquals("", stories[i].getDescription());
//    		assertEquals(j, stories[i].getIssueID());
//    		assertEquals("-1", stories[i].getSprintID());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		assertEquals(ITSEnum.S_NEW_STATUS, stories[i].getStatus());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		j--;
//    	}
//    	
//    	situation = "-" + situationID + "," + "-" + situationIMP;	// ID 遞增排序 再 importance 遞增排序
//    	stories = this.helper.getStories(situation);    	
//    	for (int i=0 ; i<stories.length ; i++) {
//    		assertEquals((i+1), stories[i].getIssueID());
//    		assertEquals(ScrumEnum.STORY_ISSUE_TYPE, stories[i].getCategory());			// ====================
//    		assertEquals(this.CPB.getDefault_HOW_TO_DEMO(i+1), stories[i].getHowToDemo());
//    		assertEquals(this.CPB.getDefault_STORY_NAME(i+1), stories[i].getName());
//    		assertEquals(this.CPB.getDefault_STORY_NOTES(i+1), stories[i].getNotes());
//    		assertEquals("0", stories[i].getActualHour());
//    		assertEquals("", stories[i].getAdditional());
//    		assertEquals(0, stories[i].getAssignedDate());
//    		assertEquals("", stories[i].getAssignto());
//    		assertEquals(0, stories[i].getAttachFile().size());
//    		assertEquals("", stories[i].getDescription());
//    		assertEquals(i+1, stories[i].getIssueID());
//    		assertEquals("-1", stories[i].getSprintID());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		assertEquals(ITSEnum.S_NEW_STATUS, stories[i].getStatus());
//    		assertEquals("-1", stories[i].getReleaseID());
//    	}    	
//    	
//    	situation = "+" + situationID + "," + "-" + situationIMP;	//  ID 遞增排序 再 importance 遞減排序
//    	stories = this.helper.getStories(situation);
//    	j = stories.length;
//    	for (int i=0 ; i<stories.length ; i++) {
//    		assertEquals((j), stories[i].getIssueID());
//    		assertEquals(ScrumEnum.STORY_ISSUE_TYPE, stories[i].getCategory());			// ====================
//    		assertEquals(this.CPB.getDefault_HOW_TO_DEMO(j), stories[i].getHowToDemo());
//    		assertEquals(this.CPB.getDefault_STORY_NAME(j), stories[i].getName());
//    		assertEquals(this.CPB.getDefault_STORY_NOTES(j), stories[i].getNotes());
//    		assertEquals("0", stories[i].getActualHour());
//    		assertEquals("", stories[i].getAdditional());
//    		assertEquals(0, stories[i].getAssignedDate());
//    		assertEquals("", stories[i].getAssignto());
//    		assertEquals(0, stories[i].getAttachFile().size());
//    		assertEquals("", stories[i].getDescription());
//    		assertEquals(j, stories[i].getIssueID());
//    		assertEquals("-1", stories[i].getSprintID());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		assertEquals(ITSEnum.S_NEW_STATUS, stories[i].getStatus());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		j--;
//    	}
//    	
//    	situation = "-" + situationID + "," + "+" + situationIMP;	//  ID 遞減排序 再 importance 遞增排序
//    	stories = this.helper.getStories(situation);    	
//    	for (int i=0 ; i<stories.length ; i++) {
//    		assertEquals((i+1), stories[i].getIssueID());
//    		assertEquals(ScrumEnum.STORY_ISSUE_TYPE, stories[i].getCategory());			// ====================
//    		assertEquals(this.CPB.getDefault_HOW_TO_DEMO(i+1), stories[i].getHowToDemo());
//    		assertEquals(this.CPB.getDefault_STORY_NAME(i+1), stories[i].getName());
//    		assertEquals(this.CPB.getDefault_STORY_NOTES(i+1), stories[i].getNotes());
//    		assertEquals("0", stories[i].getActualHour());
//    		assertEquals("", stories[i].getAdditional());
//    		assertEquals(0, stories[i].getAssignedDate());
//    		assertEquals("", stories[i].getAssignto());
//    		assertEquals(0, stories[i].getAttachFile().size());
//    		assertEquals("", stories[i].getDescription());
//    		assertEquals(i+1, stories[i].getIssueID());
//    		assertEquals("-1", stories[i].getSprintID());
//    		assertEquals("-1", stories[i].getReleaseID());
//    		assertEquals(ITSEnum.S_NEW_STATUS, stories[i].getStatus());
//    		assertEquals("-1", stories[i].getReleaseID());
//    	}
//    }
// ==========================================================================================================    

    // 驗證 Story 狀態為 Done 時，不顯示
    public void testgetAddableStories1() throws Exception {
    	initialSQLData();
    	
		this.CP = new CreateProject(1);
		this.CP.exeCreate();	// 新增一專案
		
		this.CPB = new CreateProductBacklog(10, this.CP);
		this.CPB.exe();	// 新增十筆 Story
		
		CreateSprint CS = new CreateSprint(1, this.CP);
		CS.exe();		// 新增一 Sprint
		
//		SprintBacklogMapper S_backlog = new SprintBacklogMapper(this.CP.getProjectList().get(0), config.getUserSession());
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(this.CP.getProjectList().get(0), configuration.getUserSession(), null);
		
		// 將第一筆 Story Done
		sprintBacklogLogic.doneIssue(this.CPB.getIssueList().get(0).getIssueID(), "Story_"+0, this.CPB.TEST_STORY_NOTES + "1", null, Integer.toString(0));
		
		List<IStory> AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(9, AvailabelIssue.size());

		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		// 將第十筆 Story Done
		sprintBacklogLogic.doneIssue(this.CPB.getIssueList().get(9).getIssueID(), "Story_"+9, this.CPB.TEST_STORY_NOTES + "10", null, Integer.toString(0));
		
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(8, AvailabelIssue.size());

		// 從 ID 第二筆開始驗證到第九筆
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
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
//		this.helper.addRelease(StoryID, Integer.toString(1));
		this.productBacklogLogic.addReleaseTagToIssue(StoryID, Integer.toString(1));
				
		List<IStory> AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(9, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		// 再將三筆 Story 加入 Release 1 內
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(2).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(3).getIssueID());
//		this.helper.addRelease(StoryID, Integer.toString(1));
		this.productBacklogLogic.addReleaseTagToIssue(StoryID, Integer.toString(1));
		
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(6, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+5), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+5), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+5), AvailabelIssue.get(i).getSummary());
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
    	this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(1));
    	
		List<IStory> AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(9, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		// 再將三筆 Story 加入 Sprint 1 內
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(2).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(3).getIssueID());
//		this.helper.add(StoryID, Integer.toString(1));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(1));
		
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(6, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+5), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+5), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+5), AvailabelIssue.get(i).getSummary());
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
//		this.helper.add(StoryID, Integer.toString(1));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(1));
		
		List<IStory> AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(9, AvailabelIssue.size());
		
		// 從 ID 第二筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+2), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+2), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+2), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+2), AvailabelIssue.get(i).getNotes());
		}
		
		
		// 將第二筆 Story 加入 Release 1 內
		StoryID.remove(0);
		StoryID.add(this.CPB.getIssueList().get(1).getIssueID());
//		this.helper.addRelease(StoryID, Integer.toString(1));
		this.productBacklogLogic.addReleaseTagToIssue(StoryID, Integer.toString(1));
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(8, AvailabelIssue.size());
		
		// 從 ID 第三筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+3), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+3), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+3), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+3), AvailabelIssue.get(i).getNotes());
		}
		
		
		// 將第十筆 Story 加入 Release 1 & Sprint 1 內
		StoryID.remove(0);
		StoryID.add(this.CPB.getIssueList().get(9).getIssueID());
//		this.helper.addRelease(StoryID, Integer.toString(1));
//		this.helper.add(StoryID, Integer.toString(1));
		this.productBacklogLogic.addReleaseTagToIssue(StoryID, Integer.toString(1));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(1));
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(7, AvailabelIssue.size());
		
		// 從 ID 第三筆開始驗證到第九筆
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+3), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+3), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+3), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+3), AvailabelIssue.get(i).getNotes());
		}
		
		
		// 將剩下不為 Release 亦 不為 Sprint 的 Story 狀態設定為 Done
//		SprintBacklogMapper S_backlog = new SprintBacklogMapper(this.CP.getProjectList().get(0), config.getUserSession());
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(this.CP.getProjectList().get(0), configuration.getUserSession(), null);
		
		// 將第三筆 ~ 九筆 Story Done
		for (int i=2 ; i<9 ; i++) {
			Long ID = this.CPB.getIssueList().get(i).getIssueID();
			sprintBacklogLogic.doneIssue(ID, "Story_"+i, this.CPB.TEST_STORY_NOTES + "1", null, Integer.toString(0));
		}
		
		// 驗證取出 0 筆資料
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
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
//		this.helper.add(StoryID, Integer.toString(1));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(1));
		
		// 驗證顯示剩下的 6-10 筆 StoryID
		List<IStory> AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(5, AvailabelIssue.size());
		
		// 從 ID 第六筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+6), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+6), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+6), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+6), AvailabelIssue.get(i).getNotes());
		}
		
		// 將三筆 Story (ID = 6, 7, 8) 加入 Sprint 2
		StoryID.clear();
		StoryID.add(this.CPB.getIssueList().get(5).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(6).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(7).getIssueID());
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
//		this.helper.add(StoryID, Integer.toString(2));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(2));
		
		// 驗證顯示剩下的 9, 10 筆 StoryID
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(2, AvailabelIssue.size());
		
		// 從 ID 第九筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+9), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+9), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+9), AvailabelIssue.get(i).getSummary());
			assertEquals(this.CPB.TEST_STORY_NOTES + Long.toString(i+9), AvailabelIssue.get(i).getNotes());
		}
		
		// 將兩筆 Story (ID = 9, 10) 加入 Release 1
		StoryID.clear();
		StoryID.add(this.CPB.getIssueList().get(8).getIssueID());
		StoryID.add(this.CPB.getIssueList().get(9).getIssueID());
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
//		this.helper.addRelease(StoryID, Integer.toString(1));
		this.productBacklogLogic.addReleaseTagToIssue(StoryID, Integer.toString(1));
		
		// 驗證顯示 0 筆資料
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(0, AvailabelIssue.size());
		
		// 將兩筆 Story (ID = 1, 2, 9, 10) 加入 Release 2
		StoryID.add((long)1);
		StoryID.add((long)2);
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
//		this.helper.addRelease(StoryID, Integer.toString(2));
		this.productBacklogLogic.addReleaseTagToIssue(StoryID, Integer.toString(2));
		
		// 驗證顯示 0 筆資料
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(0, AvailabelIssue.size());
		
		
		// 將兩筆 Story (ID = 9, 10) 加入 Sprint 3 (Sprint 3 不存在)
		StoryID.clear();
		StoryID.add((long)9);
		StoryID.add((long)10);
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
//		this.helper.add(StoryID, Integer.toString(3));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(3));
		
		// 驗證顯示 0 筆資料
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(0, AvailabelIssue.size());

		// 將兩筆 Story (ID = 7, 8) 移除 Sprint 2
		StoryID.clear();
		StoryID.add((long)7);
		StoryID.add((long)8);
		Thread.sleep(1000);		// 速度太快，暫停一下，避免影響資料存的時間一樣
//		this.helper.add(StoryID, Integer.toString(0));
		this.productBacklogLogic.addIssueToSprint(StoryID, Integer.toString(0));
		
		// 驗證顯示 2 筆資料
		AvailabelIssue = this.productBacklogLogic.getAddableStories();
		assertEquals(2, AvailabelIssue.size());
		
		// 從 ID 第七筆開始驗證
		for (int i=0 ; i<AvailabelIssue.size() ; i++) {
			assertEquals((i+7), AvailabelIssue.get(i).getIssueID());
			assertEquals(this.CPB.TEST_STORY_HOW_TO_DEMO + Long.toString(i+7), AvailabelIssue.get(i).getHowToDemo());
			assertEquals(this.CPB.TEST_STORY_NAME + Long.toString(i+7), AvailabelIssue.get(i).getSummary());
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
