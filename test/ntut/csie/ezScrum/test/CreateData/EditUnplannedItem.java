package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
import ntut.csie.ezScrum.web.mapper.UnplannedItemMapper;

public class EditUnplannedItem {
	
	private CreateUnplannedItem mCU;
	private CreateAccount mCA;
	private UnplannedItemMapper mUnplannedItemMapper;
	private ProjectObject mProject;
	private String TEST_NOTE = "i am the update one";
	
	public EditUnplannedItem(CreateUnplannedItem CU, CreateProject CP) {
		mCU = CU;
		mProject = CP.getAllProjects().get(0);
		mUnplannedItemMapper = new UnplannedItemMapper(mProject);
	}
	
	public EditUnplannedItem(CreateUnplannedItem CU, CreateProject CP, CreateAccount CA) {
		mCU = CU;
		mCA = CA;
		mProject = CP.getAllProjects().get(0);
		mUnplannedItemMapper = new UnplannedItemMapper(mProject);
	}

	/**
	 * Edit unplanned info
	 */
	public void exe() {
		for (UnplannedObject unplanned :  mCU.getUnplanneds()) {
			UnplannedInfo unplannedInfo = new UnplannedInfo();
			unplannedInfo.id = unplanned.getId();
			unplannedInfo.sprintId = unplanned.getSprintId();
			unplannedInfo.name = unplanned.getName();
			unplannedInfo.notes = TEST_NOTE;
			unplannedInfo.status = unplanned.getStatus();
			unplannedInfo.estimate = unplanned.getEstimate();
			unplannedInfo.actual = unplanned.getActual();
			unplannedInfo.handlerId = unplanned.getHandlerId();
			unplannedInfo.partnersId = unplanned.getPartnersId();
			unplannedInfo.specificTime = System.currentTimeMillis();
			mUnplannedItemMapper.updateUnplanned(unplannedInfo.id, unplannedInfo);
		}
	}
	
	/**
	 * 將 unplanned checkout
	 */
	public void exe_CHECKOUT() {
		for (UnplannedObject unplanned :  mCU.getUnplanneds()) {
			UnplannedInfo unplannedInfo = new UnplannedInfo();
			unplannedInfo.id = unplanned.getId();
			unplannedInfo.sprintId = unplanned.getSprintId();
			unplannedInfo.name = unplanned.getName();
			unplannedInfo.notes = TEST_NOTE;
			unplannedInfo.status = UnplannedObject.STATUS_CHECK;
			unplannedInfo.estimate = unplanned.getEstimate();
			unplannedInfo.actual = unplanned.getActual();
			unplannedInfo.handlerId = mCA.getAccountList().get(0).getId();
			unplannedInfo.partnersId = unplanned.getPartnersId();
			unplannedInfo.specificTime = System.currentTimeMillis();
			mUnplannedItemMapper.updateUnplanned(unplannedInfo.id, unplannedInfo);
		}
	}
	
	/**
	 * 將 unplanned done
	 */
	public void exe_DONE() {
		for (UnplannedObject unplanned : mCU.getUnplanneds()) {
			UnplannedInfo unplannedInfo = new UnplannedInfo();
			unplannedInfo.id = unplanned.getId();
			unplannedInfo.sprintId = unplanned.getSprintId();
			unplannedInfo.name = unplanned.getName();
			unplannedInfo.notes = TEST_NOTE;
			unplannedInfo.status = UnplannedObject.STATUS_DONE;
			unplannedInfo.estimate = unplanned.getEstimate();
			unplannedInfo.actual = unplanned.getActual();
			unplannedInfo.handlerId = mCA.getAccountList().get(0).getId();
			unplannedInfo.partnersId = unplanned.getPartnersId();
			unplannedInfo.specificTime = System.currentTimeMillis();
			mUnplannedItemMapper.updateUnplanned(unplannedInfo.id, unplannedInfo);
		}
	}
}
