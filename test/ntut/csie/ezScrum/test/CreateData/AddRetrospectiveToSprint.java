package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

public class AddRetrospectiveToSprint {
	private static Log log = LogFactory.getLog(AddRetrospectiveToSprint.class);
	
	// retrospective相關參數
	private int GoodCount = 1;	
	private int ImproveCount = 1;
	private List<IIssue> GoodIssueList = new LinkedList<IIssue>();
	private List<IIssue> ImproveIssueList = new LinkedList<IIssue>();	
	// 
	private int SprintCount = 1;
	private int ProjectCount = 1;
	private CreateProject CP;	
	private Configuration configuration = new Configuration();
	
	// 加入到所指定的sprint
	public AddRetrospectiveToSprint(int goodCount, int improveCount, CreateSprint cs, CreateProject cp, String type) throws Exception {
		this.GoodCount = goodCount;
		this.ImproveCount = improveCount;
		this.ProjectCount = cp.getProjectList().size();
		this.SprintCount = cs.getSprintCount();
		this.CP = cp;
		
//		CreateStories(EstValue, type);
	}
	
	// 加入到多個sprint	
	public AddRetrospectiveToSprint(int goodCount, int improveCount, int SprintNumber , CreateProject cp,String type) throws Exception {		
		this.GoodCount = goodCount;
		this.ImproveCount = improveCount;
		this.ProjectCount = cp.getProjectList().size();
		this.SprintCount = SprintNumber;
		this.CP = cp;		
		
//		CreateStories(EstValue, type);
	}
	
	public int getSprintCount() {
		return this.SprintCount;
	}
	
	public List<IIssue> getIssueList() {
		return this.GoodIssueList;
	}
	
	public void exe() throws Exception {
//		IUserSession userSession = this.config.getUserSession();
		
		for (int i=0 ; i<this.ProjectCount ; i++) {
//			String projectName = this.CP.PJ_NAME + Integer.toString((i+1));	// TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = this.CP.getProjectList().get(i);
			// 此路徑為開發端的   TestData/MyWorkspace/
			
			// IssueList 為所有 project 所屬的所有 issues
			// sublist 為單一個 project 所屬的所有 issues
			ArrayList<Long> subList = new ArrayList<Long>();
			// =========== 將所有的 list 資料切割出每個 Project 所包含的 issue 個數 ==========
			for (int j=0 ; j<(this.GoodCount*this.SprintCount) ; j++) {
				subList.add(this.GoodIssueList.get(i*(this.GoodCount*this.SprintCount) + j).getIssueID());
			}
			
			// 將 sublist 依據 sprint 個數以及每個 sprint 想要加入的 story 個數建立關聯
			for (int k=0 ; k<this.SprintCount ; k++) {
				ArrayList<Long> subList_each = new ArrayList<Long>();
				for (int l=0 ; l<this.GoodCount ; l++) {
					subList_each.add(subList.get((k*this.GoodCount) + l));
				}
				
				addRetrospectiveToSprint(project, subList_each, Integer.toString(k+1) );
				log.info("專案 " + project.getName() + ", 第 " + (k+1) + " 個 sprint 加入 " + this.GoodCount + " 個 stories 成功");
			}
		}
	}
	
	// create new story list
	private void CreateStories(int EstValue, String type) throws Exception {
//		int TotalStory = this.GoodCount * this.SprintCount;
//		CreateProductBacklog createStory = new CreateProductBacklog(TotalStory, EstValue, this.CP, type);
//		createStory.exe();
		
//		this.IssueList.addAll(createStory.getIssueList());
	}
	
	private void addRetrospectiveToSprint(IProject p, ArrayList<Long> list, String sprintID) {
//		ProductBacklog pb = new ProductBacklog(p, config.getUserSession());
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(p, configuration.getUserSession());
		
		for (long issueID : list) {
//			IIssue issue = pb.getIssue(issueID);
			IIssue issue = productBacklogMapper.getIssue(issueID);
			String oldSprintID = issue.getSprintID();
			if (sprintID != null && !sprintID.equals("")
					&& Integer.parseInt(sprintID) >= 0) {

				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);

				Date current = new Date();
				String dateTime = DateUtil.format(current,
						DateUtil._16DIGIT_DATE_TIME_2);
				// history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
				// IIssue.STORY_TYPE_HSITORY_VALUE);
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, dateTime);

				// iteration node
				Element iteration = new Element(ScrumEnum.SPRINT_ID);
				iteration.setText(sprintID);
				history.addContent(iteration);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
//				pb.updateTagValue(issue);
//				pb.addHistory(issue.getIssueID(), ScrumEnum.SPRINT_TAG, oldSprintID, sprintID);
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
//				pb.updateStoryRelation(Long.toString(issueID), issue
//						.getReleaseID(), sprintID, null, null, current);
				
				// 最後將修改的結果更新至DB
				productBacklogMapper.updateIssueValue(issue, true);
				productBacklogMapper.addHistory(issue.getIssueID(), issue.getIssueType(), HistoryObject.TYPE_APPEND, oldSprintID, sprintID);
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
				productBacklogMapper.updateStoryRelation(issueID, issue.getReleaseID(), sprintID, null, null, current);
			}
		}
	}
}
