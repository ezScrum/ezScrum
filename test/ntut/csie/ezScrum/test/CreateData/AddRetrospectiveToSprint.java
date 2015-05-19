package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

public class AddRetrospectiveToSprint {
	private static Log mlog = LogFactory.getLog(AddRetrospectiveToSprint.class);
	// retrospective相關參數
	private int mGoodCount = 1;	
	private int mImprovementCount = 1;
	private List<IIssue> mGoods = new LinkedList<IIssue>();
	private List<IIssue> mImprovements = new LinkedList<IIssue>();	
	// 
	private int mSprintCount = 1;
	private int mProjectCount = 1;
	private CreateProject mCP;	
	private Configuration mConfig = new Configuration();
	
	// 加入到所指定的sprint
	public AddRetrospectiveToSprint(int goodCount, int improvementCount, CreateSprint CS, CreateProject CP, String type) throws Exception {
		mGoodCount = goodCount;
		mImprovementCount = improvementCount;
		mProjectCount = CP.getProjectList().size();
		mSprintCount = CS.getSprintCount();
		mCP = CP;
//		CreateStories(EstValue, type);
	}
	
	// 加入到多個sprint	
	public AddRetrospectiveToSprint(int goodCount, int improvementCount, int SprintNumber , CreateProject CP,String type) throws Exception {		
		mGoodCount = goodCount;
		mImprovementCount = improvementCount;
		mProjectCount = CP.getProjectList().size();
		mSprintCount = SprintNumber;
		mCP = CP;	
//		CreateStories(EstValue, type);
	}
	
	public int getSprintCount() {
		return mSprintCount;
	}
	
	public List<IIssue> getIssueList() {
		return mGoods;
	}
	
	public void exe() throws Exception {
//		IUserSession userSession = config.getUserSession();
		for (int i=0 ; i<mProjectCount ; i++) {
//			String projectName = CP.PJ_NAME + Integer.toString((i+1));	// TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = mCP.getProjectList().get(i);
			// 此路徑為開發端的   TestData/MyWorkspace/
			
			// IssueList 為所有 project 所屬的所有 issues
			// sublist 為單一個 project 所屬的所有 issues
			ArrayList<Long> subList = new ArrayList<Long>();
			// =========== 將所有的 list 資料切割出每個 Project 所包含的 issue 個數 ==========
			for (int j=0 ; j<(mGoodCount*mSprintCount) ; j++) {
				subList.add(mGoods.get(i*(mGoodCount*mSprintCount) + j).getIssueID());
			}
			
			// 將 sublist 依據 sprint 個數以及每個 sprint 想要加入的 story 個數建立關聯
			for (int k=0 ; k<mSprintCount ; k++) {
				ArrayList<Long> subList_each = new ArrayList<Long>();
				for (int l=0 ; l<mGoodCount ; l++) {
					subList_each.add(subList.get((k*mGoodCount) + l));
				}
				
				addRetrospectiveToSprint(project, subList_each, Integer.toString(k+1) );
				mlog.info("專案 " + project.getName() + ", 第 " + (k+1) + " 個 sprint 加入 " + mGoodCount + " 個 stories 成功");
			}
		}
	}
	
	// create new story list
	private void CreateStories(int EstValue, String type) throws Exception {
//		int TotalStory = GoodCount * SprintCount;
//		CreateProductBacklog createStory = new CreateProductBacklog(TotalStory, EstValue, CP, type);
//		createStory.exe();
		
//		IssueList.addAll(createStory.getIssueList());
	}
	
	private void addRetrospectiveToSprint(IProject iproject, ArrayList<Long> list, String sprintID) {
		// ProductBacklog pb = new ProductBacklog(p, config.getUserSession());
		ProjectObject project = new ProjectObject(iproject.getName());
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(project);
		
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
