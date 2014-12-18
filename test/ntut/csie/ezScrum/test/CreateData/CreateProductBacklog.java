package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

public class CreateProductBacklog {
	private static Log log = LogFactory.getLog(CreateProductBacklog.class);

	private int StoryCount = 1;
	private CreateProject CP = null;

	public String TEST_STORY_NAME = "TEST_STORY_";			// Story Name
	public String TEST_STORY_DESC = "TEST_DESCRIPTION_";	// Story Description
	public String TEST_STORY_VALUE = "50";					// Business Value
	public String TEST_STORY_IMP = "100";					// Story importance
	public String TEST_STORY_EST = "2";					// Story estimation
	private int Min_EST = 2;
	private int Max_IMP = 100;
	public String TEST_STORY_HOW_TO_DEMO = "TEST_STORY_DEMO_";	// How to demo
	public String TEST_STORY_NOTES = "TEST_STORY_NOTE_";	// Story notes

	private ArrayList<IIssue> IssueList = new ArrayList<IIssue>();
	private ArrayList<Long> IssueIDList = new ArrayList<Long>();
	private ProductBacklogMapper productBacklogMapper = null;

	public static final String TYPE_ESTIMATION = "EST";
	public static final String TYPE_IMPORTANCE = "IMP";

	// ========================== 為了可以設定 story 而新增下列屬性 ===========================
	private int AssignValue = 1;
	private boolean AutoSetStory = true;
	private String Type;
	private Configuration configuration = new Configuration();

	public CreateProductBacklog() {}

	public CreateProductBacklog(int storycount, CreateProject cp) {
		this.StoryCount = storycount;
		this.CP = cp;
		this.AutoSetStory = true;
	}

	public CreateProductBacklog(int storycount, int Est, CreateProject cp, String type) {
		this.StoryCount = storycount;
		this.CP = cp;
		this.AssignValue = Est;
		this.AutoSetStory = false;
		this.Type = type;
	}

	public void exe() {
		IUserSession userSession = configuration.getUserSession();
		if (this.AutoSetStory) {

			for (int i = 0; i < this.CP.getProjectList().size(); i++) {
				IProject project = this.CP.getProjectList().get(i);		// TEST_PROJECT_X
				this.productBacklogMapper = new ProductBacklogMapper(project, userSession);

				this.TEST_STORY_IMP = Integer.toString(this.Max_IMP);
				this.TEST_STORY_EST = Integer.toString(this.Min_EST);

				for (int j = 0; j < this.StoryCount; j++) {
					StoryInformation storyInformation = new StoryInformation
					        (this.TEST_STORY_NAME + Integer.toString(j + 1),
					                this.TEST_STORY_IMP,
					                this.TEST_STORY_EST,
					                this.TEST_STORY_VALUE,
					                this.TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
					                this.TEST_STORY_NOTES + Integer.toString(j + 1),
					                "", "", "", "");
					IIssue story = this.productBacklogMapper.addStory(storyInformation);

					this.editStory(story.getIssueID(),
					        this.TEST_STORY_NAME + Integer.toString(j + 1),
					        this.TEST_STORY_VALUE,
					        this.TEST_STORY_IMP,
					        this.TEST_STORY_EST,
					        this.TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
					        this.TEST_STORY_NOTES + Integer.toString(j + 1),
					        false);
					// 重新拿出更新過的story
					story = this.productBacklogMapper.getIssue(story.getIssueID());
					this.IssueList.add(story);
					this.IssueIDList.add(story.getIssueID());
					resetEST(j + 1);
					resetIMP(j + 1);
				}
				this.log.info("Project " + this.CP.getProjectList().get(i).getName() + " create " + this.StoryCount + " Stories success.");
			}
		} else {
			if (this.Type.equals(TYPE_ESTIMATION)) {
				for (int i = 0; i < this.CP.getProjectList().size(); i++) {
					IProject project = this.CP.getProjectList().get(i);		// TEST_PROJECT_X
					this.productBacklogMapper = new ProductBacklogMapper(project, userSession);
					this.TEST_STORY_IMP = Integer.toString(this.Max_IMP);
					this.TEST_STORY_EST = Integer.toString(this.Min_EST);

					for (int j = 0; j < this.StoryCount; j++) {
						StoryInformation storyInformation = new StoryInformation
						        (this.TEST_STORY_NAME + Integer.toString(j + 1),
						                this.TEST_STORY_IMP,
						                Integer.toString(this.AssignValue),
						                this.TEST_STORY_VALUE,
						                this.TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						                this.TEST_STORY_NOTES + Integer.toString(j + 1),
						                "", "", "", "");
						IIssue story = this.productBacklogMapper.addStory(storyInformation);
						editStory(story.getIssueID(),
						        this.TEST_STORY_NAME + Integer.toString(j + 1),
						        this.TEST_STORY_VALUE,
						        this.TEST_STORY_IMP,
						        Integer.toString(this.AssignValue),
						        this.TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						        this.TEST_STORY_NOTES + Integer.toString(j + 1),
						        false);
						// 重新拿出更新過的story
						story = this.productBacklogMapper.getIssue(story.getIssueID());
						this.IssueList.add(story);
						this.IssueIDList.add(story.getIssueID());
						resetIMP(j + 1);
					}
				}
			} else if (this.Type.equals(TYPE_IMPORTANCE)) {
				for (int i = 0; i < this.CP.getProjectList().size(); i++) {
					String projectName = this.CP.PJ_NAME + Integer.toString((i + 1));	// TEST_PROJECT_X
					this.TEST_STORY_IMP = Integer.toString(this.Max_IMP);
					this.TEST_STORY_EST = Integer.toString(this.Min_EST);

					for (int j = 0; j < this.StoryCount; j++) {
						StoryInformation storyInformation = new StoryInformation
						        (this.TEST_STORY_NAME + Integer.toString(j + 1),
						                this.TEST_STORY_IMP,
						                this.TEST_STORY_EST,
						                this.TEST_STORY_VALUE,
						                this.TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						                this.TEST_STORY_NOTES + Integer.toString(j + 1),
						                this.TEST_STORY_DESC + Integer.toString(j + 1),
						                "", "", "");
						IIssue story = this.productBacklogMapper.addStory(storyInformation);

						this.editStory(story.getIssueID(),
						        this.TEST_STORY_NAME + Integer.toString(j + 1),
						        this.TEST_STORY_VALUE,
						        this.TEST_STORY_IMP,
						        this.TEST_STORY_EST,
						        this.TEST_STORY_HOW_TO_DEMO + Integer.toString(j + 1),
						        this.TEST_STORY_NOTES + Integer.toString(j + 1),
						        false);
						// 重新拿出更新過的story
						story = this.productBacklogMapper.getIssue(story.getIssueID());
						this.IssueList.add(story);	// add to list
						this.IssueIDList.add(story.getIssueID());
						resetEST(j + 1);
					}
					this.log.info("Assign Importance Value");
					this.log.info("Project " + projectName + " create " + this.StoryCount + " Stories success");
				}
			} else {
				this.log.info("Create Product Backlog Type error.");
			}
		}
	}

	public void createBacklogStory(IProject project, String value, String importance, String estimation) {
		IUserSession userSession = configuration.getUserSession();
		this.productBacklogMapper = new ProductBacklogMapper(project, userSession);
		int index = IssueIDList.size();
		StoryInformation storyInformation = new StoryInformation
		        (this.TEST_STORY_NAME + Integer.toString(index + 1),
		                importance,
		                estimation,
		                value,
		                this.TEST_STORY_HOW_TO_DEMO + Integer.toString(index + 1),
		                this.TEST_STORY_NOTES + Integer.toString(index + 1),
		                "",
		                "", "", "");
		IIssue story = this.productBacklogMapper.addStory(storyInformation);
		editStory(story.getIssueID(),
		        this.TEST_STORY_NAME + Integer.toString(index + 1),
		        value,
		        importance,
		        estimation,
		        this.TEST_STORY_HOW_TO_DEMO + Integer.toString(index + 1),
		        this.TEST_STORY_NOTES + Integer.toString(index + 1),
		        false);
		story = productBacklogMapper.getIssue(story.getIssueID());
		this.IssueList.add(story);	// add to list
		this.IssueIDList.add(story.getIssueID());
	}

	public ArrayList<IIssue> getIssueList() {
		return this.IssueList;
	}

	public ArrayList<Long> getIssueIDList() {
		return this.IssueIDList;
	}

	// reset estimation value
	private void resetEST(int j) {
		int div = this.StoryCount / 5;
		if (div > 0) {
			int c = j / div;
			this.TEST_STORY_EST = Integer.toString(Min_EST + c);
		} else {
			this.TEST_STORY_EST = Integer.toString(Min_EST);
		}
	}

	// reset importance value
	private void resetIMP(int j) {
		int div = this.StoryCount / 5;
		if (div > 0) {
			int c = j / div;
			this.TEST_STORY_IMP = Integer.toString(Max_IMP - (c * 5));
		} else {
			this.TEST_STORY_IMP = Integer.toString(Max_IMP);
		}
	}

	// set story column value of IMP, EST, HowToDemo, Note
	public void editStory(long issueID, String name, String value, String importance,
	        String estimation, String howToDemo, String note, boolean addHistory) {
		this.productBacklogMapper.modifyName(issueID, name, null);
		Element history = this.translateIssueToXML(value, importance, estimation, howToDemo, note);
		if (history.getChildren().size() > 0) {
			IIssue issue = this.productBacklogMapper.getIssue(issueID);
			issue.addTagValue(history);
			issue.setSummary(name);
			this.productBacklogMapper.updateIssueValue(issue, addHistory);
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
