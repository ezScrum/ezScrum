package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;

public class CreateUnplannedItem {
	private int mProjectCount;
	private int mSprintCount;
	private int mUnplannedCount = 1;
	private CreateProject mCP;
	private CreateSprint mCS;
	private ArrayList<Long> mUnplannedItemsId = new ArrayList<Long>();
	private ArrayList<UnplannedObject> mUnplannedItems = new ArrayList<UnplannedObject>();
	private String TEST_NAME = "TEST_UNPLANNED_";
	private String TEST_NOTE = "TEST_UNPLANNED_NOTES_";
	private int TEST_EST = 2;
	private long TEST_HANDLER = -1;
	private ArrayList<Long> TEST_PARTNER = new ArrayList<Long>();
	private long mSpecificTime = System.currentTimeMillis();

	public CreateUnplannedItem(int count, CreateProject CP, CreateSprint CS) {
		mCP = CP;
		mCS = CS;
		mUnplannedCount = count;
		mProjectCount = mCP.getAllProjects().size();
		mSprintCount = mCS.getSprintCount();
	}

	public int getCount() {
		return mUnplannedCount;
	}

	public ArrayList<Long> getUnplannedsId() {
		return mUnplannedItemsId;
	}

	public ArrayList<UnplannedObject> getUnplanneds() {
		return mUnplannedItems;
	}

	public void exe() {
		for (int i = 0; i < mProjectCount; i++) {
			ProjectObject project = mCP.getAllProjects().get(i);
			long projectId = project.getId();
			for (int sprintIndex = 0; sprintIndex < mSprintCount; sprintIndex++) {
				long sprintId = sprintIndex + 1;
				for (int unplannedIndex = 0; unplannedIndex < mUnplannedCount; unplannedIndex++) {
					int unplannedPositionInSprints = 1 + unplannedIndex +
							mUnplannedCount * sprintIndex;
					String name = TEST_NAME + unplannedPositionInSprints;
					String notes = TEST_NOTE + unplannedPositionInSprints;
					
					UnplannedObject unplanned = new UnplannedObject(sprintId, projectId);
					unplanned.setName(name).setNotes(notes).setEstimate(TEST_EST)
					.setActual(0).setHandlerId(TEST_HANDLER).setPartnersId(TEST_PARTNER)
					.setCreateTime(mSpecificTime).save();
					
					mUnplannedItems.add(unplanned);
					mUnplannedItemsId.add(unplanned.getId());
				}
				System.out.println("   專案:" + project.getName() + " 的sprintID:" + sprintId + " 創建" + mUnplannedCount + "個unplanned item(s) 成功.");
			}
		}
		System.out.println("Create " + String.valueOf(mProjectCount) + " project(s) " + String.valueOf(mSprintCount) + " sprint(s)" + String.valueOf(mUnplannedCount) + " UnplannedItem Finish!");
	}

}
