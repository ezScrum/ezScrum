package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

public class AddStoryToSprint {
	private static Log log = LogFactory.getLog(AddStoryToSprint.class);

	private int EachCount = 1;
	private int SprintCount = 1;
	private int ProjectCount = 1;
	private CreateProject CP;
	private List<IIssue> IssueList = new LinkedList<IIssue>();
	private Configuration configuration = new Configuration();

	public AddStoryToSprint(int count, int EstValue, CreateSprint cs, CreateProject cp, String type) throws Exception {
		this.EachCount = count;
		this.ProjectCount = cp.getProjectList().size();
		this.SprintCount = cs.getSprintCount();
		this.CP = cp;

		CreateStories(EstValue, type);
	}

	public AddStoryToSprint(int storyCount, int EstValue, int SprintNumber, CreateProject cp, String type) throws Exception {
		EachCount = storyCount;
		ProjectCount = cp.getProjectList().size();
		SprintCount = SprintNumber;
		CP = cp;

		CreateStories(EstValue, type);
	}

	public int getSprintCount() {
		return this.SprintCount;
	}

	public List<IIssue> getIssueList() {
		return this.IssueList;
	}

	public void exe() throws Exception {
		// IUserSession userSession = this.config.getUserSession();

		for (int i = 0; i < this.ProjectCount; i++) {
			IProject project = this.CP.getProjectList().get(i);
			// 此路徑為開發端的 TestData/MyWorkspace/

			// IssueList 為所有 project 所屬的所有 issues
			// sublist 為單一個 project 所屬的所有 issues
			ArrayList<Long> subList = new ArrayList<Long>();
			// =========== 將所有的 list 資料切割出每個 Project 所包含的 issue 個數 ==========
			for (int j = 0; j < (this.EachCount * this.SprintCount); j++) {
				subList.add(this.IssueList.get(i * (this.EachCount * this.SprintCount) + j).getIssueID());
			}

			// 將 sublist 依據 sprint 個數以及每個 sprint 想要加入的 story 個數建立關聯
			for (int k = 0; k < this.SprintCount; k++) {
				ArrayList<Long> subList_each = new ArrayList<Long>();
				for (int l = 0; l < this.EachCount; l++) {
					subList_each.add(subList.get((k * this.EachCount) + l));
				}
				addStoryToSprint(project, subList_each, Integer.toString(k + 1));
				log.info("專案 " + project.getName() + ", 第 " + (k + 1) + " 個 sprint 加入 " + this.EachCount + " 個 stories 成功");
			}
		}
	}

	// create new story list
	private void CreateStories(int EstValue, String type) throws Exception {
		int TotalStory = this.EachCount * this.SprintCount;
		CreateProductBacklog createStory = new CreateProductBacklog(TotalStory, EstValue, this.CP, type);
		createStory.exe();
		this.IssueList.addAll(createStory.getIssueList());
	}

	private void addStoryToSprint(IProject p, ArrayList<Long> list, String sprintID) {
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(p, configuration.getUserSession());
		
		for (long issueId : list) {
			IIssue issue = productBacklogMapper.getIssue(issueId);
			String oldSprintID = issue.getSprintID();
			if (sprintID != null && !sprintID.equals("") && Integer.parseInt(sprintID) >= 0) {

				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);

				Date current = new Date();
				String dateTime = DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2);
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, dateTime);

				// iteration node
				Element iteration = new Element(ScrumEnum.SPRINT_ID);
				iteration.setText(sprintID);
				history.addContent(iteration);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
				productBacklogMapper.updateIssueValue(issue, false);
				if (sprintID != "0") {
					productBacklogMapper.addHistory(issue.getIssueID(), issue.getIssueType(), HistoryObject.TYPE_APPEND, oldSprintID, sprintID);	
				}
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
				productBacklogMapper.updateStoryRelation(issueId, issue.getReleaseID(), sprintID, null, null, current);
				
			}
		}
	}
}
