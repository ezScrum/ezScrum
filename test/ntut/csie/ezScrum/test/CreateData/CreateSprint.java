package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class CreateSprint {
	// private static Log log = LogFactory.getLog(CreateSprint.class);

	private int SprintCount = 1;
	private CreateProject CP = null;

	private List<String> SprintIDList;

	public String TEST_SPRINT_GOAL = "TEST_SPRINTGOAL_";		// Sprint Goal
	public String TEST_SPRINT_NOTE = "TEST_SPRINTNOTE_";		// Sprint Notes
	public final static String SPRINT_INTERVAL = "2";			// 2 weeks
	public final static String SPRINT_MEMBER = "2";				// 2 members
	public final static String SPRINT_AVAILABLE_DAY = "10";		// 10 days
	public final static String SPRINT_FOCUS_FACTOR = "100";		// 100%
	public final static String SPRINT_DEMOPLACE = "Lab 1321";	// daily scrum place

	public Date Today = null;

	// ========================== 為了可以設定 sprint 而新增下列屬性 ===========================
	private boolean AutoSetSprint = true;
	private int SprintIDIndex = 0;
	private IProject P = null;

	public CreateSprint(int Count, CreateProject cp) {
		this.SprintCount = Count;
		this.CP = cp;

		this.AutoSetSprint = false;
		this.SprintIDList = new ArrayList<String>();
	}

	public CreateSprint(int SPcount, int Index, Date today, IProject p) {
		this.SprintCount = SPcount;
		this.SprintIDList = new ArrayList<String>();

		this.Today = today;
		this.AutoSetSprint = false;
		this.SprintIDIndex = Index * SPcount;
		this.P = p;
	}

	public CreateSprint(int SPcount, int Index, Date today, CreateProject cp) {
		this.SprintCount = SPcount;
		this.SprintIDList = new ArrayList<String>();

		this.Today = today;
		this.AutoSetSprint = false;
		this.SprintIDIndex = Index * SPcount;
		this.CP = cp;
	}

	public int getSprintCount() {
		return this.SprintCount;
	}

	public List<String> getSprintIDList() {
		return this.SprintIDList;	// not implemented yet
	}

	public void exe() {
		Calendar cal = Calendar.getInstance();

		for (int i = 0; i < this.CP.getProjectList().size(); i++) {
			this.Today = cal.getTime();									// get Today
			IProject project = this.CP.getProjectList().get(i);			// get Project

			for (int j = 0; j < this.SprintCount; j++) {
				ISprintPlanDesc desc = createDesc(j);
				SprintPlanMapper spMapper = new SprintPlanMapper(project);
				spMapper.addSprintPlan(desc);
				SprintIDList.add(String.valueOf((j + 1)));
			}

			System.out.println("  " + project.getName() + " create " + this.SprintCount + " sprint success.");
		}
		System.out.println("Create " + this.CP.getProjectList().size() + " Sprint(s) Finish!");
	}

	private ISprintPlanDesc createDesc(int index) {
		ISprintPlanDesc desc = new SprintPlanDesc();

		String ID = Integer.toString(index + 1);
		desc.setID(ID);
		desc.setGoal(this.TEST_SPRINT_GOAL + ID);
		desc.setStartDate(getDate(this.Today, index * Integer.parseInt(this.SPRINT_INTERVAL) * 7));
		desc.setInterval(this.SPRINT_INTERVAL);
		desc.setMemberNumber(this.SPRINT_MEMBER);
		desc.setAvailableDays(this.SPRINT_AVAILABLE_DAY);
		desc.setFocusFactor(this.SPRINT_FOCUS_FACTOR);
		desc.setDemoDate(desc.getEndDate());
		desc.setDemoPlace(this.SPRINT_DEMOPLACE);
		desc.setNotes(this.TEST_SPRINT_NOTE + ID);

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
