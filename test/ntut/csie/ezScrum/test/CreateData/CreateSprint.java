package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

public class CreateSprint {
	private int mSprintCount = 1;
	private CreateProject mCP = null;
	private ArrayList<Long> mSprintsId;
	private ArrayList<SprintObject> mSprints;
	private ArrayList<ProjectObject> mProjects;

	public String TEST_SPRINT_GOAL = "TEST_SPRINTGOAL_";		    // Sprint Goal
	public String TEST_SPRINT_DAILY_INFO = "TEST_SPRINTDAILYINFO_";	// Sprint Notes
	public final static int SPRINT_INTERVAL = 2;					// 2 weeks
	public final static int SPRINT_MEMBER = 4;						// 2 members
	public final static int SPRINT_HOURS_CAN_COMMIT = 120;			// 120 hours
	public final static int SPRINT_FOCUS_FACTOR = 80;				// 80%
	public final static String SPRINT_DEMOPLACE = "Lab1321";		// daily scrum place

	public Date mToday = null;

	public CreateSprint(int count, CreateProject CP) {
		mSprintCount = count;
		mCP = CP;
		mSprintsId = new ArrayList<Long>();
		mSprints = new ArrayList<SprintObject>();
		mProjects = mCP.getAllProjects();
	}

	public CreateSprint(int sprintCount, Date today, ProjectObject project) {
		mSprintCount = sprintCount;
		mSprintsId = new ArrayList<Long>();
		mSprints = new ArrayList<SprintObject>();
		mToday = today;
		mProjects = new ArrayList<ProjectObject>();
		mProjects.add(project);
	}

	public CreateSprint(int SPcount, int Index, Date today, CreateProject cp) {
		mSprintCount = SPcount;
		mSprintsId = new ArrayList<Long>();
		mSprints = new ArrayList<SprintObject>();
		mToday = today;
		mCP = cp;
		mProjects = mCP.getAllProjects();
	}

	public int getSprintCount() {
		return mSprintCount;
	}

	public ArrayList<Long> getSprintsId() {
		return mSprintsId;	// not implemented yet
	}
	
	public ArrayList<SprintObject> getSprints() {
		return mSprints;
	}

	public void exe() {
		Calendar cal = Calendar.getInstance();

		for (int i = 0; i < mProjects.size(); i++) {
			mToday = cal.getTime();						// get Today
			ProjectObject project = mProjects.get(i);	// get Project

			for (int j = 0; j < mSprintCount; j++) {
				long index = (j + 1);
				SprintObject sprint = new SprintObject(project.getId());
				sprint.setSprintGoal(TEST_SPRINT_GOAL + index)
				.setStartDate(getDate(mToday, j * SPRINT_INTERVAL * 7 + j))
				.setInterval(SPRINT_INTERVAL)
				.setMembers(SPRINT_MEMBER)
				.setFocusFactor(SPRINT_FOCUS_FACTOR)
				.setDemoDate(getDate(mToday, ((j + 1) * SPRINT_INTERVAL * 7 + j) - 1))
				.setDueDate(getDate(mToday, ((j + 1) * SPRINT_INTERVAL * 7 + j) - 1))
				.setDemoPlace(SPRINT_DEMOPLACE)
				.setDailyInfo(TEST_SPRINT_DAILY_INFO + index)
				.setHoursCanCommit(SPRINT_HOURS_CAN_COMMIT)
				.save();
				
				long sprintId = sprint.getId();
				mSprintsId.add(sprintId);
				mSprints.add(sprint);
			}
			System.out.println("  " + project.getName() + " create " + mSprintCount + " sprint success.");
		}
		System.out.println("Create " + mProjects.size() + " Sprint(s) Finish!");
	}

	private String getDate(Date date, int duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(date);
		calendarEnd.add(Calendar.DAY_OF_YEAR, duration);

		return format.format(calendarEnd.getTime());
	}
}
