package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;

public class CreateUnplannedItem {
	private int mItemCount = 1;
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
		mItemCount = count;
		mCP = CP;
		mCS = CS;
	}

	public int getCount() {
		return mItemCount;
	}

	public ArrayList<Long> getUnplannedsId() {
		return mUnplannedItemsId;
	}

	public ArrayList<UnplannedObject> getUnplanneds() {
		return mUnplannedItems;
	}

	public void exe() {
		int projectCount = mCP.getAllProjects().size();
		int sprintCount = mCS.getSprintCount();

		// get parameter info
		
		for (int i = 0; i < projectCount; i++) {
			ProjectObject project = mCP.getAllProjects().get(i);
			long projectId = project.getId();

			for (int j = 0; j < sprintCount; j++) {
				long sprintId = mCS.getSprintsId().get(j);

				for (int k = 0; k < mItemCount; k++) {
					String name = TEST_NAME + String.valueOf(k + 1);
					String notes = TEST_NOTE + String.valueOf(k + 1);
					
					UnplannedObject unplanned = new UnplannedObject(sprintId, projectId);
					unplanned.setName(name).setNotes(notes).setEstimate(TEST_EST)
					.setActual(0).setHandlerId(TEST_HANDLER).setPartnersId(TEST_PARTNER)
					.setCreateTime(mSpecificTime).save();
					
					mUnplannedItems.add(unplanned);
					mUnplannedItemsId.add(unplanned.getId());
				}
				System.out.println("   專案:" + project.getName() + " 的sprintID:" + sprintId + " 創建" + mItemCount + "個unplanned item(s) 成功.");
			}
		}
		System.out.println("Create " + String.valueOf(projectCount) + " project(s) " + String.valueOf(sprintCount) + " sprint(s)" + String.valueOf(mItemCount) + " UnplannedItem Finish!");
	}

}
