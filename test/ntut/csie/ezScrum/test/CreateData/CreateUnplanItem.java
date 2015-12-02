package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;

public class CreateUnplanItem {
	private int mProjectCount;
	private int mSprintCount;
	private int mUnplanCount = 1;
	private CreateProject mCP;
	private CreateSprint mCS;
	private ArrayList<Long> mUnplanItemsId = new ArrayList<Long>();
	private ArrayList<UnplanObject> mUnplanItems = new ArrayList<UnplanObject>();
	private String TEST_NAME = "TEST_UNPLAN_";
	private String TEST_NOTE = "TEST_UNPLAN_NOTES_";
	private int TEST_EST = 2;
	private long TEST_HANDLER = -1;
	private ArrayList<Long> TEST_PARTNER = new ArrayList<Long>();
	private long mSpecificTime = System.currentTimeMillis();

	public CreateUnplanItem(int count, CreateProject CP, CreateSprint CS) {
		mCP = CP;
		mCS = CS;
		mUnplanCount = count;
		mProjectCount = mCP.getAllProjects().size();
		mSprintCount = mCS.getSprintCount();
	}

	public int getCount() {
		return mUnplanCount;
	}

	public ArrayList<Long> getUnplansId() {
		return mUnplanItemsId;
	}

	public ArrayList<UnplanObject> getUnplans() {
		return mUnplanItems;
	}

	public void exe() {
		for (int i = 0; i < mProjectCount; i++) {
			ProjectObject project = mCP.getAllProjects().get(i);
			long projectId = project.getId();
			for (int sprintIndex = 0; sprintIndex < mSprintCount; sprintIndex++) {
				long sprintId = sprintIndex + 1;
				for (int unplanIndex = 0; unplanIndex < mUnplanCount; unplanIndex++) {
					int unplanPositionInSprints = 1 + unplanIndex +
							mUnplanCount * sprintIndex;
					String name = TEST_NAME + unplanPositionInSprints;
					String notes = TEST_NOTE + unplanPositionInSprints;
					
					UnplanObject unplan = new UnplanObject(sprintId, projectId);
					unplan.setName(name).setNotes(notes).setEstimate(TEST_EST)
					.setActual(0).setHandlerId(TEST_HANDLER).setPartnersId(TEST_PARTNER)
					.setCreateTime(mSpecificTime).save();
					
					mUnplanItems.add(unplan);
					mUnplanItemsId.add(unplan.getId());
				}
				System.out.println("   專案:" + project.getName() + " 的sprintID:" + sprintId + " 創建" + mUnplanCount + "個unplan item(s) 成功.");
			}
		}
		System.out.println("Create " + String.valueOf(mProjectCount) + " project(s) " + String.valueOf(mSprintCount) + " sprint(s)" + String.valueOf(mUnplanCount) + " UnplanItem Finish!");
	}

}
