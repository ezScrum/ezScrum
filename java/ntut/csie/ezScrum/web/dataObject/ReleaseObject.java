package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.ReleaseDAO;
import ntut.csie.ezScrum.web.databasEnum.ReleaseEnum;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReleaseObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;

	private String mName = "";
	private String mDescription = "";
	private Date mStartDate = new Date();
	private Date mDueDate = new Date();

	private long mCreateTime = 0;
	private long mUpdateTime = 0;

	public ReleaseObject(long projectId) {
		mProjectId = projectId;
	}

	public ReleaseObject(long id, Long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}

	public ReleaseObject setName(String name) {
		mName = name;
		return this;
	}

	public ReleaseObject setDescription(String description) {
		mDescription = description;
		return this;
	}

	public ReleaseObject setStartDate(String startDateString) {
		mStartDate = DateUtil.dayFilter(startDateString);
		return this;
	}

	public ReleaseObject setDueDate(String dueDateString) {
		mDueDate = DateUtil.dayFilter(dueDateString);
		return this;
	}

	public ReleaseObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public ReleaseObject setUpdateTime(long updateTime) {
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

	public String getName() {
		return mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public String getStartDateString() {
		return DateUtil.formatBySlashForm(mStartDate);
	}

	public String getDueDateString() {
		return DateUtil.formatBySlashForm(mDueDate);
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	public static ReleaseObject get(long id) {
		return ReleaseDAO.getInstance().get(id);
	}

	@Override
	public void save() {
		if (exists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			ReleaseObject sprint = ReleaseDAO.getInstance().get(mId);
			resetData(sprint);
		}
	}

	@Override
	public boolean delete() {
		boolean success = ReleaseDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
			mProjectId = DEFAULT_VALUE;
		}
		return success;
	}

	public boolean containsSprint(SprintObject sprint) {
		Date sprintStartDate = DateUtil.dayFilter(sprint.getStartDateString());
		Date sprintDueDate = DateUtil.dayFilter(sprint.getDueDateString());
		boolean isContains = sprintStartDate.after(mStartDate)
				|| DateUtils.isSameDay(sprintStartDate, mStartDate);
		isContains &= sprintDueDate.before(mDueDate)
				|| DateUtils.isSameDay(sprintDueDate, mDueDate);
		return isContains;
	}

	public ArrayList<SprintObject> getSprints() {
		ArrayList<SprintObject> sprints = new ArrayList<>();
		ProjectObject project = ProjectObject.get(mProjectId);
		ArrayList<SprintObject> allSprints = project.getSprints();
		for (SprintObject sprint : allSprints) {
			if (containsSprint(sprint)) {
				sprints.add(sprint);
			}
		}
		return sprints;
	}

	public ArrayList<StoryObject> getStories() {
		ArrayList<SprintObject> sprints = getSprints();
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		for (SprintObject sprint : sprints) {
			stories.addAll(sprint.getStories());
		}
		return stories;
	}

	// 取得該日期前已完成的Story數量
	public double getDoneStoryByDate(Date date) {
		double count = 0;
		ArrayList<StoryObject> stories = getStories();
		for (StoryObject story : stories) {
			// Story Close的時間

			int status = story.getStatus(date);
			if (status == StoryObject.STATUS_DONE) {
				count++;
			}
		}
		return count;
	}

	// 所有story done後，不論何時把story拉到done都把real point設成0
	public double getReleaseAllStoryDone() {
		ArrayList<StoryObject> stories = getStories();
		double count = stories.size();
		for (StoryObject story : stories) {
			// story狀態為done的 count就-1
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				count--;
			}
		}
		return count;
	}

	private void doCreate() {
		mId = ReleaseDAO.getInstance().create(this);
		reload();
	}

	private void doUpdate() {
		mUpdateTime = System.currentTimeMillis();
		ReleaseDAO.getInstance().update(this);
	}

	private void resetData(ReleaseObject release) {
		mId = release.getId();
		mProjectId = release.getProjectId();
		mSerialId = release.getSerialId();

		setName(release.getName());
		setDescription(release.getDescription());
		setStartDate(release.getStartDateString());
		setDueDate(release.getDueDateString());
		setCreateTime(release.getCreateTime());
		setUpdateTime(release.getUpdateTime());
	}

	private boolean exists() {
		ReleaseObject release = ReleaseDAO.getInstance().get(mId);
		return release != null;
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
		JSONObject release = new JSONObject();
		release.put(ReleaseEnum.ID, mId)
				.put(ReleaseEnum.PROJECT_ID, mProjectId)
				.put(ReleaseEnum.NAME, mName)
				.put(ReleaseEnum.DESCRIPTION, mDescription)
				.put(ReleaseEnum.START_DATE, getStartDateString())
				.put(ReleaseEnum.DUE_DATE, getDueDateString())
				.put(ReleaseEnum.CREATE_TIME, mCreateTime)
				.put(ReleaseEnum.UPDATE_TIME, mUpdateTime);
		return release;
	}

}
