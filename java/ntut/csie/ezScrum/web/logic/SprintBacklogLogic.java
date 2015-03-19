package ntut.csie.ezScrum.web.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class SprintBacklogLogic {

	private IProject mIProject;
	private ProjectObject mProject;
	private IUserSession mUserSession;
	private SprintBacklogMapper mSprintBacklogMapper;

	// 儲存目前處理過的 Sprint Date Column
	private ArrayList<SprintBacklogDateColumn> mCurrentCols = null;
	private ArrayList<Date> mDateList = null;

	public SprintBacklogLogic() {
	}

	/**
	 * 要換成用 ProjectObject 的建構子
	 * 
	 * @param project
	 *            IProject
	 * @param userSession
	 * @param sprintID
	 */
	@Deprecated
	public SprintBacklogLogic(IProject project, IUserSession userSession,
			String sprintID) {
		mIProject = project;
		mUserSession = userSession;
		if (sprintID == null || sprintID.equals("")) {
			sprintID = "-1";
		}
		mSprintBacklogMapper = createSprintBacklogMapper(Long
				.parseLong(sprintID));
	}

	public SprintBacklogLogic(ProjectObject project, IUserSession userSession,
			long sprintId) {
		mProject = project;
		mUserSession = userSession;
		mSprintBacklogMapper = createSprintBacklogMapper(sprintId);
	}

	public SprintBacklogMapper getSprintBacklogMapper() {
		return mSprintBacklogMapper;
	}

	/**
	 * 依據 sprintId 取得 SprintBacklogMapper
	 * 
	 * @param sprintId
	 * @return SprintBacklogMapper
	 */
	private SprintBacklogMapper createSprintBacklogMapper(long sprintId) {
		SprintBacklogMapper sprintBacklogMapper = null;

		try {
			if (sprintId == -1 || sprintId == 0) {
				sprintBacklogMapper = new SprintBacklogMapper(mIProject,
						mUserSession);
			} else {
				sprintBacklogMapper = new SprintBacklogMapper(mIProject,
						mUserSession, sprintId);
			}
		} catch (Exception e) {
			sprintBacklogMapper = null;
		}
		return sprintBacklogMapper;
	}

	// for ezScrum 1.8
	public void checkOutTask(long id, String name, String handlerUsername,
			String partners, String notes, String changeDate) {
		Date closeDate = new Date();
		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate,
					DateUtil._16DIGIT_DATE_TIME);
		}

		AccountObject handler = AccountObject.get(handlerUsername);
		long handlerId = -1;
		if (handler != null) {
			handlerId = handler.getId();
		}

		ArrayList<Long> partnersId = new ArrayList<Long>();

		for (String partnerUsername : partners.split(";")) {
			AccountObject partner = AccountObject.get(partnerUsername);
			if (partner != null) {
				partnersId.add(partner.getId());
			}
		}

		mSprintBacklogMapper.checkOutTask(id, name, handlerId, partnersId,
				notes, closeDate);
	}

	public void closeStory(long id, String notes, String changeDate) {
		mSprintBacklogMapper.closeStory(id, notes, changeDate);
	}
	
	public void reopenStory(long id, String name, String notes, String changeDate) {
		mSprintBacklogMapper.reopenStory(id, name, notes, changeDate);
	}
	
	public void reopenTask(long id, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.reopenTask(id, name, notes, closeDate);
	}
	
	public void resetTask(long id, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.resetTask(id, name, notes, closeDate);
	}
	
	public void closeTask(long id, String name, String notes, int actual, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.closeTask(id, name, notes, actual, closeDate);
	}
	
	// for ezScrum 1.8
	// TaskInfo should include task id
	public void updateTask(TaskInfo taskInfo) {
		mSprintBacklogMapper.updateTask(taskInfo);
	}

	/**
	 * 根據 Sprint ID 取得該 Sprint 的工作天數
	 * 
	 * @param sprintId
	 * @return
	 */
	public int getSprintAvailableDays(long sprintId) {
		SprintBacklogMapper backlog = createSprintBacklogMapper(sprintId);
		int availableDays = 0;
		if (backlog.getSprintId() > 0) {
			ISprintPlanDesc desc = (new SprintPlanMapper(mIProject))
					.getSprintPlan(Integer.toString(backlog.getSprintId()));
			availableDays = Integer.parseInt(desc.getInterval()) * 5; // 一個禮拜五天
		}
		return availableDays;
	}

	/**
	 * 根據Sprint的開始日期和可工作天數，計算出SprintBacklog的data column上的日期。
	 * 
	 * @param startDate
	 * @param availableDays
	 * @return
	 */
	public ArrayList<SprintBacklogDateColumn> calculateSprintBacklogDateList(
			Date startDate, int availableDays) {
		if (startDate == null)
			return new ArrayList<SprintBacklogDateColumn>();

		ArrayList<SprintBacklogDateColumn> cols = new ArrayList<SprintBacklogDateColumn>();
		ArrayList<Date> dates = new ArrayList<Date>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate); // 設定為現在時間

		String ID_Date = "Date_";
		int count = 1;
		while (availableDays-- > 0) {
			while (DateUtil.isHoliday(cal.getTime())) { // 判斷假日
				cal.add(Calendar.DATE, 1); // 跳過此一工作天
			}

			SimpleDateFormat format = new SimpleDateFormat("MM/dd");
			String date = format.format(cal.getTime());

			String ID = ID_Date + Integer.toString(count++);
			cols.add(new SprintBacklogDateColumn(ID, date)); // 將可工作的日期加入 list

			dates.add(cal.getTime());
			cal.add(Calendar.DATE, 1); // 加一工作天
		}

		mDateList = dates;
		mCurrentCols = cols;

		return cols;
	}

	public ArrayList<SprintBacklogDateColumn> getCurrentDateColumns() {
		return mCurrentCols;
	}

	public ArrayList<Date> getCurrentDateList() {
		return mDateList;
	}

	/************************************************************
	 * 
	 * =================取得Iteration的描述===============
	 * 
	 *************************************************************/

	/**
	 * 取得該sprint開始工作的日期
	 * 
	 * @return
	 */
	public Date getSprintStartWorkDate() {
		Date m_startDate = mSprintBacklogMapper.getSprintStartDate();
		Date m_endDate = mSprintBacklogMapper.getSprintEndDate();
		Date workDate = DateUtil.nearWorkDate(m_startDate,
				DateUtil.BACK_DIRECTION);
		if (workDate.getTime() > m_endDate.getTime())
			return m_startDate;
		return workDate;
	}

	/**
	 * 取得該sprint結束工作的日期
	 * 
	 * @return
	 */
	public Date getSprintEndWorkDate() {
		Date m_startDate = mSprintBacklogMapper.getSprintStartDate();
		Date m_endDate = mSprintBacklogMapper.getSprintEndDate();
		Date workDate = DateUtil.nearWorkDate(m_endDate,
				DateUtil.FRONT_DIRECTION);
		if (workDate.getTime() < m_startDate.getTime())
			return m_endDate;
		return workDate;
	}

	/**
	 * 取得該sprint可工作的天數
	 * 
	 * @return
	 */
	public int getSprintWorkDays() {
		// 扣除假日後，Sprint的總天數
		int dayOfSprint = -1;

		Calendar indexDate = Calendar.getInstance();
		indexDate.setTime(getSprintStartWorkDate());
		long endTime = getSprintEndWorkDate().getTime();

		while (!(indexDate.getTimeInMillis() > endTime)) {
			// 扣除假日
			if (!DateUtil.isHoliday(indexDate.getTime())) {
				dayOfSprint++;
			}
			indexDate.add(Calendar.DATE, 1);
		}

		return dayOfSprint;
	}

	public boolean isOutOfSprint() {
		long OneDay = ScrumEnum.DAY_MILLISECOND;
		// 在當天的晚上11:59:59仍是當天
		return (new Date().getTime() > (getSprintEndWorkDate().getTime()
				+ OneDay - 1));
	}

	/**
	 * Get all tasks estimate point
	 * 
	 * @return task estimate point
	 */
	public double getTaskCurrentEstimatePoint() {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getAllTasks();
		double point = 0;
		for (TaskObject task : tasks) {
			point += task.getEstimate();
		}
		return point;
	}

	/**
	 * Get issue estimate point (task 已被抽出來，story 完成後就可以拿掉)
	 * 
	 * @param Issuetype
	 * @return issue estimate point
	 */
	@Deprecated
	public double getCurrentPoint(String type) {
		List<IIssue> items;
		double point = 0;
		if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
			point = getTaskCurrentEstimatePoint();
		} else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
			items = getStories();
			for (IIssue item : items) {
				point += Double.parseDouble(item.getEstimated());
			}
		} else
			return 0;

		return point;
	}

	/**
	 * Get all tasks remains point
	 * 
	 * @return task remains point
	 */
	public double getTaskCurrnetRemainsPoint() {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getAllTasks();
		double point = 0;
		for (TaskObject task : tasks) {
			if (task.getStatus() == TaskObject.STATUS_DONE) {
				continue;
			}
			point += task.getRemains();
		}
		return point;
	}

	/**
	 * Get issue remains point (task 已被抽出來，story 完成後就可以拿掉)
	 * 
	 * @param Issuetype
	 * @return issue remains point
	 */
	@Deprecated
	public double getCurrentUnclosePoint(String type) {
		List<IIssue> items;
		double point = 0;
		if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
			point = getTaskCurrnetRemainsPoint();
		} else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
			items = getStories();
			for (IIssue item : items) {
				if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS)
					continue;
				point += Double.parseDouble(item.getEstimated());
			}

		} else
			return 0;

		return point;
	}

	public ArrayList<IIssue> getStories() {
		ArrayList<IIssue> stories = mSprintBacklogMapper
				.getAllStories(ScrumEnum.STORY_ISSUE_TYPE);
		return sort(stories, "null");
	}

	public ArrayList<IIssue> getStoriesByImp() {
		ArrayList<IIssue> stories = mSprintBacklogMapper
				.getAllStories(ScrumEnum.STORY_ISSUE_TYPE);
		return sortByImp(stories);
	}

	/**
	 * 根據tag name的值來排序
	 * 
	 * @param list
	 * @param tagName
	 * @return
	 */
	private ArrayList<IIssue> sort(Collection<IIssue> list, String tagName) {
		ArrayList<IIssue> sortedList = new ArrayList<IIssue>();
		for (IIssue issue : list) {
			int index = 0;
			int valueSource = 0;

			if (issue.getTagValue(tagName) != null)
				valueSource = Integer.parseInt(issue.getTagValue(tagName));

			for (IIssue sortedIssue : sortedList) {
				int valueTarget = 0;
				if (sortedIssue.getTagValue(tagName) != null)
					valueTarget = Integer.parseInt(sortedIssue
							.getTagValue(tagName));
				if (valueSource > valueTarget)
					break;
				index++;
			}
			sortedList.add(index, issue);
		}

		return sortedList;
	}

	/**
	 * 根據 importance 的值來排序
	 * 
	 * @param list
	 * @return
	 */
	private ArrayList<IIssue> sortByImp(Collection<IIssue> list) {
		ArrayList<IIssue> sortedList = new ArrayList<IIssue>();

		for (IIssue issue : list) {
			int index = 0;
			for (index = 0; index < sortedList.size(); index++) {
				if (Integer.parseInt(issue.getImportance()) > Integer
						.parseInt(sortedList.get(index).getImportance())) {
					break;
				}
			}
			sortedList.add(index, issue);
		}

		return sortedList;
	}
	
	private Date parseToDate(String dateString) {
		Date closeDate = new Date();
		if (dateString != null && !dateString.equals("")) {
			closeDate = DateUtil.dayFillter(dateString,
					DateUtil._16DIGIT_DATE_TIME);
		}
		return closeDate;
	}

	public class SprintBacklogDateColumn {
		private String Id;
		private String Name;

		public SprintBacklogDateColumn(String ID, String name) {
			this.Id = ID;
			this.Name = name;
		}
	}
}
