package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
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
	private static Log mlog = LogFactory.getLog(AddStoryToSprint.class);
	private int mStoryCount; // Number of Stories each sprint
	private int mSprintCount;
	private int mProjectCount;
	private CreateProject mCP;
	private ArrayList<IIssue> mStories = new ArrayList<IIssue>();
	private Configuration mConfig = new Configuration();

	public AddStoryToSprint(int storyCount, int estimate, CreateSprint CS,
			CreateProject CP, String type) throws Exception {
		mStoryCount = storyCount;
		mProjectCount = CP.getProjectList().size();
		mSprintCount = CS.getSprintCount();
		mCP = CP;
		CreateStories(estimate, type);
	}

	public AddStoryToSprint(int storyCount, int estimate, int sprintCount,
			CreateProject CP, String type) throws Exception {
		mStoryCount = storyCount;
		mProjectCount = CP.getProjectList().size();
		mSprintCount = sprintCount;
		mCP = CP;
		CreateStories(estimate, type);
	}

	public int getSprintCount() {
		return mSprintCount;
	}

	public ArrayList<IIssue> getStories() {
		return mStories;
	}

	public void exe() throws Exception {
		for (int i = 0; i < mProjectCount; i++) {
			IProject project = mCP.getProjectList().get(i);
			// 此路徑為開發端的 TestData/MyWorkspace/

			// IssueList 為所有 project 所屬的所有 issues
			// sublist 為單一個 project 所屬的所有 issues
			ArrayList<Long> subList = new ArrayList<Long>();
			// =========== 將所有的 list 資料切割出每個 Project 所包含的 issue 個數 ==========
			for (int j = 0; j < (mStoryCount * mSprintCount); j++) {
				subList.add(mStories.get(
						i * (mStoryCount * mSprintCount) + j)
						.getIssueID());
			}

			// 將 sublist 依據 sprint 個數以及每個 sprint 想要加入的 story 個數建立關聯
			for (int k = 0; k < mSprintCount; k++) {
				ArrayList<Long> subList_each = new ArrayList<Long>();
				for (int l = 0; l < mStoryCount; l++) {
					subList_each.add(subList.get((k * mStoryCount) + l));
				}
				addStoryToSprint(project, subList_each, Integer.toString(k + 1));
				mlog.info("專案 " + project.getName() + ", 第 " + (k + 1)
						+ " 個 sprint 加入 " + mStoryCount + " 個 stories 成功");
			}
		}
	}

	// create new story list
	private void CreateStories(int EstValue, String type) throws Exception {
		int TotalStory = mStoryCount * mSprintCount;
		CreateProductBacklog createStory = new CreateProductBacklog(TotalStory,
				EstValue, mCP, type);
		createStory.exe();
		mStories.addAll(createStory.getIssueList());
	}

	private void addStoryToSprint(IProject p, ArrayList<Long> list,
			String sprintID) {
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(p,
				mConfig.getUserSession());

		for (long issueId : list) {
			IIssue issue = productBacklogMapper.getIssue(issueId);
			String oldSprintID = issue.getSprintID();
			if (sprintID != null && !sprintID.equals("")
					&& Integer.parseInt(sprintID) >= 0) {

				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);

				Date current = new Date();
				String dateTime = DateUtil.format(current,
						DateUtil._16DIGIT_DATE_TIME_2);
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, dateTime);

				// iteration node
				Element iteration = new Element(ScrumEnum.SPRINT_ID);
				iteration.setText(sprintID);
				history.addContent(iteration);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
				productBacklogMapper.updateIssueValue(issue, false);
				if (sprintID != "0") {
					productBacklogMapper.addHistory(issue.getIssueID(),
							issue.getIssueType(), HistoryObject.TYPE_APPEND,
							oldSprintID, sprintID);
				}
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
				productBacklogMapper.updateStoryRelation(issueId,
						issue.getReleaseID(), sprintID, null, null, current);
			}
		}
	}
}
