package ntut.csie.ezScrum.web.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class SprintBacklogLogic {

	private IProject project;
	private IUserSession userSession;
	private SprintBacklogMapper sprintBacklogMapper;

	// 儲存目前處理過的 Sprint Date Column
	private List<SprintBacklogDateColumn> currentCols = null;
	private ArrayList<Date> dateList = null;

	public SprintBacklogLogic() {}

	public SprintBacklogLogic(IProject project, IUserSession userSession, String sprintID) {
		this.project = project;
		this.userSession = userSession;
		this.sprintBacklogMapper = this.createSprintBacklogMapper(sprintID);
	}

	public SprintBacklogMapper getSprintBacklogMapper() {
		return this.sprintBacklogMapper;
	}

	/**
	 * 判斷使用者輸入的sprintID是否為合法
	 * 
	 * @param mProject
	 * @param userSession
	 * @param sprintId
	 * @return
	 */
	private SprintBacklogMapper createSprintBacklogMapper(String sprintId) {
		SprintBacklogMapper sprintBacklogMapper = null;

		try {
			// sprint 不存在，回傳最近的一個 sprint 或 空的 sprint
			if (sprintId == null || sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1")) {
				sprintBacklogMapper = new SprintBacklogMapper(this.project, this.userSession);
			} else {
				sprintBacklogMapper = new SprintBacklogMapper(this.project, this.userSession, Long.parseLong(sprintId));
			}
		} catch (Exception e) {
			// 已經處理過不必輸出 Exception
			sprintBacklogMapper = null;
		}
		return sprintBacklogMapper;
	}

	public long addTask(String name, String description, String estimate,
	        String handler, String partners, String notes, long storyID,
	        Date date) {

		long taskID = this.sprintBacklogMapper.addTask(name, description, storyID, date);

		// 利用edit來增加estimation的tag
		// 剛新增Task時Remaining = estimation
		String actualHour = "0";
		this.editTask(taskID, name, estimate, estimate, handler, partners, actualHour, notes, date);

		return taskID;
	}

	public boolean editTask(long taskId, String name, String estimate,
	        String remains, String handler, String partners, String actualHour,
	        String notes, Date modifyDate) {
		// 先變更handler
		this.sprintBacklogMapper.modifyTaskInformation(taskId, name, handler, modifyDate);

		// 建立tag (這邊的tag是指Mantis紀錄Note的地方）
		IIssue task = this.sprintBacklogMapper.getIssue(taskId);
		if (task == null) {
			return false;
		}

		Element history = new Element(ScrumEnum.HISTORY_TAG);
		// history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
		// IIssue.STORY_TYPE_HSITORY_VALUE);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
		        (modifyDate == null ? new Date() : modifyDate),
		        DateUtil._16DIGIT_DATE_TIME_2));

		if (estimate != null && !estimate.equals("")) {
			if (!task.getEstimated().equals(estimate)) {
				Element storyPoint = new Element(ScrumEnum.ESTIMATION);
				storyPoint.setText(estimate);
				history.addContent(storyPoint);
				
				HistoryDAO.getInstance().add(new HistoryObject(
						taskId,
						IssueTypeEnum.TYPE_TASK,
						HistoryObject.TYPE_ESTIMATE,
						String.valueOf(task.getEstimated()),
						String.valueOf(estimate),
						System.currentTimeMillis()));
			}
		}
		if (remains != null && !remains.equals("")) {
			if (!task.getRemains().equals(remains)) {
				Element remainingPoints = new Element(ScrumEnum.REMAINS);
				remainingPoints.setText(remains);
				history.addContent(remainingPoints);
				
				HistoryDAO.getInstance().add(new HistoryObject(
						taskId,
						IssueTypeEnum.TYPE_TASK,
						HistoryObject.TYPE_REMAIMS,
						String.valueOf(task.getRemains()),
						String.valueOf(remains),
						System.currentTimeMillis()));
			}
		}
		if (!task.getPartners().equals(partners)) {
			Element element = new Element(ScrumEnum.PARTNERS);
			element.setText(partners.replaceAll("'", "''"));
			history.addContent(element);
			
			HistoryDAO.getInstance().add(new HistoryObject(
					taskId,
					IssueTypeEnum.TYPE_TASK,
					HistoryObject.TYPE_PARTNERS,
					String.valueOf(task.getPartners()),
					String.valueOf(partners),
					System.currentTimeMillis()));
		}
		if (notes != null) {
			if (!task.getNotes().equals(notes)) {
				Element element = new Element(ScrumEnum.NOTES);
				element.setText(notes.replaceAll("'", "''"));
				history.addContent(element);
				
				HistoryDAO.getInstance().add(new HistoryObject(
						taskId,
						IssueTypeEnum.TYPE_TASK,
						HistoryObject.TYPE_NOTE,
						String.valueOf(task.getNotes()),
						String.valueOf(notes),
						System.currentTimeMillis()));
			}
		}
		if (actualHour != null && !actualHour.equals("")) {
			if (!task.getActualHour().equals(actualHour)) {
				Element element = new Element(ScrumEnum.ACTUALHOUR);
				element.setText(actualHour);
				history.addContent(element);
				
				HistoryDAO.getInstance().add(new HistoryObject(
						taskId,
						IssueTypeEnum.TYPE_TASK,
						HistoryObject.TYPE_ACTUAL,
						String.valueOf(task.getActualHour()),
						String.valueOf(actualHour),
						System.currentTimeMillis()));
			}
		}

		if (history.getChildren().size() > 0) {
			task.addTagValue(history);
			// 最後將修改的結果更新至DB
			this.sprintBacklogMapper.updateTagValue(task);

			return true;
		}
		return false;
	}

	public void checkOutTask(long id, String name, String handler, String partners, String bugNote, String changeDate) {
		Date closeDate = null;
		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);
		}
		
		this.sprintBacklogMapper.checkOutTask(id, bugNote);
		// checkoutTask 時,actual hour 的值必為0
		this.editTask(id, name, null, null, handler, partners, "0", bugNote, closeDate);
	}

	public void doneIssue(long id, String name, String bugNote, String changeDate, String actualHour) {
		IIssue task = this.sprintBacklogMapper.getIssue(id);
		Date closeDate = null;
		if (changeDate != null && !changeDate.equals("")) {
			closeDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);
		}
		// 如果issue的type為Task時則將Remians設定為空值，否則reopen時由於Remains為0
		// 圖表將不會有任何變動
		if (task.getCategory().equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			this.editTask(id, name, null, "0", task.getAssignto(), task.getPartners(), actualHour, bugNote, closeDate);
		} else {
			this.editTask(id, name, null, null, task.getAssignto(), task.getPartners(), actualHour, bugNote, closeDate);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.sprintBacklogMapper.doneIssue(id, bugNote, changeDate);
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
			ISprintPlanDesc desc = (new SprintPlanMapper(this.project)).getSprintPlan(Integer.toString(backlog.getSprintPlanId()));
			availableDays = Integer.parseInt(desc.getInterval()) * 5;		// 一個禮拜五天
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
	public List<SprintBacklogDateColumn> calculateSprintBacklogDateList(Date startDate, int availableDays) {
		if (startDate == null) return new ArrayList<SprintBacklogDateColumn>();

		List<SprintBacklogDateColumn> cols = new ArrayList<SprintBacklogDateColumn>();
		ArrayList<Date> dates = new ArrayList<Date>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);		// 設定為現在時間

		String ID_Date = "Date_";
		int count = 1;
		while (availableDays-- > 0) {
			while (DateUtil.isHoliday(cal.getTime())) {	// 判斷假日
				cal.add(Calendar.DATE, 1);		// 跳過此一工作天
			}

			SimpleDateFormat format = new SimpleDateFormat("MM/dd");
			String date = format.format(cal.getTime());

			String ID = ID_Date + Integer.toString(count++);
			cols.add(new SprintBacklogDateColumn(ID, date));	// 將可工作的日期加入 list

			dates.add(cal.getTime());
			cal.add(Calendar.DATE, 1);			// 加一工作天
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
		Date m_startDate = this.sprintBacklogMapper.getSprintStartDate();
		Date m_endDate = this.sprintBacklogMapper.getSprintEndDate();
		Date workDate = DateUtil.nearWorkDate(m_startDate, DateUtil.BACK_DIRECTION);
		if (workDate.getTime() > m_endDate.getTime()) return m_startDate;
		return workDate;
	}

	/**
	 * 取得該sprint結束工作的日期
	 * 
	 * @return
	 */
	public Date getSprintEndWorkDate() {
		Date m_startDate = this.sprintBacklogMapper.getSprintStartDate();
		Date m_endDate = this.sprintBacklogMapper.getSprintEndDate();
		Date workDate = DateUtil.nearWorkDate(m_endDate, DateUtil.FRONT_DIRECTION);
		if (workDate.getTime() < m_startDate.getTime()) return m_endDate;
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
		return (new Date().getTime() > (this.getSprintEndWorkDate().getTime() + OneDay - 1));
	}

	public double getCurrentPoint(String type) {
		List<IIssue> items;
		double point = 0;
		if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
			items = this.sprintBacklogMapper.getTasks();
			for (IIssue item : items) {
				point += Double.parseDouble(item.getEstimated());
			}
		} else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
			items = this.getStories();
			for (IIssue item : items) {
				point += Double.parseDouble(item.getEstimated());
			}
		} else return 0;

		return point;
	}

	public double getCurrentUnclosePoint(String type) {
		List<IIssue> items;
		double point = 0;
		if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
			items = this.sprintBacklogMapper.getTasks();
			for (IIssue item : items) {
				if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS) continue;
				point += Double.parseDouble(item.getRemains());
			}
		} else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
			items = this.getStories();
			for (IIssue item : items) {
				if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS) continue;
				point += Double.parseDouble(item.getEstimated());
			}

		} else return 0;

		return point;
	}

	public List<IIssue> getStories() {
		List<IIssue> stories = this.sprintBacklogMapper.getIssues(ScrumEnum.STORY_ISSUE_TYPE);
		return this.sort(stories, "null");
	}

	public List<IIssue> getStoriesByImp() {
		List<IIssue> stories = this.sprintBacklogMapper.getIssues(ScrumEnum.STORY_ISSUE_TYPE);
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

			if (issue.getTagValue(tagName) != null) valueSource = Integer.parseInt(issue.getTagValue(tagName));

			for (IIssue sortedIssue : sortedList) {
				int valueTarget = 0;
				if (sortedIssue.getTagValue(tagName) != null) valueTarget = Integer.parseInt(sortedIssue.getTagValue(tagName));
				if (valueSource > valueTarget) break;
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
				if (Integer.parseInt(issue.getImportance()) > Integer.parseInt(sortedList.get(index).getImportance())) {
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
		List<IIssue> tasks = this.sprintBacklogMapper.getTasks();
		for (IIssue task : tasks) {
			if (task.getIssueID() == id) return task;
		}
		return null;
	}
}
