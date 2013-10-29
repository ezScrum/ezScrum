package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.internal.IssueTag;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateTag {
	private static Log log = LogFactory.getLog(CreateTag.class);
	
	private CreateProject CP = null;
	private CreateProductBacklog CPB = null;
	private int TagCount = 1;
	public String TEST_TAG_NAME = "TEST_TAG_";	// Tag Name
	
	private IProject m_project;
	private IUserSession m_userSession;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	private ArrayList<IIssueTag> TagList = new ArrayList<IIssueTag>();
//	private ProductBacklog m_backlog = null;
	private ProductBacklogMapper productBacklogMapper = null;

	public CreateTag(int tagcount, CreateProject cp){
		this.TagCount = tagcount;
		this.CP = cp;
	}
	
	//create tag in project
	public void exe(){
		m_userSession = config.getUserSession();
		for (int projectIndex = 0 ; projectIndex < this.CP.getProjectList().size() ; projectIndex++) {
			m_project = this.CP.getProjectList().get(projectIndex);		// TEST_PROJECT_X
//			m_backlog = new ProductBacklog(m_project, m_userSession);
			this.productBacklogMapper = new ProductBacklogMapper(m_project, m_userSession);
			for(int tagIndex = 0; tagIndex < this.TagCount; tagIndex++){
//				m_backlog.addNewTag(TEST_TAG_NAME + Integer.toString(tagIndex+1));		//TEST_TAG_Y
				this.productBacklogMapper.addNewTag(TEST_TAG_NAME + Integer.toString(tagIndex+1));		//TEST_TAG_Y
				
				IIssueTag issueTag = new IssueTag();
				issueTag.setTagId((long)tagIndex+1);
				issueTag.setTagName(TEST_TAG_NAME + Integer.toString(tagIndex+1));
				TagList.add(issueTag);
			}
			log.info("Project " + this.CP.getProjectList().get(projectIndex).getName() + " create " + this.TagCount + " Tags success.");
		}
	}
	
	//attach tag to issue
	public void attachTagToStory(CreateProductBacklog cpb){
		this.CPB = cpb;
		long storyId;
		for (int projectIndex = 0 ; projectIndex < this.CP.getProjectList().size() ; projectIndex++){
			m_project = this.CP.getProjectList().get(projectIndex);		// TEST_PROJECT_X
//			m_backlog = new ProductBacklog(m_project, m_userSession);
			this.productBacklogMapper = new ProductBacklogMapper(m_project, m_userSession);
			for(int pbIndex = 0; pbIndex < this.CPB.getIssueList().size(); pbIndex++){
				for(int tagIndex = 0; tagIndex < this.TagCount; tagIndex++){
					storyId = this.CPB.getIssueList().get(pbIndex).getIssueID();
//					m_backlog.addStoryTag(String.valueOf(storyId), String.valueOf(tagIndex+1));
					this.productBacklogMapper.addStoryTag(String.valueOf(storyId), String.valueOf(tagIndex+1));
					log.info("Project " + this.CP.getProjectList().get(projectIndex).getName() 
							+ " TEST_STORY_" + String.valueOf(storyId) + " attach " 
							+ " TEST_TAG_" + String.valueOf(tagIndex+1) + " 成功");
				}
			}
		}
	}
	
	public ArrayList<IIssueTag> getTagList(){
		return this.TagList;
	}
}
