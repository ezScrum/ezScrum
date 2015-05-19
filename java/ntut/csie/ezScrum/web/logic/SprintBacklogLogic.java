package ntut.csie.ezScrum.web.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import edu.emory.mathcs.backport.java.util.Collections;

public class SprintBacklogLogic {
	private ProjectObject mProject;
	private IProject mIProject;
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
	 * @param sprintId
	 */
	@Deprecated
	public SprintBacklogLogic(IProject project, long sprintId) {
		mIProject = project;
		mSprintBacklogMapper = createSprintBacklogMapper(sprintId);
	}
	
	public SprintBacklogLogic(ProjectObject project, long sprintId) {
		mProject = project;
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
				sprintBacklogMapper = new SprintBacklogMapper(mProject);
			} else {
				sprintBacklogMapper = new SprintBacklogMapper(mProject, sprintId);
			}
		} catch (Exception e) {
			sprintBacklogMapper = null;
		}
		return sprintBacklogMapper;
	}
	
	public void closeStory(long id, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.closeStory(id, name, notes, closeDate);
	}
	
	public void reopenStory(long id, String name, String notes,
			String changeDate) {
		Date reopenDate = parseToDate(changeDate);
		mSprintBacklogMapper.reopenStory(id, name, notes, reopenDate);
	}

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

	public void closeTask(long id, String name, String notes, int actual,
			String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.closeTask(id, name, notes, actual, closeDate);
	}

	public void reopenTask(long id, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.reopenTask(id, name, notes, closeDate);
	}

	public void resetTask(long id, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.resetTask(id, name, notes, closeDate);
	}

	/**
	 * 根據 sprint id 取得該 sprint 的工作天數
	 * 
	 * @param sprintId
	 * @return
	 */
	public int getSprintAvailableDays(long sprintId) {
		SprintBacklogMapper backlog = createSprintBacklogMapper(sprintId);
		int availableDays = 0;
		if (backlog.getSprintId() > 0) {
			ISprintPlanDesc sprint = (new SprintPlanMapper(mProject))
					.getSprintPlan(Long.toString(backlog.getSprintId()));
			availableDays = Integer.parseInt(sprint.getInterval()) * 5; // 一個禮拜五天
		}
		return availableDays;
	}

	/**
	 * 根據 sprint 的開始日期和可工作天數，計算出 sprintBacklog 的 data column 上的日期。
	 * 
	 * @param startDate
	 * @param availableDays
	 * @return
	 */
	public ArrayList<SprintBacklogDateColumn> calculateSprintBacklogDateList(
			Date startDate, int availableDays) {
		if (startDate == null) {
			return new ArrayList<SprintBacklogDateColumn>();
		}

		ArrayList<SprintBacklogDateColumn> dateColumns = new ArrayList<SprintBacklogDateColumn>();
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

			String dateId = ID_Date + Integer.toString(count++);
			dateColumns.add(new SprintBacklogDateColumn(dateId, date)); // 將可工作的日期加入
																		// list

			dates.add(cal.getTime());
			cal.add(Calendar.DATE, 1); // 加一工作天
		}

		mDateList = dates;
		mCurrentCols = dateColumns;

		return dateColumns;
	}

	public ArrayList<SprintBacklogDateColumn> getCurrentDateColumns() {
		return mCurrentCols;
	}

	public ArrayList<Date> getCurrentDateList() {
		return mDateList;
	}

	/*************************************************************
	 * ====================== 取得 Sprint 的描述 ====================
	 *************************************************************/

	/**
	 * 取得該 sprint 開始工作的日期
	 * 
	 * @return StartWorkDate
	 */
	public Date getSprintStartWorkDate() {
		Date startDate = mSprintBacklogMapper.getSprintStartDate();
		Date endDate = mSprintBacklogMapper.getSprintEndDate();
		Date workDate = DateUtil.nearWorkDate(startDate,
				DateUtil.BACK_DIRECTION);
		if (workDate.getTime() > endDate.getTime())
			return startDate;
		return workDate;
	}

	/**
	 * 取得該 sprint 結束工作的日期
	 * 
	 * @return EndWorkDate
	 */
	public Date getSprintEndWorkDate() {
		Date startDate = mSprintBacklogMapper.getSprintStartDate();
		Date endDate = mSprintBacklogMapper.getSprintEndDate();
		Date workDate = DateUtil
				.nearWorkDate(endDate, DateUtil.FRONT_DIRECTION);
		if (workDate.getTime() < startDate.getTime())
			return endDate;
		return workDate;
	}

	/**
	 * 取得該 sprint 可工作的天數
	 * 
	 * @return WorkDays
	 */
	public int getSprintWorkDays() {
		// 扣除假日後，Sprint 的總天數
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
		long oneDay = ScrumEnum.DAY_MILLISECOND;
		// 在當天的晚上 11:59:59 仍是當天
		return (new Date().getTime() > (getSprintEndWorkDate().getTime()
				+ oneDay - 1));
	}

	/**
	 * Get all tasks estimate point in one sprint
	 * 
	 * @return task estimate point
	 */
	public double getTaskEstimatePoints() {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getAllTasks();
		double point = 0;
		for (TaskObject task : tasks) {
			point += task.getEstimate();
		}
		return point;
	}

	/**
	 * Get all stories point in one sprint
	 * 
	 * @return total story point
	 */
	public double getTotalStoryPoints() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getAllStories();
		double point = 0;
		for (StoryObject story : stories) {
			point += story.getEstimate();
		}
		return point;
	}

	/**
	 * Get all tasks remains point in one sprint
	 * 
	 * @return task remains point
	 */
	public double getTaskRemainsPoints() {
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
	 * Get all stories unclosed point in one sprint
	 * 
	 * @return story unclosed point
	 */
	public double getStoryUnclosedPoints() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getAllStories();
		double point = 0;
		for (StoryObject story : stories) {
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				continue;
			}
			point += story.getEstimate();
		}
		return point;
	}

	public ArrayList<StoryObject> getStories() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getAllStories();
		return sort(stories, "");
	}

	public ArrayList<StoryObject> getStoriesByImp() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getAllStories();
		return sort(stories, "IMP");
	}

	/**
	 * 根據 story column 的值來排序
	 * 
	 * @param stories
	 * @param sortedColumn
	 * @return sorted stories
	 */
	private ArrayList<StoryObject> sort(ArrayList<StoryObject> stories,
			String sortedColumn) {
		if (sortedColumn.equals("EST")) {
			Collections.sort(stories, new StoryComparator(
					StoryComparator.TYPE_EST));
		} else if (sortedColumn.equals("IMP")) {
			Collections.sort(stories, new StoryComparator(
					StoryComparator.TYPE_IMP));
		} else if (sortedColumn.equals("VAL")) {
			Collections.sort(stories, new StoryComparator(
					StoryComparator.TYPE_VAL));
		} else {
			Collections.sort(stories, new StoryComparator(
					StoryComparator.TYPE_ID));
		}
		return stories;
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

	/**
	 * 給 story 做 sort 時，可以自己選定要用(estimate or importance or value) 其中一個欄位來做排序
	 * 
	 * @author cutecool
	 */
	private class StoryComparator implements Comparator<StoryObject> {
		public final static int TYPE_EST = 1;
		public final static int TYPE_IMP = 2;
		public final static int TYPE_VAL = 3;
		public final static int TYPE_ID = 4;
		private int mType = -1;

		public StoryComparator(int columnType) {
			mType = columnType;
		}

		@Override
		public int compare(StoryObject story1, StoryObject story2) {
			if (mType == TYPE_EST) {
				return story1.getEstimate() - story2.getEstimate();
			} else if (mType == TYPE_IMP) { // Importance from large to small
				return story2.getImportance() - story1.getImportance();
			} else if (mType == TYPE_VAL) {
				return story1.getValue() - story2.getValue();
			} else {
				return (int)(story1.getId() - story2.getId());
			}
		}
	}
}
