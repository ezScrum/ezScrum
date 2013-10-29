package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateRelease {
	private static Log log = LogFactory.getLog(CreateRelease.class);
	
	private int ReleaseCount = 1;
	private CreateProject CP = null;
	
	public String TEST_RELEASE_NAME = "TEST_RELEASE_";						// Release Name
	public int RELEASE_DURATION = 180;										// Release duration days
	public String TEST_RELEASE_DESC = "This is Release Description - ";	// This is Release Description - X
	private String StartDate = "";
	private String EndDate = "";
	
	private Date Today = null;
	private List<IReleasePlanDesc> ReleaseList = null;
	
	public CreateRelease(int Count, CreateProject cp) {
		this.ReleaseCount = Count;
		this.CP = cp;
		this.ReleaseList = new LinkedList<IReleasePlanDesc>();
	}
	
	/**
	 * return release count
	 */
	public int getReleaseCount() {
		return this.ReleaseCount;
	}
	
	/**
	 * append release default name to string
	 */
	public String getDefault_RELEASE_NAME(int i) {
		return (this.TEST_RELEASE_NAME + Integer.toString(i));
	}
	
	/**
	 * append release default description to string
	 */
	public String getDefault_RELEASE_DESC(int i) {
		return (this.TEST_RELEASE_DESC + Integer.toString(i));
	}
	
	public void exe() {
		Calendar cal = Calendar.getInstance();
		
		for (int i=0 ; i<this.CP.getProjectList().size() ; i++) {
			this.Today = cal.getTime();									// get Today
			IProject project = this.CP.getProjectList().get(i);			// get Project
			
			setInitialDate();											// initial Data

			ReleasePlanMapper saver = new ReleasePlanMapper(project);
			for (int j=0 ; j<this.ReleaseCount ; j++) {
				String ID = Integer.toString(j+1);
				String releaseName = this.TEST_RELEASE_NAME + ID;		// TEST_RELEASE_Y
				String releseDesc = this.TEST_RELEASE_DESC + ID;		// This is Release Description - Y
				
				setDate();
				ReleasePlanDesc desc = new ReleasePlanDesc();
				desc.setID(ID);						// set ID
				desc.setName(releaseName);			// set Name
				desc.setStartDate(this.StartDate);	// set Start Date
				desc.setEndDate(this.EndDate);		// set End Date
				desc.setDescription(releseDesc);	// set Description
				
				// save to file
				saver.addReleasePlan(desc);
				this.ReleaseList.add(desc);
			}
			log.info(project.getName() + " create " + this.ReleaseCount + " release plan success.");
		}
		log.info("Create Release Finish");
	}
	
	public List<IReleasePlanDesc> getReleaseList() {
		return this.ReleaseList;
	}
	
	// 設定初始時間
	private void setInitialDate() {
		Calendar cal_today = Calendar.getInstance();
		cal_today.setTime(this.Today);
		
		if (this.ReleaseCount > 2) {
			int release_count = this.ReleaseCount/2;
			cal_today.add(Calendar.DAY_OF_YEAR, (-1) * release_count * (RELEASE_DURATION+1));
			
//			while (DateUtil.isHoliday(cal_today.getTime())) {		// set Start Date
//			cal_today.add(Calendar.DAY_OF_YEAR, 1);				// 加上假日
//			}
	
			this.Today = cal_today.getTime();
		}
	}
	
	// 每次呼叫 setDate 就會接續計算下一個 this.RELEASE_DURATION 過後的日期
	private void setDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		cal_start.setTime(this.Today);		// 得到今天的日期
		cal_end.setTime(this.Today);		// 得到今天的日期
		cal_end.add(Calendar.DAY_OF_YEAR, this.RELEASE_DURATION);
		
		this.StartDate = format.format(cal_start.getTime());		// get start date
		this.EndDate = format.format(cal_end.getTime());			// get end date
		
		// set next Today
		cal_end.add(Calendar.DAY_OF_YEAR, 1);
		this.Today = cal_end.getTime();
	}
}
