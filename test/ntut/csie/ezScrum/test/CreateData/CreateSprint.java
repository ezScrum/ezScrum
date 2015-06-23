package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;

public class CreateSprint {
	private int mSprintCount = 1;
	private CreateProject mCP = null;
	private ArrayList<Long> mSprintsId;

	public String TEST_SPRINT_GOAL = "TEST_SPRINTGOAL_";		    // Sprint Goal
	public String TEST_SPRINT_DAILY_INFO = "TEST_SPRINTDAILYINFO_";	// Sprint Notes
	public final static int SPRINT_INTERVAL = 2;					// 2 weeks
	public final static int SPRINT_MEMBER = 4;						// 2 members
	public final static int SPRINT_HOURS_CAN_COMMIT = 120;			// 120 hours
	public final static int SPRINT_FOCUS_FACTOR = 80;				// 80%
	public final static String SPRINT_DEMOPLACE = "Lab1321";		// daily scrum place

	public Date mToday = null;

	// ========================== 為了可以設定 sprint 而新增下列屬性 ===========================
	private boolean mAutoSetSprint = true;
	private int mSprintIDIndex = 0;
	private ProjectObject mProject = null;

	public CreateSprint(int count, CreateProject CP) {
		mSprintCount = count;
		mCP = CP;

		mAutoSetSprint = false;
		mSprintsId = new ArrayList<Long>();
	}

	public CreateSprint(int sprintCount, int Index, Date today, ProjectObject project) {
		mSprintCount = sprintCount;
		mSprintsId = new ArrayList<Long>();

		mToday = today;
		mAutoSetSprint = false;
		mSprintIDIndex = Index * sprintCount;
		mProject = project;
	}

	public CreateSprint(int SPcount, int Index, Date today, CreateProject cp) {
		mSprintCount = SPcount;
		mSprintsId = new ArrayList<Long>();

		mToday = today;
		mAutoSetSprint = false;
		mSprintIDIndex = Index * SPcount;
		mCP = cp;
	}

	public int getSprintCount() {
		return mSprintCount;
	}

	public ArrayList<Long> getSprintsId() {
		return mSprintsId;	// not implemented yet
	}

	public void exe() {
		Calendar cal = Calendar.getInstance();

		for (int i = 0; i < mCP.getProjectList().size(); i++) {
			mToday = cal.getTime();									// get Today
			ProjectObject project = mCP.getAllProjects().get(i);	// get Project

			for (int j = 0; j < mSprintCount; j++) {
				long id = (j + 1);
				
				SprintInfo sprintInfo = new SprintInfo();
				sprintInfo.id = id;
				sprintInfo.sprintGoal = TEST_SPRINT_GOAL + id;
				sprintInfo.startDate = getDate(mToday, j * SPRINT_INTERVAL * 7 + j);
				sprintInfo.interval = SPRINT_INTERVAL;
				sprintInfo.members = SPRINT_MEMBER;
				sprintInfo.focusFactor = SPRINT_FOCUS_FACTOR;
				sprintInfo.demoDate = getDate(mToday, (j + 1) * SPRINT_INTERVAL * 7 + j);
				sprintInfo.demoPlace = SPRINT_DEMOPLACE;
				sprintInfo.dailyInfo = TEST_SPRINT_DAILY_INFO + id;
				sprintInfo.hoursCanCommit = SPRINT_HOURS_CAN_COMMIT;
				
				SprintPlanMapper sprintPlanMapper = new SprintPlanMapper(project);
				sprintPlanMapper.addSprint(sprintInfo);
				mSprintsId.add(id);
			}
			System.out.println("  " + project.getName() + " create " + mSprintCount + " sprint success.");
		}
		System.out.println("Create " + mCP.getProjectList().size() + " Sprint(s) Finish!");
	}

	private String getDate(Date date, int duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(date);
		calendarEnd.add(Calendar.DAY_OF_YEAR, duration);

		return format.format(calendarEnd.getTime());
	}
}
