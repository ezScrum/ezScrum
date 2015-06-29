package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateRelease {
	private static Log mlog = LogFactory.getLog(CreateRelease.class);
	
	private int mReleaseCount = 1;
	private CreateProject mCP = null;
	
	public String TEST_RELEASE_NAME = "TEST_RELEASE_";						// Release Name
	public int RELEASE_DURATION = 180;										// Release duration days
	public String TEST_RELEASE_DESC = "This is Release Description - ";	// This is Release Description - X
	private String StartDate = "";
	private String EndDate = "";
	
	private Date mToday = null;
	private List<IReleasePlanDesc> mIReleasePlanDescs = null;
	
	public CreateRelease(int Count, CreateProject cp) {
		mReleaseCount = Count;
		mCP = cp;
		mIReleasePlanDescs = new LinkedList<IReleasePlanDesc>();
	}
	
	/**
	 * return release count
	 */
	public int getReleaseCount() {
		return mReleaseCount;
	}
	
	/**
	 * append release default name to string
	 */
	public String getDefault_RELEASE_NAME(int i) {
		return (TEST_RELEASE_NAME + Integer.toString(i));
	}
	
	/**
	 * append release default description to string
	 */
	public String getDefault_RELEASE_DESC(int i) {
		return (TEST_RELEASE_DESC + Integer.toString(i));
	}
	
	public void exe() {
		Calendar cal = Calendar.getInstance();
		
		for (int i=0 ; i<mCP.getProjectList().size() ; i++) {
			mToday = cal.getTime();									// get Today
			ProjectObject project = mCP.getAllProjects().get(i);	// get Project
			
			setInitialDate();										// initial Data

			ReleasePlanMapper saver = new ReleasePlanMapper(project);
			for (int j=0 ; j<mReleaseCount ; j++) {
				String ID = Integer.toString(j+1);
				String releaseName = TEST_RELEASE_NAME + ID;		// TEST_RELEASE_Y
				String releseDesc = TEST_RELEASE_DESC + ID;		// This is Release Description - Y
				
				setDate();
				ReleasePlanDesc desc = new ReleasePlanDesc();
				desc.setID(ID);						// set ID
				desc.setName(releaseName);			// set Name
				desc.setStartDate(StartDate);	// set Start Date
				desc.setEndDate(EndDate);		// set End Date
				desc.setDescription(releseDesc);	// set Description
				
				// save to file
				saver.addReleasePlan(desc);
				mIReleasePlanDescs.add(desc);
			}
			mlog.info(project.getName() + " create " + mReleaseCount + " release plan success.");
		}
		mlog.info("Create Release Finish");
	}
	
	public List<IReleasePlanDesc> getReleaseList() {
		return mIReleasePlanDescs;
	}
	
	// 設定初始時間
	private void setInitialDate() {
		Calendar cal_today = Calendar.getInstance();
		cal_today.setTime(mToday);
		
		if (mReleaseCount > 2) {
			int release_count = mReleaseCount/2;
			cal_today.add(Calendar.DAY_OF_YEAR, (-1) * release_count * (RELEASE_DURATION+1));
			
//			while (DateUtil.isHoliday(cal_today.getTime())) {		// set Start Date
//			cal_today.add(Calendar.DAY_OF_YEAR, 1);				// 加上假日
//			}
	
			mToday = cal_today.getTime();
		}
	}
	
	// 每次呼叫 setDate 就會接續計算下一個 RELEASE_DURATION 過後的日期
	private void setDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		cal_start.setTime(mToday);		// 得到今天的日期
		cal_end.setTime(mToday);		// 得到今天的日期
		cal_end.add(Calendar.DAY_OF_YEAR, RELEASE_DURATION);
		
		StartDate = format.format(cal_start.getTime());		// get start date
		EndDate = format.format(cal_end.getTime());			// get end date
		
		// set next Today
		cal_end.add(Calendar.DAY_OF_YEAR, 1);
		mToday = cal_end.getTime();
	}
}
