package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.UnplannedItemMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class CreateUnplannedItem {
	private Configuration mConfig = new Configuration();
	private int mItemCount = 1;
	private CreateProject mCP = null;
	private CreateSprint mCS = null;
	private ArrayList<Long> mUnplannedItemsId = new ArrayList<Long>();
	private ArrayList<IIssue> mUnplannedItems = new ArrayList<IIssue>();
	private String TEST_NAME = "TEST_UNPLANNED_";
	private String TEST_EST = "2";
	private String TEST_HANDLER = "";
	private String TEST_PARTNER = "";
	private String TEST_NOTE = "TEST_UNPLANNED_NOTES_";

	public CreateUnplannedItem(int count, CreateProject CP, CreateSprint CS) {
		mItemCount = count;
		mCP = CP;
		mCS = CS;
	}

	public int getCount() {
		return mItemCount;
	}

	public ArrayList<Long> getIdList() {
		return mUnplannedItemsId;
	}

	public ArrayList<IIssue> getIssueList() {
		return mUnplannedItems;
	}

	public void exe() {
		IUserSession userSession = mConfig.getUserSession();
		int projectCount = mCP.getProjectList().size();
		int sprintCount = mCS.getSprintCount();

		// get parameter info
		String specificTime = DateUtil.getNow();
		Date date = DateUtil.dayFillter(specificTime, DateUtil._16DIGIT_DATE_TIME);
		String issueType = ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE;
		String name = "";
		long unplannedId = 0;

		for (int i = 0; i < projectCount; i++) {
			IProject project = mCP.getProjectList().get(i);			// get Project
			UnplannedItemMapper um = new UnplannedItemMapper(project, userSession);

			for (int j = 0; j < sprintCount; j++) {
				long sprintId = mCS.getSprintsId().get(j);

				for (int k = 0; k < mItemCount; k++) {
					// name = p1s1_TEST_NAME_1 -> project N, sprint N, NAME, index N
					name = "p" + String.valueOf(i + 1) + "s" + String.valueOf(j + 1) + "_" + TEST_NAME + String.valueOf(k + 1);
					unplannedId = um.add(name, TEST_EST, TEST_HANDLER, TEST_PARTNER, TEST_NOTE + String.valueOf(k + 1), date, issueType, sprintId);
					mUnplannedItemsId.add(unplannedId);
					mUnplannedItems.add(um.getById(unplannedId));
				}
				System.out.println("   專案:" + project.getName() + " 的sprintID:" + sprintId + " 創建" + mItemCount + "個unplanned item(s) 成功.");
			}
		}
		System.out.println("Create " + String.valueOf(projectCount) + " project(s) " + String.valueOf(sprintCount) + " sprint(s)" + String.valueOf(mItemCount) + " UnplannedItem Finish!");
	}

}
