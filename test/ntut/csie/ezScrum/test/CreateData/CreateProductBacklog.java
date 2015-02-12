package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

public class CreateProductBacklog {
	private static Log mlog = LogFactory.getLog(CreateProductBacklog.class);
	private int mStoryCount = 1;
	private CreateProject mCP = null;

	public String TEST_STORY_NAME = "TEST_STORY_";			// Story Name
	public String TEST_STORY_DESC = "TEST_DESCRIPTION_";	// Story Description
	public String TEST_STORY_VALUE = "50";					// Business Value
	public String TEST_STORY_IMP = "100";					// Story importance
	public String TEST_STORY_EST = "2";					// Story estimation
	private int Min_EST = 2;
	private int Max_IMP = 100;
	public String TEST_STORY_HOW_TO_DEMO = "TEST_STORY_DEMO_";	// How to demo
	public String TEST_STORY_NOTES = "TEST_STORY_NOTE_";	// Story notes

	private ArrayList<IIssue> Issues = new ArrayList<IIssue>();
	private ArrayList<Long> IssuesId = new ArrayList<Long>();
	private ProductBacklogMapper mProductBacklogMapper = null;

	public static final String TYPE_ESTIMATION = "EST";
	public static final String TYPE_IMPORTANCE = "IMP";

	// ========================== 為了可以設定 story 而新增下列屬性 ===========================
	private int mAssignValue = 1;
	private boolean mAutoSetStory = true;
	private String mType;
	private Configuration mConfig = new Configuration();

	public CreateProductBacklog() {
	}

	public CreateProductBacklog(int storycount, CreateProject cp) {
		mStoryCount = storycount;
		mCP = cp;
		mAutoSetStory = true;
	}

	public CreateProductBacklog(int storycount, int Est, CreateProject cp, String type) {
		mStoryCount = storycount;
		mCP = cp;
		mAssignValue = Est;
		mAutoSetStory = false;
		mType = type;
	}

	public void exe() {
		IUserSession userSession = mConfig.getUserSession();
		if (mAutoSetStory) {

			for (int i = 0; i < mCP.getProjectList().size(); i++) {
				IProject project = mCP.getProjectList().get(i);		// TEST_PROJECT_X
				mProductBacklogMapper = new ProductBacklogMapper(project, userSession);

				TEST_STORY_IMP = Integer.toString(Max_IMP);
				TEST_STORY_EST = Integer.toString(Min_EST);

				for (int j = 0; j < mStoryCount; j++) {
					StoryInfo storyInformation = new StoryInfo
					        (TEST_STORY_NAME + Integer.toString(j + 1),
					                TEST_STORY_IMP,
					                TEST_STORY_EST,
					                TEST_STORY_VALUE,
					                TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
					                TEST_STORY_NOTES + Integer.toString(j + 1),
					                "", "", "", "");
					IIssue story = mProductBacklogMapper.addStory(storyInformation);

					editStory(story.getIssueID(),
					        TEST_STORY_NAME + Integer.toString(j + 1),
					        TEST_STORY_VALUE,
					        TEST_STORY_IMP,
					        TEST_STORY_EST,
					        TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
					        TEST_STORY_NOTES + Integer.toString(j + 1),
					        false);
					// 重新拿出更新過的story
					story = mProductBacklogMapper.getIssue(story.getIssueID());
					Issues.add(story);
					IssuesId.add(story.getIssueID());
					resetEST(j + 1);
					resetIMP(j + 1);
				}
				mlog.info("Project " + mCP.getProjectList().get(i).getName() + " create " + mStoryCount + " Stories success.");
			}
		} else {
			if (mType.equals(TYPE_ESTIMATION)) {
				for (int i = 0; i < mCP.getProjectList().size(); i++) {
					IProject project = mCP.getProjectList().get(i);		// TEST_PROJECT_X
					mProductBacklogMapper = new ProductBacklogMapper(project, userSession);
					TEST_STORY_IMP = Integer.toString(Max_IMP);
					TEST_STORY_EST = Integer.toString(Min_EST);

					for (int j = 0; j < mStoryCount; j++) {
						StoryInfo storyInformation = new StoryInfo
						        (TEST_STORY_NAME + Integer.toString(j + 1),
						                TEST_STORY_IMP,
						                Integer.toString(mAssignValue),
						                TEST_STORY_VALUE,
						                TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						                TEST_STORY_NOTES + Integer.toString(j + 1),
						                "", "", "", "");
						IIssue story = mProductBacklogMapper.addStory(storyInformation);
						editStory(story.getIssueID(),
						        TEST_STORY_NAME + Integer.toString(j + 1),
						        TEST_STORY_VALUE,
						        TEST_STORY_IMP,
						        Integer.toString(mAssignValue),
						        TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						        TEST_STORY_NOTES + Integer.toString(j + 1),
						        false);
						// 重新拿出更新過的story
						story = mProductBacklogMapper.getIssue(story.getIssueID());
						Issues.add(story);
						IssuesId.add(story.getIssueID());
						resetIMP(j + 1);
					}
				}
			} else if (mType.equals(TYPE_IMPORTANCE)) {
				for (int i = 0; i < mCP.getProjectList().size(); i++) {
					String projectName = mCP.mProjectName + Integer.toString((i + 1));	// TEST_PROJECT_X
					TEST_STORY_IMP = Integer.toString(Max_IMP);
					TEST_STORY_EST = Integer.toString(Min_EST);

					for (int j = 0; j < mStoryCount; j++) {
						StoryInfo storyInformation = new StoryInfo
						        (TEST_STORY_NAME + Integer.toString(j + 1),
						                TEST_STORY_IMP,
						                TEST_STORY_EST,
						                TEST_STORY_VALUE,
						                TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						                TEST_STORY_NOTES + Integer.toString(j + 1),
						                TEST_STORY_DESC + Integer.toString(j + 1),
						                "", "", "");
						IIssue story = mProductBacklogMapper.addStory(storyInformation);

						editStory(story.getIssueID(),
						        TEST_STORY_NAME + Integer.toString(j + 1),
						        TEST_STORY_VALUE,
						        TEST_STORY_IMP,
						        TEST_STORY_EST,
						        TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						        TEST_STORY_NOTES + Integer.toString(j + 1),
						        false);
						// 重新拿出更新過的story
						story = mProductBacklogMapper.getIssue(story.getIssueID());
						Issues.add(story);	// add to list
						IssuesId.add(story.getIssueID());
						resetEST(j + 1);
					}
					mlog.info("Assign Importance Value");
					mlog.info("Project " + projectName + " create " + mStoryCount + " Stories success");
				}
			} else {
				mlog.info("Create Product Backlog Type error.");
			}
		}
	}

	public void createBacklogStory(IProject project, String value, String importance, String estimation) {
		IUserSession userSession = mConfig.getUserSession();
		mProductBacklogMapper = new ProductBacklogMapper(project, userSession);
		int index = IssuesId.size();
		StoryInfo storyInformation = new StoryInfo
		        (TEST_STORY_NAME + Integer.toString(index + 1),
		                importance,
		                estimation,
		                value,
		                TEST_STORY_HOW_TO_DEMO + Integer.toString(index + 1),
		                TEST_STORY_NOTES + Integer.toString(index + 1),
		                "",
		                "", "", "");
		IIssue story = mProductBacklogMapper.addStory(storyInformation);
		editStory(story.getIssueID(),
		        TEST_STORY_NAME + Integer.toString(index + 1),
		        value,
		        importance,
		        estimation,
		        TEST_STORY_HOW_TO_DEMO + Integer.toString(index + 1),
		        TEST_STORY_NOTES + Integer.toString(index + 1),
		        false);
		story = mProductBacklogMapper.getIssue(story.getIssueID());
		Issues.add(story);	// add to list
		IssuesId.add(story.getIssueID());
	}

	public ArrayList<IIssue> getIssueList() {
		return Issues;
	}

	public ArrayList<Long> getIssueIDList() {
		return IssuesId;
	}

	// reset estimation value
	private void resetEST(int j) {
		int div = mStoryCount / 5;
		if (div > 0) {
			int c = j / div;
			TEST_STORY_EST = Integer.toString(Min_EST + c);
		} else {
			TEST_STORY_EST = Integer.toString(Min_EST);
		}
	}

	// reset importance value
	private void resetIMP(int j) {
		int div = mStoryCount / 5;
		if (div > 0) {
			int c = j / div;
			TEST_STORY_IMP = Integer.toString(Max_IMP - (c * 5));
		} else {
			TEST_STORY_IMP = Integer.toString(Max_IMP);
		}
	}

	// set story column value of IMP, EST, HowToDemo, Note
	public void editStory(long issueID, String name, String value, String importance,
	        String estimation, String howToDemo, String note, boolean addHistory) {
		mProductBacklogMapper.modifyName(issueID, name, new Date());
		Element history = translateIssueToXML(value, importance, estimation, howToDemo, note);
		if (history.getChildren().size() > 0) {
			IIssue issue = mProductBacklogMapper.getIssue(issueID);
			issue.addTagValue(history);
			issue.setSummary(name);
			mProductBacklogMapper.updateIssueValue(issue, addHistory);
		}
	}

	private Element translateIssueToXML(String value, String importance, String estimation, String howToDemo, String note) {
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		if (importance != null && !importance.equals("")) {
			Element importanceElem = new Element(ScrumEnum.IMPORTANCE);
			int temp = (int) Float.parseFloat(importance);
			importanceElem.setText(temp + "");
			history.addContent(importanceElem);
		}

		if (estimation != null && !estimation.equals("")) {
			Element storyPoint = new Element(ScrumEnum.ESTIMATION);
			storyPoint.setText(estimation);
			history.addContent(storyPoint);
		}

		if (value != null && !value.equals("")) {
			Element customValue = new Element(ScrumEnum.VALUE);
			customValue.setText(value);
			history.addContent(customValue);
		}
		Element howToDemoElem = new Element(ScrumEnum.HOWTODEMO);
		howToDemoElem.setText(howToDemo);
		history.addContent(howToDemoElem);

		Element notesElem = new Element(ScrumEnum.NOTES);
		notesElem.setText(note);
		history.addContent(notesElem);
		return history;
	}
}
