package ntut.csie.ezScrum.web.mapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.logic.SprintPlanLogic;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SprintBacklogMapper {
	private static Log log = LogFactory.getLog(SprintBacklogMapper.class);
	private int m_sprintPlanId = 0;
	private IProject m_project;
	private ISprintPlanDesc m_iterPlanDesc;
	private Date m_startDate;
	private Date m_endDate;
	private ITSServiceFactory m_itsFactory;
	private Configuration m_config;
	private double m_limitedPoint = 0;
	private IUserSession m_userSession;

	private ArrayList<IIssue> m_stories = null;
	private List<IIssue> m_tasks = null;
	private ArrayList<IIssue> m_dropedStories = null;
	private ArrayList<IIssue> all_issues = null;

	// 用於紀錄Story與Task之間的Mapping
	LinkedHashMap<Long, IIssue[]> map_story_tasks = null;
	LinkedHashMap<Long, IIssue[]> map_droped_story_tasks = null;
	// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	private boolean updateFlag = true;

	// private int m_interval = 0;
	// final private int[] StatusBoundary = { ITSEnum.ASSIGNED_STATUS, ITSEnum.CLOSED_STATUS };
	// final private long OneDay = ScrumEnum.DAY_MILLISECOND;

	/**
	 * 若沒有指定的話,自動取得目前的sprint#
	 * 
	 * @param project
	 * @param userSession
	 */
	public SprintBacklogMapper(IProject project, IUserSession userSession) {
		// m_project = project;
		//
		// SprintPlanHelper helper = new SprintPlanHelper(project);
		// m_userSession = userSession;
		// m_iterPlanDesc = helper.loadCurrentPlan();
		//
		// m_sprintPlanId = Integer.parseInt(m_iterPlanDesc.getID());
		//
		// init();

		m_project = project;
		m_userSession = userSession;

		SprintPlanLogic sprintPlanLogic = new SprintPlanLogic(project);
		m_iterPlanDesc = sprintPlanLogic.loadCurrentPlan();
		if (m_iterPlanDesc != null) {
			m_sprintPlanId = Integer.parseInt(m_iterPlanDesc.getID());
		}
		// m_sprintPlanId = Integer.parseInt(m_iterPlanDesc.getID());

		this.initSprintInformation();
		this.initITSInformation();
	}

	/**
	 * 取得指定的sprint backlog
	 * 
	 * @param project
	 * @param userSession
	 * @param sprintId
	 */
	public SprintBacklogMapper(IProject project, IUserSession userSession, int sprintId) {
		// m_project = project;
		//
		// SprintPlanMapper mapper = new SprintPlanMapper(project);
		//
		// m_iterPlanDesc = mapper.getSprintPlan(Integer.toString(sprintId));
		// m_sprintPlanId = Integer.parseInt(m_iterPlanDesc.getID());
		//
		// m_userSession = userSession;
		//
		// init();

		m_project = project;
		m_userSession = userSession;

		SprintPlanMapper mapper = new SprintPlanMapper(project);
		m_iterPlanDesc = mapper.getSprintPlan(Integer.toString(sprintId));
		m_sprintPlanId = Integer.parseInt(m_iterPlanDesc.getID());

		this.initSprintInformation();
		this.initITSInformation();
	}

	/**
	 * 初始化Sprint的資訊
	 */
	private void initSprintInformation() {
		try {
			// m_interval = Integer.parseInt(m_iterPlanDesc.getInterval()) * ScrumEnum.WEEK_DAY;

			m_startDate = DateUtil.dayFilter(m_iterPlanDesc.getStartDate());

			m_endDate = DateUtil.dayFilter(m_iterPlanDesc.getEndDate());

			String aDays = m_iterPlanDesc.getAvailableDays();
			// 將判斷 aDay:hours can commit 為 0 時, 計算 sprint 天數 * focus factor 的機制移除
			// 改為只計算 aDay:hours can commit * focus factor
			if (aDays != null && !aDays.equals("")) m_limitedPoint = Integer.parseInt(aDays) * Integer.parseInt(m_iterPlanDesc.getFocusFactor()) / 100;

			// // 初始ITS的設定
			// m_itsFactory = ITSServiceFactory.getInstance();
			// m_itsPrefs = new ITSPrefsStorage(m_project, m_userSession);
		} catch (NumberFormatException e) {
			log.info("non-exist sprint");
		}
		// }catch (Exception e) {
		// log.info("class: SprintBacklog, method : init, exception : " + e.toString());
		// }
	}

	/**
	 * 初始ITS的設定
	 */
	private void initITSInformation() {
		m_itsFactory = ITSServiceFactory.getInstance();
		m_config = new Configuration(m_userSession);
	}

	/**
	 * 測試用
	 */
	public void forceRefresh() {
		this.getAllIssuesInSprint();
		updateFlag = false;
	}

	/************************************************************
	 * =============== Sprint Backlog的操作 =========
	 *************************************************************/

	public Map<Long, IIssue[]> getTasksMap() {
		refresh();
		return map_story_tasks;
	}

	/**
	 * 如果沒有指定時間的話，預設就回傳目前最新的Task表給他
	 * 
	 * @return
	 */
	public List<IIssue> getTasks() {
		refresh();
		return m_tasks;
	}

	public Map<Long, IIssue[]> getDropedTaskMap() {
		if (map_droped_story_tasks == null) getDropedStory();
		return map_droped_story_tasks;
	}

	public List<IIssue> getIssues(String category) {
		refresh();

		List<IIssue> stories = new ArrayList<IIssue>();
		if (category.equals(ScrumEnum.STORY_ISSUE_TYPE)) {
			stories.addAll(m_stories);
		} else if (category.equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			stories.addAll(m_tasks);
		} else {
			stories.addAll(all_issues);
		}

		return stories;
	}

	/**
	 * 取得這個Sprint內stories
	 * 
	 * @param sprintID
	 * @return
	 */
	public IIssue[] getStoryInSprint(long sprintID) {
		// refresh();

		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		IIssue[] stories = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, Long.toString(sprintID), null);
		itsService.closeConnect();

		return stories;
	}

	/**
	 * 取得 story 內的 tasks 與 sprint 無關
	 * 
	 * @param storyID
	 */
	public IIssue[] getTaskInStory(long storyID) {
		// refresh();

		IIssue story = this.getIssue(storyID);
		if (story == null) return null;
		List<Long> taskIDs = story.getChildrenID();
		List<IIssue> tasks = new ArrayList<IIssue>();
		for (long taskID : taskIDs) {
			IIssue task = this.getIssue(taskID);
			if (task != null) tasks.add(task);
		}

		return tasks.toArray(new IIssue[tasks.size()]);
	}

	/**
	 * 取得在這個Sprint中曾經被Drop掉的Story
	 * 
	 * @return
	 */
	public IIssue[] getDropedStory() {
		if (m_dropedStories == null) m_dropedStories = new ArrayList<IIssue>();
		else return m_dropedStories.toArray(new IIssue[m_dropedStories.size()]);

		IITSService itsService = m_itsFactory.getService(m_config);
		String iter = Integer.toString(m_sprintPlanId);
		Date startDate = getSprintStartDate();
		Date endDate = getSprintEndDate();

		itsService.openConnect();

		// 找出這個Sprint期間，所有可能出現的 issue，下面再進行過濾
		IIssue[] tmpIIssues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "*", startDate, endDate);

		// 確認這些這期間被Drop掉的Story是否曾經有在此Sprint過
		if (tmpIIssues != null) {
			for (IIssue issue : tmpIIssues) {
				Map<Date, String> map = issue.getTagValueList("Iteration");

				if (!issue.getSprintID().equals(iter)) {
					m_dropedStories.add(issue);
				}
			}
		}

		// 取得這些被Dropped Story的Task
		map_droped_story_tasks = new LinkedHashMap<Long, IIssue[]>();
		ArrayList<IIssue> tmpList = new ArrayList<IIssue>();
		for (IIssue issue : m_dropedStories) {
			tmpList.clear();

			List<Long> childList = issue.getChildrenID();

			for (Long id : childList) {
				IIssue tmp = itsService.getIssue(id);
				if (tmp != null) tmpList.add(tmp);
			}
			map_droped_story_tasks.put(issue.getIssueID(), tmpList.toArray(new IIssue[tmpList.size()]));
		}

		itsService.closeConnect();
		return m_dropedStories.toArray(new IIssue[m_dropedStories.size()]);
	}

	public IIssue getIssue(long issueID) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		IIssue issue = itsService.getIssue(issueID);
		itsService.closeConnect();
		return issue;
	}

	public void modifyTaskInformation(long taskID, String Name, String handler, Date modifyDate) {
		IIssue task = this.getIssue(taskID);

		// taskID 為不存在的 issue 時，會有 null 的危險
		if (task != null) {
			if ((handler != null) && (handler.length() > 0) && (!task.getAssignto().equals(handler))) {
				IITSService itsService = m_itsFactory.getService(m_config);
				itsService.openConnect();
				itsService.updateHandler(task, handler, modifyDate);
				itsService.closeConnect();
			}
			if (!task.getSummary().equals(Name) && Name != null) {
				IITSService itsService = m_itsFactory.getService(m_config);
				itsService.openConnect();
				itsService.updateName(task, Name, modifyDate);
				itsService.closeConnect();
			}
		}
	}

	public void updateTagValue(IIssue issue) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.updateBugNote(issue);

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		updateFlag = true;

		itsService.closeConnect();
	}

	public long addTask(String name, String description, long storyID, Date date) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		IIssue task = new Issue();

		task.setProjectID(m_project.getName());
		task.setSummary(name);
		task.setDescription(description);
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);

		task = this.getIssue(itsService.newIssue(task));

		// 新增關係
		itsService.addRelationship(storyID, task.getIssueID(), ITSEnum.PARENT_RELATIONSHIP, date);

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		updateFlag = true;
		itsService.closeConnect();
		return task.getIssueID();
	}

	public void addExistedTask(long[] taskIDs, long storyID, Date date) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		// 新增關係
		for (long taskID : taskIDs)
			itsService.addRelationship(storyID, taskID, ITSEnum.PARENT_RELATIONSHIP, date);

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		updateFlag = true;
		itsService.closeConnect();
	}

	public void deleteExistedTask(long[] taskIDs) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		for (long taskID : taskIDs)
			itsService.deleteTask(taskID);
		updateFlag = true;
		itsService.closeConnect();
	}

	public void removeTask(long taskID, long parentID) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.removeRelationship(parentID, taskID, ITSEnum.PARENT_RELATIONSHIP);

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		updateFlag = true;
		itsService.closeConnect();
	}

	public void updateHistoryModifiedDate(long issueID, long historyID, Date date) {
		IIssue issue = this.getIssue(issueID);
		for (IIssueHistory history : issue.getIssueHistories()) {
			if (history.getHistoryID() == historyID) {
				String current = DateUtil.format(new Date(history.getModifyDate()), DateUtil._16DIGIT_DATE_TIME_MYSQL);
				String modify = DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_MYSQL);
				if (current.equals(modify)) break;
				IITSService itsService = m_itsFactory.getService(m_config);
				itsService.openConnect();
				itsService.updateHistoryModifiedDate(issueID, historyID, date);
				itsService.closeConnect();
				this.updateFlag = true;
				break;

			}
		}
	}

	/************************************************************
	 * 
	 * ================= 取得Iteration的描述 ===============
	 * 
	 *************************************************************/

	public int getSprintPlanId() {
		return m_sprintPlanId;
	}

	public IProject getProject() {
		return m_project;
	}

	public Date getSprintStartDate() {
		return m_startDate;
	}

	public Date getSprintEndDate() {
		return m_endDate;
	}

	public double getLimitedPoint() {
		return m_limitedPoint;
	}

	public String getSprintGoal() {
		return m_iterPlanDesc.getGoal();
	}

	/************************************************************
	 * 
	 * ================= TaskBoard 中有關於 task 操作 ===============
	 * 
	 *************************************************************/

	public void doneIssue(long id, String bugNote, String changeDate) {
		Date closeDate = null;
		if (changeDate != null && !changeDate.equals("")) closeDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.changeStatusToClosed(id, ITSEnum.FIXED_RESOLUTION, bugNote, closeDate);
		itsService.closeConnect();
	}

	public void reopenIssue(long id, String name, String bugNote, String changeDate) {
		Date reopenDate = null;
		if (changeDate != null && !changeDate.equals("")) reopenDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);

		IIssue issue = getIssue(id);
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		if (issue.getCategory().equals(ScrumEnum.STORY_ISSUE_TYPE)) itsService.resetStatusToNew(id, name, bugNote, reopenDate);
		else if (issue.getCategory().equals(ScrumEnum.TASK_ISSUE_TYPE)) itsService.reopenStatusToAssigned(id, name, bugNote, reopenDate);
		itsService.closeConnect();
	}

	public void resetTask(long id, String name, String bugNote, String changeDate) {
		Date reopenDate = null;
		if (changeDate != null && !changeDate.equals("")) reopenDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);

		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.resetStatusToNew(id, name, bugNote, reopenDate);
		itsService.closeConnect();
	}

	public void checkOutTask(long id, String bugNote) {
		if (bugNote != null && !bugNote.equals("")) {
			IITSService itsService = m_itsFactory.getService(m_config);
			itsService.openConnect();
			itsService.insertBugNote(id, bugNote);
			itsService.closeConnect();
		}
	}

	public void deleteTask(long taskID, long parentID) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.deleteRelationship(parentID, taskID);
		itsService.deleteTask(taskID);
		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		updateFlag = true;
		itsService.closeConnect();
	}

	/************************************************************
	 * private methods
	 *************************************************************/

	/**
	 * Refresh 動作
	 */
	private void refresh() {
		if (m_stories == null || m_tasks == null || all_issues == null || map_story_tasks == null || updateFlag) {
			// this.clearAllIssues();
			this.getAllIssuesInSprint();
			updateFlag = false;
		}
	}

	// private void clearAllIssues(){
	// if (m_stories == null || m_tasks == null || all_issues == null || map_story_tasks == null) {
	// m_stories = new ArrayList<IIssue>();
	// m_tasks = new ArrayList<IIssue>();
	// all_issues = new ArrayList<IIssue>();
	// map_story_tasks = new LinkedHashMap<Long, IIssue[]>();
	// } else {
	// m_stories.clear();
	// m_tasks.clear();
	// all_issues.clear();
	// map_story_tasks.clear();
	// }
	// }

	/**
	 * 取得目前所有在此Sprint的Story與Task
	 * 
	 * @return
	 */
	private IIssue[] getAllIssuesInSprint() {
		if (m_stories == null || m_tasks == null || all_issues == null || map_story_tasks == null) {
			m_stories = new ArrayList<IIssue>();
			m_tasks = new ArrayList<IIssue>();
			all_issues = new ArrayList<IIssue>();
			map_story_tasks = new LinkedHashMap<Long, IIssue[]>();
		} else {
			m_stories.clear();
			m_tasks.clear();
			all_issues.clear();
			map_story_tasks.clear();
		}

		IIssue[] issues = this.getStoryInSprint(m_sprintPlanId);

		for (IIssue issue : issues) {
			m_stories.add(issue);
			IIssue[] taskList = this.getTaskInStory(issue.getIssueID());
			if (taskList.length != 0) {
				for (IIssue task : taskList) {
					m_tasks.add(task);
				}
				map_story_tasks.put(issue.getIssueID(), taskList);
			}
		}
		updateFlag = false;

		all_issues.addAll(m_stories);
		all_issues.addAll(m_tasks);
		return all_issues.toArray(new IIssue[all_issues.size()]);
	}

	// /**
	// * 取得目前所有在此Sprint的Story與Task
	// * @return
	// */
	// private IIssue[] getAllIssuesInSprint() {
	// IIssue[] issues = null;
	// if (m_stories == null || m_tasks == null || all_issues == null || map_story_tasks == null) {
	// m_stories = new ArrayList<IIssue>();
	// m_tasks = new ArrayList<IIssue>();
	// all_issues = new ArrayList<IIssue>();
	// map_story_tasks = new LinkedHashMap<Long, IIssue[]>();
	// } else {
	// m_stories.clear();
	// m_tasks.clear();
	// all_issues.clear();
	// map_story_tasks.clear();
	// }
	//
	// IIssue[] issues = null;
	//
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	//
	// // 取出所有在此Sprint的Story，因為有限制SprintID，但只有Story有SprintID所以取不到Task
	// issues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, Integer.toString(m_sprintPlanId), null);
	// /*
	// * 檢查每個取出來的Story看他是否有關聯Issue的ID，並且依照此ID去取得相關得Issue
	// * 並且將Story與Task都存入List好回傳
	// */
	//
	// // 暫存的List，用於紀錄此Story底下有哪些Tasks
	// List<IIssue> tmpList = new ArrayList<IIssue>();
	// for (IIssue issue : issues) {
	// tmpList.clear();
	//
	// List<Long> childID = issue.getChildrenID();
	// for (Long id : childID) {
	//
	// IIssue task = itsService.getIssue(id);
	//
	// if (task == null) {
	// continue;
	// }
	// m_tasks.add(task);
	//
	// // 將此Task存入暫存的List，以便之後轉成Array
	// tmpList.add(task);
	// }
	//
	// m_stories.add(issue);
	//
	// if (!tmpList.isEmpty()) {
	// map_story_tasks.put(issue.getIssueID(), tmpList.toArray(new IIssue[tmpList.size()]));
	// }
	// }
	//
	// itsService.closeConnect();
	// updateFlag = false;
	// all_issues.addAll(m_stories);
	// all_issues.addAll(m_tasks);
	// return all_issues.toArray(new IIssue[all_issues.size()]);
	// }

	// /************************************************************
	// * 針對某個時間，取出當時Task的狀態
	// *************************************************************/
	// public IIssue[] getTasks(Date date) {
	// refresh();
	// MultiValueMap map = (MultiValueMap) getIssuesbyDate(date);
	// Collection<IIssue> coll = map.getCollection(ScrumEnum.TASK_ISSUE_TYPE);
	// coll = sortByImp(coll);
	// return coll.toArray(new IIssue[coll.size()]);
	// }
	//
	// public IIssue[] getStories(Date date) {
	// refresh();
	// MultiValueMap map = (MultiValueMap) getIssuesbyDate(date);
	// Collection<IIssue> coll = map.getCollection(ScrumEnum.STORY_ISSUE_TYPE);
	// coll = sortByImp(coll);
	// return coll.toArray(new IIssue[coll.size()]);
	// }
	//
	// public IIssue getStory(long id) {
	// refresh();
	// IIssue[] storiess = getStories();
	// for (IIssue story : storiess) {
	// if (story.getIssueID() == id)
	// return story;
	// }
	// return null;
	// }
	//
	// /**
	// * 應急處理，只有這個取得Issue歷史資料需要去觀察所有的Story 好麻煩喔，不管了啦By Date的話就Story跟Task都通吃了八
	// *
	// * @param category
	// * @param date
	// * @return
	// */
	// private MultiMap getIssuesbyDate(Date date) {
	//
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// String iter = Integer.toString(m_iter);
	// // 如果時間大於最後結束的時間，那麼就將結束時間設定為Sprint的結束時間
	// if (date.getTime() > getIterEndDate().getTime())
	// date = getIterEndDate();
	// MultiMap map = new MultiValueMap();
	//
	// /************************************************************
	// * 從DataBase中取得當天的Story
	// *************************************************************/
	// itsService.openConnect();
	// IIssue[] tmpIssues = itsService.getIssues(m_project.getName(), null,
	// null, iter, date);
	//
	// for (IIssue issue : tmpIssues)
	// map.put(ScrumEnum.STORY_ISSUE_TYPE, issue);
	//
	// /************************************************************
	// * 針對現在取出的這先Story找出其下的Task
	// *************************************************************/
	//
	// for (IIssue issue : tmpIssues) {
	// // 取出此Story所有的Task
	// List<Long> childID = issue.getChildrenID();
	// for (Long id : childID) {
	// IIssue tmp = itsService.getIssue(id);
	// if (tmp != null) {
	// map.put(ScrumEnum.TASK_ISSUE_TYPE, tmp);
	// }
	// }
	// }
	//
	// return map;
	// }
	//
	// // 根據種類來取得符合的issue
	// private IIssue[] getIssues(String category, Date date) {
	// refresh();
	//
	// /*
	// * 如果不是要Story也不是要Task就將Story與Task一起回傳
	// */
	// if (!category.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)
	// && !category.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
	//
	// return all_issues.toArray(new IIssue[all_issues.size()]);
	// }
	//
	// /*
	// * 只回傳Story
	// */
	// List<IIssue> storyList = new ArrayList<IIssue>();
	//
	// // 用來呈現當時Sprint中的Story,即使沒做完,在下次被分到其它的Sprint
	// if (date.getTime() > getIterEndDate().getTime())
	// date = getIterEndDate();
	//
	// for (IIssue issue : m_stories) {
	// if (issue.getTagValue(ScrumEnum.SPRINT_ID, date) != null) {
	// storyList.add(issue);
	// }
	// }
	//
	// if (category.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
	// storyList = sort(storyList, ScrumEnum.IMPORTANCE);
	// return storyList.toArray(new IIssue[storyList.size()]);
	// }
	//
	// /*
	// * 只回傳Task
	// */
	// // 當然重覆時,希望只存放一筆資料,因此利用set
	// ArrayList<IIssue> taskList = new ArrayList<IIssue>();
	// HashSet<Long> taskKeySet = new HashSet<Long>();
	// for (IIssue story : storyList)
	// for (long childID : story.getChildrenID(date))
	// taskKeySet.add(childID);
	//
	// if (taskKeySet.size() > 0) {
	// for (IIssue issue : m_tasks) {
	// if (taskKeySet.contains(issue.getIssueID()))
	// taskList.add(issue);
	// }
	// }
	//
	// return taskList.toArray(new IIssue[taskList.size()]);
	// }
	//
	// public List<IIssue> getCheckedOutStories() {
	// IIssue[] issues = getStories();
	// ArrayList<IIssue> list = new ArrayList<IIssue>();
	//
	// for (IIssue issue : issues) {
	// int status = ITSEnum.getStatus(issue.getStatus());
	// if (status >= StatusBoundary[0] && status < StatusBoundary[1] && isSetupOnFirstDayOfIteration(issue))
	// list.add(issue);
	// }
	//
	// return list;
	// }
	//
	// public void remove(long id) {
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	// IIssue issue = itsService.getIssue(id);
	// Element history = new Element(ScrumEnum.HISTORY_TAG);
	// // history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
	// // IIssue.STORY_TYPE_HSITORY_VALUE);
	// history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
	// new Date(), DateUtil._16DIGIT_DATE_TIME_2));
	//
	// Element iter = new Element(ScrumEnum.SPRINT_ID);
	// iter.setText(Integer.toString(0));
	// history.addContent(iter);
	//
	// issue.addTagValue(history);
	//
	// itsService.updateBugNote(issue);
	//
	// // 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	// updateFlag = true;
	// itsService.closeConnect();
	// }
	//
	// public List<IIssue> getNotCheckedOutStories() {
	// IIssue[] issues = getStories();
	// ArrayList<IIssue> list = new ArrayList<IIssue>();
	//
	// for (IIssue issue : issues) {
	// int status = ITSEnum.getStatus(issue.getStatus());
	// if (status < StatusBoundary[0]
	// && isSetupOnFirstDayOfIteration(issue))
	// list.add(issue);
	// }
	//
	// return list;
	// }
	//
	// public List<IIssue> getDoneStories() {
	// IIssue[] issues = getStories();
	// ArrayList<IIssue> list = new ArrayList<IIssue>();
	//
	// for (IIssue issue : issues) {
	// int status = ITSEnum.getStatus(issue.getStatus());
	// if (status >= StatusBoundary[1]
	// && isSetupOnFirstDayOfIteration(issue))
	// list.add(issue);
	// }
	//
	// return list;
	// }
	//
	// // 用來判斷是否為unplanned的issue
	// private boolean isSetupOnFirstDayOfIteration(IIssue issue) {
	// // TODO:尚未實作,暫時先回傳true
	// return true;
	// }
	//
	// public double getCurrentPoint(long parentID) {
	// IIssue[] items = getTasks();
	// double point = 0;
	// for (IIssue item : items) {
	// if (item.getParentsID().contains(parentID)) {
	// try {
	// point += Double.parseDouble(item
	// .getTagValue(ScrumEnum.ESTIMATION));
	// } catch (Exception e) {
	// continue;
	// }
	// }
	// }
	// return point;
	//
	// }
	//
	// public int getIterInterval() {
	// return m_interval;
	// }
	//
	// public void modifyTagValue(List<IIssue> list) {
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	// for (IIssue issue : list) {
	// itsService.updateBugNote(issue);
	// }
	//
	// // 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	// updateFlag = true;
	// itsService.closeConnect();
	// }
	//
	// move to MantisAccountMapper.java
	// public List<String> getActorList() {
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	// String[] actors = itsService.getActors(this.m_project.getName(),
	// ITSEnum.UPDATER_ACCESS_LEVEL);
	//
	// List<String> list = new ArrayList<String>();
	// MantisAccountMapper helper = new MantisAccountMapper(this.m_project, this.m_userSession);
	//
	// list.add("");
	// for (String actor : actors) {
	// if (actor.equalsIgnoreCase("administrator")
	// || actor.equalsIgnoreCase("admin"))
	// continue;
	// if (helper.existUser(actor)) {
	// list.add(actor);
	// }
	// }
	// itsService.closeConnect();
	// return list;
	// }
	//
	// public IIssue getTaskById(long id) {
	// // refresh();
	// List<IIssue> tasks = this.getTasks();
	// for (IIssue task : tasks) {
	// if (task.getIssueID() == id)
	// return task;
	// }
	// return null;
	// }

	// public Date getSprintStartWorkDate() {
	// Date workDate = DateUtil.nearWorkDate(m_startDate, DateUtil.BACK_DIRECTION);
	// if (workDate.getTime() > m_endDate.getTime())
	// return m_startDate;
	// return workDate;
	// }
	//
	// public Date getSprintEndWorkDate() {
	// Date workDate = DateUtil.nearWorkDate(m_endDate, DateUtil.FRONT_DIRECTION);
	// if (workDate.getTime() < m_startDate.getTime())
	// return m_endDate;
	// return workDate;
	// }
	//
	// public int getSprintWorkDays() {
	// // 扣除假日後，Sprint的總天數
	// int dayOfSprint = -1;
	//
	// Calendar indexDate = Calendar.getInstance();
	// indexDate.setTime(this.getSprintStartWorkDate());
	// long endTime = this.getSprintEndWorkDate().getTime();
	//
	// while (!(indexDate.getTimeInMillis() > endTime)) {
	// // 扣除假日
	// if (!DateUtil.isHoliday(indexDate.getTime())) {
	// dayOfSprint++;
	// }
	// indexDate.add(Calendar.DATE, 1);
	// }
	//
	// return dayOfSprint;
	// }
	//
	// public boolean isOutOfSprint() {
	// // 在當天的晚上11:59:59仍是當天
	// return (new Date().getTime() > (getSprintEndWorkDate().getTime() + OneDay - 1));
	// }
	//
	// public double getCurrentPoint(String type) {
	// List<IIssue> items;
	// double point = 0;
	// if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
	// items = this.getTasks();
	// for (IIssue item : items) {
	// point += Double.parseDouble(item.getEstimated());
	// }
	// } else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
	// items = this.getStories();
	// for (IIssue item : items) {
	// point += Double.parseDouble(item.getEstimated());
	// }
	// } else
	// return 0;
	//
	// return point;
	// }
	//
	// public double getCurrentUnclosePoint(String type) {
	// List<IIssue> items;
	// double point = 0;
	// if (type.equalsIgnoreCase(ScrumEnum.TASK_ISSUE_TYPE)) {
	// items = this.getTasks();
	// for (IIssue item : items) {
	// if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS)
	// continue;
	// point += Double.parseDouble(item.getRemains());
	// }
	// } else if (type.equalsIgnoreCase(ScrumEnum.STORY_ISSUE_TYPE)) {
	// items = this.getStories();
	// for (IIssue item : items) {
	// if (ITSEnum.getStatus(item.getStatus()) >= ITSEnum.CLOSED_STATUS)
	// continue;
	// point += Double.parseDouble(item.getEstimated());
	// }
	//
	// } else
	// return 0;
	//
	// return point;
	// }
	//
	// /**
	// * 根據tag name的值來排序
	// * @param list
	// * @param tagName
	// * @return
	// */
	// private List<IIssue> sort(Collection<IIssue> list, String tagName) {
	// ArrayList<IIssue> sortedList = new ArrayList<IIssue>();
	// for (IIssue issue : list) {
	// int index = 0;
	// int valueSource = 0;
	//
	// if (issue.getTagValue(tagName) != null)
	// valueSource = Integer.parseInt(issue.getTagValue(tagName));
	//
	// for (IIssue sortedIssue : sortedList) {
	// int valueTarget = 0;
	// if (sortedIssue.getTagValue(tagName) != null)
	// valueTarget = Integer.parseInt(sortedIssue.getTagValue(tagName));
	// if (valueSource > valueTarget)
	// break;
	// index++;
	// }
	// sortedList.add(index, issue);
	// }
	//
	// return sortedList;
	// }
	//
	// /**
	// * 根據 importance 的值來排序
	// * @param list
	// * @return
	// */
	// private List<IIssue> sortByImp(Collection<IIssue> list) {
	// List<IIssue> sortedList = new ArrayList<IIssue>();
	//
	// for (IIssue issue : list) {
	// int index = 0;
	// for (index = 0; index < sortedList.size(); index++) {
	// if (Integer.parseInt(issue.getImportance()) > Integer.parseInt(sortedList.get(index).getImportance())) {
	// break;
	// }
	// }
	// sortedList.add(index, issue);
	// }
	//
	// return sortedList;
	// }
	// public List<IIssue> getStories() {
	// // refresh();
	// return this.getIssues(ScrumEnum.STORY_ISSUE_TYPE, "null");
	// }
	//
	// public List<IIssue> getStoriesByImp() {
	// // refresh();
	// return this.getIssues(ScrumEnum.STORY_ISSUE_TYPE, "Importance");
	// }
	//
	// public List<IIssue> getIssues(String category, String type) {
	// refresh();
	//
	// List<IIssue> stories = new ArrayList<IIssue>();
	// if (category.equals(ScrumEnum.STORY_ISSUE_TYPE)) {
	// stories.addAll(m_stories);
	// } else if (category.equals(ScrumEnum.TASK_ISSUE_TYPE)) {
	// stories.addAll(m_tasks);
	// } else {
	// stories.addAll(all_issues);
	// }
	//
	// List<IIssue> sortList = new ArrayList<IIssue>();
	// if (type.equals("Importance")) { // sort by importance
	// sortList = this.sortByImp(stories);
	// } else { // never sort
	// sortList = this.sort(stories, ScrumEnum.STORY_ISSUE_TYPE);
	// }
	//
	// return sortList;
	// // return sortList.toArray(new IIssue[sortList.size()]);
	// }
	//
	// /**
	// * 取得指定的 sprint backlog
	// * @param project
	// * @param userSession
	// * @param sprint
	// */
	// public SprintBacklogMapper(IProject project, IUserSession userSession, ISprintPlanDesc sprint) {
	// m_project = project;
	//
	// m_iterPlanDesc = sprint;
	// if (m_iterPlanDesc != null)
	// m_sprintPlanId = Integer.parseInt(m_iterPlanDesc.getID());
	//
	// m_userSession = userSession;
	//
	// init();
	// }
	//
	// public boolean editTask(long taskID, String Name, String estimation,
	// String remains, String handler, String partners, String actualHour,
	// String notes, Date modifyDate) {
	// // 先變更handler
	// this.modifyTaskInformation(taskID, Name, handler, modifyDate);
	//
	// // 建立tag (這邊的tag是指Mantis紀錄Note的地方）
	// IIssue task = this.getIssue(taskID);
	// if (task == null) {
	// return false;
	// }
	//
	// Element history = new Element(ScrumEnum.HISTORY_TAG);
	// // history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
	// // IIssue.STORY_TYPE_HSITORY_VALUE);
	// history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
	// (modifyDate == null ? new Date() : modifyDate),
	// DateUtil._16DIGIT_DATE_TIME_2));
	//
	// if (estimation != null && !estimation.equals("")) {
	// if (!task.getEstimated().equals(estimation)) {
	// Element storyPoint = new Element(ScrumEnum.ESTIMATION);
	// storyPoint.setText(estimation);
	// history.addContent(storyPoint);
	// }
	// }
	// if (remains != null && !remains.equals("")) {
	// if (!task.getRemains().equals(remains)) {
	// Element remainingPoints = new Element(ScrumEnum.REMAINS);
	// remainingPoints.setText(remains);
	// history.addContent(remainingPoints);
	// }
	// }
	// if (!task.getPartners().equals(partners)) {
	// Element element = new Element(ScrumEnum.PARTNERS);
	// element.setText(partners.replaceAll("'", "''"));
	// history.addContent(element);
	// }
	// if (notes != null) {
	// if (!task.getNotes().equals(notes)) {
	// Element element = new Element(ScrumEnum.NOTES);
	// element.setText(notes.replaceAll("'", "''"));
	// history.addContent(element);
	// }
	// }
	// if (actualHour != null && !actualHour.equals("")) {
	// if (!task.getActualHour().equals(actualHour)) {
	// Element element = new Element(ScrumEnum.ACTUALHOUR);
	// element.setText(actualHour);
	// history.addContent(element);
	// }
	// }
	//
	// if (history.getChildren().size() > 0) {
	// task.addTagValue(history);
	// // 最後將修改的結果更新至DB
	// this.updateTagValue(task);
	//
	// // 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	// updateFlag = true;
	//
	// return true;
	// }
	// return false;
	// }
	//
	// public void checkOutTask(long id, String name, String handler, String partners, String bugNote, String changeDate) {
	// Date closeDate = null;
	// if (changeDate != null && !changeDate.equals(""))
	// closeDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);
	// // checkoutTask 時,actual hour 的值必為0
	// this.editTask(id, name, null, null, handler, partners, "0", bugNote, closeDate);
	//
	// if (bugNote != null && !bugNote.equals("")) {
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	// itsService.insertBugNote(id, bugNote);
	// itsService.closeConnect();
	// }
	// }
	//
	// public void doneIssue(long id, String name, String bugNote, String changeDate, String actualHour) {
	// this.doneTask(id, name, bugNote, changeDate, actualHour);
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// Date closeDate = null;
	// if (changeDate != null && !changeDate.equals(""))
	// closeDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	// itsService.changeStatusToClosed(id, ITSEnum.FIXED_RESOLUTION, bugNote, closeDate);
	// itsService.closeConnect();
	// }
	//
	// private void doneTask(long id, String name, String bugNote, String changeDate, String actualHour) {
	// IIssue task = this.getIssue(id);
	// Date closeDate = null;
	// if (changeDate != null && !changeDate.equals("")) {
	// closeDate = DateUtil.dayFillter(changeDate, DateUtil._16DIGIT_DATE_TIME);
	// }
	// // 如果issue的type為Task時則將Remians設定為空值，否則reopen時由於Remains為0
	// // 圖表將不會有任何變動
	// if (task.getCategory().equals(ScrumEnum.TASK_ISSUE_TYPE)) {
	// this.editTask(id, name, null, "0", task.getAssignto(), task.getPartners(), actualHour, bugNote, closeDate);
	// } else {
	// this.editTask(id, name, null, null, task.getAssignto(), task.getPartners(), actualHour, bugNote, closeDate);
	// }
	// }
	//
	// public long addTask(String name, String description, String estimation,
	// String handler, String partners, String notes, long storyID,
	// Date date) {
	// IITSService itsService = m_itsFactory.getService(m_itsPrefs);
	// itsService.openConnect();
	// IIssue task = new Issue();
	//
	// task.setProjectID(m_project.getName());
	// task.setSummary(name);
	// task.setDescription(description);
	// task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
	//
	// task = this.getIssue(itsService.newIssue(task));
	// String actualHour = "0";
	// // 增加estimation的tag
	// // if (estimation != null && !estimation.equals("")) {
	// // Element history = new Element(IIssue.HISTORY_TAG);
	// // history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
	// // IIssue.STORY_TYPE_HSITORY_VALUE);
	// // history.setAttribute(IIssue.ID_HISTORY_ATTR, DateUtil.format(
	// // new Date(), DateUtil._16DIGIT_DATE_TIME_2));
	// //
	// // Element estElem = new Element(IIssue.ESTIMATION);
	// // estElem.setText(estimation);
	// // history.addContent(estElem);
	// //
	// // task.addTagValue(history);
	// //
	// // itsService.updateBugNote(task);
	// // }
	//
	// // 利用edit來增加estimation的tag
	// // 剛新增Task時Remaining = estimation
	// this.editTask(task.getIssueID(), name, estimation, estimation, handler, partners, actualHour, notes, date);
	//
	// // 新增關係
	// itsService.addRelationship(storyID, task.getIssueID(), ITSEnum.PARENT_RELATIONSHIP, date);
	//
	// // 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	// updateFlag = true;
	// itsService.closeConnect();
	// return task.getIssueID();
	// }
}
