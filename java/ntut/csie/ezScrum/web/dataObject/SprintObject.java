package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.SprintDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.web.databasEnum.SprintEnum;
import ntut.csie.jcis.core.util.DateUtil;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SprintObject implements IBaseObject{
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;

	private int mInterval = 0;
	private int mMembers = 0;
	private int mHoursCanCommit = 0;
	private int mFocusFactor = 0;
	private String mSprintGoal = "";
	private Date mStartDate = null;
	private Date mDemoDate = null;
	private String mDemoPlace = "";
	private String mDailyInfo = "";

	private long mCreateTime = 0;
	private long mUpdateTime = 0;

	public SprintObject(long projectId) {
		mProjectId = projectId;
	}

	public SprintObject(long id, Long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}

	public SprintObject setInterval(int interval) {
		mInterval = interval;
		return this;
	}

	public SprintObject setMembers(int members) {
		mMembers = members;
		return this;
	}

	public SprintObject setHoursCanCommit(int hoursCanCommit) {
		mHoursCanCommit = hoursCanCommit;
		return this;
	}

	public SprintObject setFocusFactor(int focusFactor) {
		mFocusFactor = focusFactor;
		return this;
	}

	public SprintObject setSprintGoal(String sprintGoal) {
		mSprintGoal = sprintGoal;
		return this;
	}

	public SprintObject setStartDate(String startDate) {
		mStartDate = DateUtil.dayFilter(startDate);
		return this;
	}

	public SprintObject setDemoDate(String demoDate) {
		mDemoDate = DateUtil.dayFilter(demoDate);
		return this;
	}

	public SprintObject setDemoPlace(String demoPlace) {
		mDemoPlace = demoPlace;
		return this;
	}

	public SprintObject setDailyInfo(String dailyInfo) {
		mDailyInfo = dailyInfo;
		return this;
	}

	public SprintObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public SprintObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
	}

	public long getId() {
		return mId;
	}

	public long getSerialId() {
		return mSerialId;
	}

	public long getProjectId() {
		return mProjectId;
	}

	public int getInterval() {
		return mInterval;
	}

	public int getMembersNumber() {
		return mMembers;
	}

	public int getHoursCanCommit() {
		return mHoursCanCommit;
	}

	public int getFocusFactor() {
		return mFocusFactor;
	}

	public String getSprintGoal() {
		return mSprintGoal;
	}

	public String getStartDate() {
		return DateUtil.formatBySlashForm(mStartDate);
	}

	public String getDemoDate() {
		return DateUtil.formatBySlashForm(mDemoDate);
	}

	public String getDemoPlace() {
		return mDemoPlace;
	}

	public String getDailyInfo() {
		return mDailyInfo;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}
	
	public static SprintObject get(long id) {
		return SprintDAO.getInstance().get(id);
	}
	
	public ArrayList<StoryObject> getStorie() {
		return StoryDAO.getInstance().getStoriesBySprintId(mId);
	}

	@Override
	public void save() {
		if (exists()) {
			mUpdateTime = System.currentTimeMillis();
			doUpdate();
		} else {
			doCreate();
		}
	}

	/**
	 * Update time will equal to parameter you passed.
	 * 
	 * @param specificTime
	 */
	public void save(long specificTime) {
		if (exists()) {
			mUpdateTime = specificTime;
			doUpdate(specificTime);
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			SprintObject sprint = SprintDAO.getInstance().get(mId);
			resetData(sprint);
		}
	}

	@Override
	public boolean delete() {
		boolean success = SprintDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
			mProjectId = DEFAULT_VALUE;
		}
		return success;
	}
	
	private void doCreate() {
		mId = SprintDAO.getInstance().create(this);
		reload();
		
	}

	private void doUpdate() {
		SprintDAO.getInstance().update(this);
	}
	
	private void doUpdate(long specificTime) {
		mUpdateTime = specificTime;
		SprintDAO.getInstance().update(this);
	}
	
	private void resetData(SprintObject sprint) {
		mId = sprint.getId();
		mProjectId = sprint.getProjectId();
		mSerialId = sprint.getSerialId();
		
		setInterval(sprint.getInterval());
		setMembers(sprint.getMembersNumber());
		setHoursCanCommit(sprint.getHoursCanCommit());
		setFocusFactor(sprint.getFocusFactor());
		setSprintGoal(sprint.getSprintGoal());
		setStartDate(sprint.getStartDate());
		setDemoDate(sprint.getDemoDate());
		setDemoPlace(sprint.getDemoPlace());
		setDailyInfo(sprint.getDailyInfo());
		setCreateTime(sprint.getCreateTime());
		setUpdateTime(sprint.getUpdateTime());
	}
	
	private boolean exists() {
		SprintObject sprint = SprintDAO.getInstance().get(mId);
		return sprint != null;
	}
	
	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	@Override
    public JSONObject toJSON() throws JSONException {
		JSONObject sprint = new JSONObject();
		JSONArray stories = new JSONArray();

		for (StoryObject story : getStorie()) {
			stories.put(story.getId());
		}

		sprint.put(SprintEnum.ID, mId)
		        .put(SprintEnum.PROJECT_ID, mProjectId)
		        .put(SprintEnum.INTERVAL, mInterval)
		        .put(SprintEnum.MEMBERS, mMembers)
		        .put(SprintEnum.SERIAL_ID, mSerialId)
		        .put(SprintEnum.GOAL, mSprintGoal)
		        .put(SprintEnum.AVAILABLE_HOURS, mHoursCanCommit)
		        .put(SprintEnum.FOCUS_FACTOR, mFocusFactor)
		        .put(SprintEnum.DEMO_DATE, mDemoDate)
		        .put(SprintEnum.DEMO_PLACE, mDemoPlace)
		        .put(SprintEnum.DAILY_INFO, mDailyInfo)
		        .put(SprintEnum.CREATE_TIME, mCreateTime)
		        .put(SprintEnum.UPDATE_TIME, mUpdateTime)
		        .put("stories", stories);
		return sprint;
    }

}
