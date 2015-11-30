package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.web.dataInfo.UnplanInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.mapper.UnplanItemMapper;

public class EditUnplanItem {
	
	private CreateUnplanItem mCU;
	private CreateAccount mCA;
	private UnplanItemMapper mUnplanItemMapper;
	private ProjectObject mProject;
	private String TEST_NOTE = "i am the update one";
	
	public EditUnplanItem(CreateUnplanItem CU, CreateProject CP) {
		mCU = CU;
		mProject = CP.getAllProjects().get(0);
		mUnplanItemMapper = new UnplanItemMapper(mProject);
	}
	
	public EditUnplanItem(CreateUnplanItem CU, CreateProject CP, CreateAccount CA) {
		mCU = CU;
		mCA = CA;
		mProject = CP.getAllProjects().get(0);
		mUnplanItemMapper = new UnplanItemMapper(mProject);
	}

	/**
	 * Edit unplan info
	 */
	public void exe() {
		for (UnplanObject unplan :  mCU.getUnplans()) {
			UnplanInfo unplanInfo = new UnplanInfo();
			unplanInfo.id = unplan.getId();
			unplanInfo.sprintId = unplan.getSprintId();
			unplanInfo.name = unplan.getName();
			unplanInfo.notes = TEST_NOTE;
			unplanInfo.status = unplan.getStatus();
			unplanInfo.estimate = unplan.getEstimate();
			unplanInfo.actual = unplan.getActual();
			unplanInfo.handlerId = unplan.getHandlerId();
			unplanInfo.partnersId = unplan.getPartnersId();
			unplanInfo.specificTime = System.currentTimeMillis();
			mUnplanItemMapper.updateUnplan(unplanInfo.id, unplanInfo);
		}
	}
	
	/**
	 * 將 unplan checkout
	 */
	public void exe_CHECKOUT() {
		for (UnplanObject unplan :  mCU.getUnplans()) {
			UnplanInfo unplanInfo = new UnplanInfo();
			unplanInfo.id = unplan.getId();
			unplanInfo.sprintId = unplan.getSprintId();
			unplanInfo.name = unplan.getName();
			unplanInfo.notes = TEST_NOTE;
			unplanInfo.status = UnplanObject.STATUS_CHECK;
			unplanInfo.estimate = unplan.getEstimate();
			unplanInfo.actual = unplan.getActual();
			unplanInfo.handlerId = mCA.getAccountList().get(0).getId();
			unplanInfo.partnersId = unplan.getPartnersId();
			unplanInfo.specificTime = System.currentTimeMillis();
			mUnplanItemMapper.updateUnplan(unplanInfo.id, unplanInfo);
		}
	}
	
	/**
	 * 將 unplan done
	 */
	public void exe_DONE() {
		for (UnplanObject unplan : mCU.getUnplans()) {
			UnplanInfo unplanInfo = new UnplanInfo();
			unplanInfo.id = unplan.getId();
			unplanInfo.sprintId = unplan.getSprintId();
			unplanInfo.name = unplan.getName();
			unplanInfo.notes = TEST_NOTE;
			unplanInfo.status = UnplanObject.STATUS_DONE;
			unplanInfo.estimate = unplan.getEstimate();
			unplanInfo.actual = unplan.getActual();
			unplanInfo.handlerId = mCA.getAccountList().get(0).getId();
			unplanInfo.partnersId = unplan.getPartnersId();
			unplanInfo.specificTime = System.currentTimeMillis();
			mUnplanItemMapper.updateUnplan(unplanInfo.id, unplanInfo);
		}
	}
}
