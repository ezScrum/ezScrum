package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.UnplannedItemMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class CreateUnplannedItem {
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private int itemCount = 1;
	private CreateProject CP = null;
	private CreateSprint CS = null;
	private ArrayList<Long> UnplannedItemIdList = new ArrayList<Long>();
	private ArrayList<IIssue> UnplannedItemList = new ArrayList<IIssue>();
	private String TEST_NAME = "TEST_UNPLANNED_";
	private String TEST_EST = "2";
	private String TEST_HANDLER = "";
	private String TEST_PARTNER = "";
	private String TEST_NOTE = "TEST_UNPLANNED_NOTES_";

	public CreateUnplannedItem(int count, CreateProject cp, CreateSprint cs) {
		this.itemCount = count;
		this.CP = cp;
		this.CS = cs;
	}

	public int getCount() {
		return this.itemCount;
	}

	public ArrayList<Long> getIdList() {
		return this.UnplannedItemIdList;
	}

	public ArrayList<IIssue> getIssueList() {
		return this.UnplannedItemList;
	}

	public void exe() {
		IUserSession userSession = this.config.getUserSession();
		int projectCount = this.CP.getProjectList().size();
		int sprintCount = this.CS.getSprintCount();

		// get parameter info
		String specificTime = DateUtil.getNow();
		Date date = DateUtil.dayFillter(specificTime, DateUtil._16DIGIT_DATE_TIME);
		String issueType = ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE;
		String name = "";
		long unplannedId = 0;

		for (int i = 0; i < projectCount; i++) {
			IProject project = this.CP.getProjectList().get(i);			// get Project
			UnplannedItemMapper um = new UnplannedItemMapper(project, userSession);

			for (int j = 0; j < sprintCount; j++) {
				String sprintID = this.CS.getSprintIDList().get(j);

				for (int k = 0; k < this.itemCount; k++) {
					// name = p1s1_TEST_NAME_1 -> project N, sprint N, NAME, index N
					name = "p" + String.valueOf(i + 1) + "s" + String.valueOf(j + 1) + "_" + this.TEST_NAME + String.valueOf(k + 1);
					unplannedId = um.add(name, TEST_EST, TEST_HANDLER, TEST_PARTNER, TEST_NOTE + String.valueOf(k + 1), date, issueType, sprintID);
					this.UnplannedItemIdList.add(unplannedId);
					this.UnplannedItemList.add(um.getById(unplannedId));
				}
				System.out.println("   專案:" + project.getName() + " 的sprintID:" + sprintID + " 創建" + this.itemCount + "個unplanned item(s) 成功.");
			}
		}
		System.out.println("Create " + String.valueOf(projectCount) + " project(s) " + String.valueOf(sprintCount) + " sprint(s)" + String.valueOf(this.itemCount) + " UnplannedItem Finish!");
	}

}
