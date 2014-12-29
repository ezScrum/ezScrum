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
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class SprintBacklogLogic {

	private IProject mProject;
	private IUserSession mUserSession;
	private SprintBacklogMapper mSprintBacklogMapper;

	// 儲存目前處理過的 Sprint Date Column
	private List<SprintBacklogDateColumn> currentCols = null;
	private ArrayList<Date> dateList = null;

	public SprintBacklogLogic() {
	}

	public SprintBacklogLogic(IProject project, IUserSession userSession,
			String sprintID) {
		mProject = project;
		mUserSession = userSession;
		mSprintBacklogMapper = createSprintBacklogMapper(sprintID);
	}

	public SprintBacklogMapper getSprintBacklogMapper() {
		return mSprintBacklogMapper;
	}

	/**
	 * 判斷使用者輸入的sprintID是否為合法
	 * 
	 * @param mProject
	 * @param mUserSession
	 * @param sprintId
	 * @return
	 */
	private SprintBacklogMapper createSprintBacklogMapper(String sprintId) {
		SprintBacklogMapper sprintBacklogMapper = null;

		try {
			// sprint 不存在，回傳最近的一個 sprint 或 空的 sprint
			if (sprintId == null || sprintId.equals("") || sprintId.equals("0")
					|| sprintId.equals("-1")) {
				sprintBacklogMapper = new SprintBacklogMapper(mProject,
						mUserSession);
			} else {
				sprintBacklogMapper = new SprintBacklogMapper(mProject,
						mUserSession, Long.parseLong(sprintId));
			}
		} catch (Exception e) {
			// 已經處理過不必輸出 Exception
			sprintBacklogMapper = null;
		}
		return sprintBacklogMapper;
	}

	public long addTask(long projectId, String name, String notes,
			int estimate, long handler, ArrayList<Long> partners, long storyId,
			Date date) {

		long taskId = mSprintBacklogMapper.addTask(projectId, name, notes,
				estimate, handler, partners, storyId, date);

		return taskId;
	}

	public boolean editTask(long taskId, String name, int estimate,
			int remains, long handler, ArrayList<Long> partners,
			int actualHour, String notes, Date modifyDate) {

		mSprintBacklogMapper.updateTask(taskId, name, estimate, remains,
				handler, partners, actualHour, notes, modifyDate);

		return false;
	}

	public void checkOutTask(long id, String name, long handler,
			ArrayList<Long> partners, String notes, String changeDate) {
		Date closeDate = null;
		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate,
					DateUtil._16DIGIT_DATE_TIME);
		} else {
			closeDate = new Date();
		}

		mSprintBacklogMapper.checkOutTask(id, name, handler, partners, notes,
				closeDate);
	}

	public void doneTask(long id, String name, long handlerId,
			ArrayList<Long> partners, String notes, Date changeDate) {
		
	}

	public void doneIssue(long id, String name, String notes,
			String changeDate, String actualHour) {
		IIssue task = this.mSprintBacklogMapper.getIssue(id);
		Date closeDate = null;
		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate,
					DateUtil._16DIGIT_DATE_TIME);
		}
		// 如果issue的type為Task時則將Remians設定為空值，否則reopen時由於Remains為0
		// 圖表將不會有任何變動
		if (task.getCategory().equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			this.editTask(id, name, null, "0", task.getAssignto(),
					task.getPartners(), actualHour, notes, closeDate);
		} else {
			this .editTask(id, name, null, null, task.getAssignto(),
					task.getPartners(), actualHour, notes, closeDate);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.mSprintBacklogMapper.doneIssue(id, notes, changeDate);
	}

	/**
	 * 根據Sprint ID取得該Sprint的工作天數
	 * 
	 * @param sprintID
	 * @return
	 */
	public int getSprintAvailableDays(String sprintID) {
		SprintBacklogMapper backlog = this.createSprintBacklogMapper(sprintID);
		int availableDays = 0;
		if (backlog.getSprintPlanId() > 0) {
			ISprintPlanDesc desc = (new SprintPlanMapper(this.mProject))
					.getSprintPlan(Integer.toString(backlog.getSprintPlanId()));
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
	public List<SprintBacklogDateColumn> calculateSprintBacklogDateList(
			Date startDate, int availableDays) {
		if (startDate == null)
			return new ArrayList<SprintBacklogDateColumn>();

		List<SprintBacklogDateColumn> cols = new ArrayList<SprintBacklogDateColumn>();
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

		this.dateList = dates;
		this.currentCols = cols;

		return cols;
	}

	public List<SprintBacklogDateColumn> getCurrentDateColumns() {
		return this.currentCols;
	}

	public ArrayList<Date> getCurrentDateList() {
		return this.dateList;
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
		Date m_startDate = this.mSprintBacklogMapper.getSprintStartDate();
		Date m_endDate = this.mSprintBacklogMapper.getSprintEndDate();
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
		Date m_startDate = this.mSprintBacklogMapper.getSprintStartDate();
		Date m_endDate = this.mSprintBacklogMapper.getSprintEndDate();
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
		indexDate.setTime(this.getSprintStartWorkDate());
		long endTime = this.getSprintEndWorkDate().getTime();

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
		return (new Date().getTime() > (this.getSprintEndWorkDate().getTime()
				+ OneDay - 1));
	}

	public double getCurrentPoint(String type) {
		List<IIssue> items;
		double point = 0;
		if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
			items = this.mSprintBacklogMapper.getTasks();
			for (IIssue item : items) {
				point += Double.parseDouble(item.getEstimated());
			}
		} else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
			items = this.getStories();
			for (IIssue item : items) {
				point += Double.parseDouble(item.getEstimated());
			}
		} else
			return 0;

		return point;
	}

	public double getCurrentUnclosePoint(String type) {
		List<IIssue> items;
		double point = 0;
		if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
			items = this.mSprintBacklogMapper.getTasks();
			for (IIssue item : items) {
				if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS)
					continue;
				point += Double.parseDouble(item.getRemains());
			}
		} else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
			items = this.getStories();
			for (IIssue item : items) {
				if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS)
					continue;
				point += Double.parseDouble(item.getEstimated());
			}

		} else
			return 0;

		return point;
	}

	public List<IIssue> getStories() {
		List<IIssue> stories = this.mSprintBacklogMapper
				.getIssues(ScrumEnum.STORY_ISSUE_TYPE);
		return this.sort(stories, "null");
	}

	public List<IIssue> getStoriesByImp() {
		List<IIssue> stories = this.mSprintBacklogMapper
				.getIssues(ScrumEnum.STORY_ISSUE_TYPE);
		return this.sortByImp(stories);
	}

	/**
	 * 根據tag name的值來排序
	 * 
	 * @param list
	 * @param tagName
	 * @return
	 */
	private List<IIssue> sort(Collection<IIssue> list, String tagName) {
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
	private List<IIssue> sortByImp(Collection<IIssue> list) {
		List<IIssue> sortedList = new ArrayList<IIssue>();

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

	/**
	 * 根據 id 取得 task
	 * 
	 * @param id
	 * @return
	 */
	public IIssue getTaskById(long id) {
		List<IIssue> tasks = this.mSprintBacklogMapper.getTasks();
		for (IIssue task : tasks) {
			if (task.getIssueID() == id)
				return task;
		}
		return null;
	}

	private class SprintBacklogDateColumn {
		private String Id;
		private String Name;

		public SprintBacklogDateColumn(String ID, String name) {
			this.Id = ID;
			this.Name = name;
		}

		public String GetColumnName() {
			return this.Name;
		}
	}
}
