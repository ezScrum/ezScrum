package ntut.csie.ezScrum.web.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import edu.emory.mathcs.backport.java.util.Collections;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintBacklogLogic {
	private ProjectObject mProject;
	private SprintBacklogMapper mSprintBacklogMapper;

	// 儲存目前處理過的 Sprint Date Column
	private ArrayList<SprintBacklogDateColumn> mCurrentCols = null;
	private ArrayList<Date> mDates = null;

	public SprintBacklogLogic(ProjectObject project) {
		mProject = project;
		mSprintBacklogMapper = createSprintBacklogMapper(-1);
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
	
	/**
	 * Taskboard story card 的操作
	 * 
	 * @param id
	 * @param name
	 * @param notes
	 * @param changeDate
	 */
	public void closeStory(long projectId, long serialId, String name, String notes,
			String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.closeStory(serialId, name, notes, closeDate);
	}
	
	public void reopenStory(long projectId, long id, String name, String notes,
			String changeDate) {
		Date reopenDate = parseToDate(changeDate);
		mSprintBacklogMapper.reopenStory(serialId, name, notes, reopenDate);
	}

	/**
	 * Taskboard task card 的操作
	 * 
	 * @param id
	 * @param name
	 * @param handlerUsername
	 * @param partners
	 * @param notes
	 * @param changeDate
	 */
	public void checkOutTask(long projectId, long serialId, String name, String handlerUsername,
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

		mSprintBacklogMapper.checkOutTask(projectId, serialId, name, handlerId, partnersId,
				notes, closeDate);
	}

	public void closeTask(long projectId, long serialId, String name, String notes, int actual,
			String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.closeTask(projectId, serialId, name, notes, actual, closeDate);
	}

	public void reopenTask(long projectId, long serialId, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.reopenTask(projectId, serialId, name, notes, closeDate);
	}

	public void resetTask(long projectId, long serialId, String name, String notes, String changeDate) {
		Date closeDate = parseToDate(changeDate);
		mSprintBacklogMapper.resetTask(serialId, name, notes, closeDate);
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
		SprintObject sprint = backlog.getSprint();
		if (sprint != null) {
			availableDays = sprint.getInterval() * 5; // 一個禮拜五天
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
	public ArrayList<SprintBacklogDateColumn> getSprintBacklogDates(
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
			dateColumns.add(new SprintBacklogDateColumn(dateId, date)); // 將可工作的日期加入 list
			dates.add(cal.getTime());
			cal.add(Calendar.DATE, 1); // 加一工作天
		}

		mDates = dates;
		mCurrentCols = dateColumns;

		return dateColumns;
	}

	public ArrayList<SprintBacklogDateColumn> getCurrentDateColumns() {
		return mCurrentCols;
	}

	public ArrayList<Date> getCurrentDateList() {
		return mDates;
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
		if (startDate == null || endDate == null) {
			return null;
		}
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
		if (startDate == null || endDate == null) {
			return null;
		}
		Date workDate = DateUtil.nearWorkDate(endDate,
				DateUtil.FRONT_DIRECTION);
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
		int dayOfSprint = 0;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getSprintStartWorkDate());
		long endTime = getSprintEndWorkDate().getTime();

		while (!(calendar.getTimeInMillis() > endTime)) {
			// 扣除假日
			if (!DateUtil.isHoliday(calendar.getTime())) {
				dayOfSprint++;
			}
			calendar.add(Calendar.DATE, 1);
		}
		return dayOfSprint;
	}

	public boolean isOutOfSprint() {
		long oneDay = ScrumEnum.DAY_MILLISECOND;
		// 在當天的晚上 11:59:59 仍是當天
		return (new Date().getTime() > (getSprintEndWorkDate().getTime()
				+ oneDay - 1));
	}

	public ArrayList<StoryObject> getStoriesSortedByIdInSprint() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getStoriesInSprint();
		return sort(stories, "");
	}

	public ArrayList<StoryObject> getStoriesSortedByImpInSprint() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getStoriesInSprint();
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
		public String Id;
		public String Name;

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
	
	public void addStoriesToSprint(ArrayList<Long> serialStoriesId, long sprintId) {
		for (long serialStoryId : serialStoriesId) {
			StoryObject story = StoryObject.get(mProject.getId(), serialStoryId);
			if (sprintId > 0 && story != null) {
				// Update the relation between story and sprint
				mSprintBacklogMapper.updateStoryRelation(serialStoryId, sprintId, story.getEstimate(), story.getImportance(), new Date());
			}
		}
	}
}
