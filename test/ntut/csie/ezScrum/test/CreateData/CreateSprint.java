package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class CreateSprint {
	private int mSprintCount = 1;
	private CreateProject mCP = null;
	private ArrayList<Long> mSprintsId;

	public String TEST_SPRINT_GOAL = "TEST_SPRINTGOAL_";		// Sprint Goal
	public String TEST_SPRINT_NOTE = "TEST_SPRINTNOTE_";		// Sprint Notes
	public final static String SPRINT_INTERVAL = "2";			// 2 weeks
	public final static String SPRINT_MEMBER = "2";				// 2 members
	public final static String SPRINT_AVAILABLE_DAY = "10";		// 10 days
	public final static String SPRINT_FOCUS_FACTOR = "100";		// 100%
	public final static String SPRINT_DEMOPLACE = "Lab 1321";	// daily scrum place

	public Date mToday = null;

	// ========================== 為了可以設定 sprint 而新增下列屬性 ===========================
	private boolean mAutoSetSprint = true;
	private int mSprintIDIndex = 0;
	private IProject mProject = null;

	public CreateSprint(int count, CreateProject CP) {
		mSprintCount = count;
		mCP = CP;

		mAutoSetSprint = false;
		mSprintsId = new ArrayList<Long>();
	}

	public CreateSprint(int sprintCount, int Index, Date today, IProject project) {
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
				ISprintPlanDesc desc = createDesc(j);
				SprintPlanMapper spMapper = new SprintPlanMapper(project);
				spMapper.addSprintPlan(desc);
				mSprintsId.add((long) (j + 1));
			}

			System.out.println("  " + project.getName() + " create " + mSprintCount + " sprint success.");
		}
		System.out.println("Create " + mCP.getProjectList().size() + " Sprint(s) Finish!");
	}

	private ISprintPlanDesc createDesc(int index) {
		ISprintPlanDesc desc = new SprintPlanDesc();

		String ID = Integer.toString(index + 1);
		desc.setID(ID);
		desc.setGoal(TEST_SPRINT_GOAL + ID);
		desc.setStartDate(getDate(mToday, index * Integer.parseInt(SPRINT_INTERVAL) * 7));
		desc.setInterval(SPRINT_INTERVAL);
		desc.setMemberNumber(SPRINT_MEMBER);
		desc.setAvailableDays(SPRINT_AVAILABLE_DAY);
		desc.setFocusFactor(SPRINT_FOCUS_FACTOR);
		desc.setDemoDate(desc.getEndDate());
		desc.setDemoPlace(SPRINT_DEMOPLACE);
		desc.setNotes(TEST_SPRINT_NOTE + ID);

		return desc;
	}

	private String getDate(Date date, int duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		cal_start.setTime(date);		// 得到今天的日期
		cal_end.setTime(date);			// 得到今天的日期
		cal_end.add(Calendar.DAY_OF_YEAR, duration);

		return format.format(cal_end.getTime());	// get start date
	}
}
