package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.ScrumIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class UnplannedItemMapper {
	private boolean updateFlag = true;	// ?? 因使用暫存的方式來加速存取速度,所以當有變動時則需更新

	private IProject m_project;
	private ITSServiceFactory m_itsFactory;
	private Configuration m_config;
	private IUserSession m_userSession;

	public UnplannedItemMapper(IProject project, IUserSession userSession) {
		m_project = project;
		m_userSession = userSession;
		// 初始ITS的設定
		m_itsFactory = ITSServiceFactory.getInstance();
		m_config = new Configuration(m_userSession);
	}

	public IIssue getById(long id) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue issue = itsService.getIssue(id);
		itsService.closeConnect();

		if (issue.getCategory().equals(ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE)) return issue;

		return null;
	}

	public List<IIssue> getList(String sprintId) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE, null, sprintId, null);
		itsService.closeConnect();

		List<IIssue> list = new ArrayList<IIssue>();
		for (IIssue issue : issues) {
			list.add(new ScrumIssue(issue));
		}

		return list;
	}

	public List<IIssue> getAll() {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE);

		List<IIssue> list = new ArrayList<IIssue>();

		for (IIssue issue : issues) {
			list.add(new ScrumIssue(issue));
		}

		itsService.closeConnect();
		return list;
	}

	public long add(String name, String estimate,
	        String handler, String partners, String notes, Date date,
	        String unplanneditemIssueType, String SprintID) {

		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		IIssue unplannedItem = new Issue();

		unplannedItem.setProjectID(m_project.getName());
		unplannedItem.setSummary(name);
		unplannedItem.setCategory(unplanneditemIssueType);
		unplannedItem = getById(itsService.newIssue(unplannedItem));
		long id = unplannedItem.getIssueID();

		// 將此unPlannedItem的關係加入Story Relation Table
		itsService.updateStoryRelationTable(Long.toString(id), m_project.getName(), "-1", SprintID, null, null, date);

		String actualHour = "0";

		modifyHandler(id, handler, date);
		// 利用edit來增加tag
		this.editNote(id, estimate, partners, actualHour, notes, date, SprintID);

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		updateFlag = true;
		itsService.closeConnect();
		return id;
	}

	public void update(long issueID, String name,
	        String handler, String status, String partners, String estimated,
	        String actualHour, String notes, String sprintID, Date date) {
		modifyHandler(issueID, handler, null);

		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		IIssue issue = getById(issueID);
		itsService.openConnect();

		if (!issue.getSummary().equals(name)) itsService.updateName(issue, name, date);

		if (status.equals(ITSEnum.S_NEW_STATUS)) {
			itsService.resetStatusToNew(issueID, name, notes, date);
		} else if (status.equals(ITSEnum.S_ASSIGNED_STATUS)) {
			itsService.updateHandler(issue, handler, date);
		} else {
			itsService.changeStatusToClosed(issueID, ITSEnum.FIXED_RESOLUTION,
			        notes, date);
		}

		// 將此unPlannedItem的關係加入Story Relation Table
		itsService.updateStoryRelationTable(String.valueOf(issueID), m_project.getName(), null, sprintID, estimated, null, date);

		itsService.closeConnect();
		editNote(issueID, estimated, partners, actualHour, notes, date, sprintID);
	}

	public void delete(String issueID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.removeIssue(issueID);
		itsService.closeConnect();
	}

	/*
	 * 以下為私有函式
	 */

	private boolean editNote(long taskID, String estimate, String partners,
	        String actualHour, String notes, Date modifyDate, String sprintID) {
		// 建立tag
		IIssue task = getById(taskID);

		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
		        (modifyDate == null ? new Date() : modifyDate),
		        DateUtil._16DIGIT_DATE_TIME_2));

		if (estimate != null && !estimate.equals("")) {
			if (!task.getEstimated().equals(estimate)) {
				Element storyPoint = new Element(ScrumEnum.ESTIMATION);
				storyPoint.setText(estimate);
				history.addContent(storyPoint);
			}
		}

		if (!task.getPartners().equals(partners)) {
			Element element = new Element(ScrumEnum.PARTNERS);
			element.setText(partners);
			history.addContent(element);
		}
		if (notes != null) {
			if (!task.getNotes().equals(notes)) {
				Element element = new Element(ScrumEnum.NOTES);
				element.setText(notes);
				history.addContent(element);
			}
		}
		if (actualHour != null && !actualHour.equals("")) {
			if (!task.getActualHour().equals(actualHour)) {
				Element element = new Element(ScrumEnum.ACTUALHOUR);
				element.setText(actualHour);
				history.addContent(element);
			}
		}

		if (sprintID != null && !sprintID.equals("")
		        && Integer.parseInt(sprintID) >= 0) {

			Element element = new Element(ScrumEnum.SPRINT_ID);
			element.setText(sprintID);
			history.addContent(element);
		}

		if (history.getChildren().size() > 0) {
			task.addTagValue(history);
			// 最後將修改的結果更新至DB
			updateTagValue(task);
			// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
			updateFlag = true;
			return true;
		}
		return false;
	}

	private void updateTagValue(IIssue issue) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.updateBugNote(issue);
		itsService.closeConnect();
	}

	private void modifyHandler(long taskID, String handler, Date modifyDate) {
		IIssue task = getById(taskID);

		if (!task.getAssignto().equals(handler)) {
			IITSService itsService = m_itsFactory.getService(m_config);
			itsService.openConnect();
			itsService.updateHandler(task, handler, modifyDate);
			itsService.closeConnect();
		}
	}

}
