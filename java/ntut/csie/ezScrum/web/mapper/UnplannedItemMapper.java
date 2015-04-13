package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.ScrumIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class UnplannedItemMapper {
	// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
	private boolean mUpdateFlag = true;
	private IProject mProject;
	private Configuration mConfig;
	private IUserSession mUserSession;
	private MantisService mMantisService;

	public UnplannedItemMapper(IProject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		mConfig = new Configuration(mUserSession);
		mMantisService = new MantisService(mConfig);
	}

	public IIssue getById(long id) {
		mMantisService.openConnect();
		IIssue issue = mMantisService.getIssue(id);
		mMantisService.closeConnect();

		if (issue.getCategory().equals(ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE)) {
			return issue;
		}

		return null;
	}

	public ArrayList<IIssue> getList(String sprintId) throws SQLException {
		mMantisService.openConnect();
		IIssue[] issues = mMantisService.getIssues(mProject.getName(), ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE, null, sprintId, null);
		mMantisService.closeConnect();

		ArrayList<IIssue> list = new ArrayList<IIssue>();
		for (IIssue issue : issues) {
			list.add(new ScrumIssue(issue));
		}

		return list;
	}

	public ArrayList<IIssue> getAll() throws SQLException {
		mMantisService.openConnect();
		IIssue[] issues = mMantisService.getIssues(mProject.getName(), ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE);

		ArrayList<IIssue> list = new ArrayList<IIssue>();

		for (IIssue issue : issues) {
			list.add(new ScrumIssue(issue));
		}

		mMantisService.closeConnect();
		return list;
	}

	public long add(String name, String estimate,
	        String handler, String partners, String notes, Date date,
	        String unplanneditemIssueType, long sprintId) {

		mMantisService.openConnect();
		
		IIssue unplannedItem = new Issue();
		unplannedItem.setProjectID(mProject.getName());
		unplannedItem.setSummary(name);
		unplannedItem.setCategory(unplanneditemIssueType);
		unplannedItem.setParentId(sprintId);
		
		
		long unplannedId = mMantisService.newIssue(unplannedItem);
		unplannedItem = mMantisService.getIssue(unplannedId);

		// 將此 unPlannedItem 的關係加入 Story Relation Table
		mMantisService.updateStoryRelationTable(unplannedId, mProject.getName(), "-1", String.valueOf(sprintId), null, null, date);

		String actualHour = "0";

//		modifyHandler(unplannedId, handler, date);
		// 利用 edit 來增加 tag
 		editNote(unplannedId, estimate, partners, actualHour, notes, date, String.valueOf(sprintId));

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		mUpdateFlag = true;
		mMantisService.closeConnect();
		return unplannedId;
	}

	public void update(long issueId, String name,
	        String handler, String status, String partners, String estimated,
	        String actualHour, String notes, String sprintId, Date date) {
		
		mMantisService.openConnect();
		IIssue issue = mMantisService.getIssue(issueId);

		if (!issue.getSummary().equals(name)) {
			mMantisService.updateName(issue, name, date);
		}
		if (!issue.getStatus().equals(status)) {
			modifyStatus(issueId, status, name, notes, date);
		}
		if (!issue.getAssignto().equals(handler)) {
			modifyHandler(issueId, handler, date);
		}
		if (!issue.getPartners().equals(partners)) {
			addHistory(issueId, issue.getIssueType(), HistoryObject.TYPE_PARTNERS,
					issue.getPartners(), partners);
		}
		if (!issue.getEstimated().equals(estimated)) {
			addHistory(issueId, issue.getIssueType(), HistoryObject.TYPE_ESTIMATE,
					issue.getEstimated(), estimated);
		}
		if (!issue.getActualHour().equals(actualHour)) {
			addHistory(issueId, issue.getIssueType(), HistoryObject.TYPE_ACTUAL,
					issue.getActualHour(), actualHour);
		}
		if (!issue.getNotes().equals(notes)) {
			addHistory(issueId, issue.getIssueType(), HistoryObject.TYPE_NOTE,
					issue.getNotes(), notes);
		}
		if (!issue.getSprintID().equals(sprintId)) {
			addHistory(issueId, issue.getIssueType(), HistoryObject.TYPE_NOTE,
					issue.getSprintID(), sprintId);
		}

		// 將此 unPlannedItem 的關係加入 Story Relation Table
		mMantisService.updateStoryRelationTable(issueId, mProject.getName(), null, sprintId, estimated, null, date);
		editNote(issueId, estimated, partners, actualHour, notes, date, String.valueOf(sprintId));
		mMantisService.closeConnect();
	}

	public void delete(String issueId) {
		mMantisService.openConnect();
		mMantisService.removeIssue(issueId);
		mMantisService.closeConnect();
	}

	/*
	 * 以下為私有函式
	 */

	private boolean editNote(long unplannedId, String estimate, String partners,
	        String actualHour, String notes, Date modifyDate, String sprintId) {
		// 建立tag
		IIssue unplanned = mMantisService.getIssue(unplannedId);

		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
		        (modifyDate == null ? new Date() : modifyDate),
		        DateUtil._16DIGIT_DATE_TIME_2));

		if (estimate != null && !estimate.equals("")) {
			if (!unplanned.getEstimated().equals(estimate)) {
				Element storyPoint = new Element(ScrumEnum.ESTIMATION);
				storyPoint.setText(estimate);
				history.addContent(storyPoint);
			}
		}

		if (!unplanned.getPartners().equals(partners)) {
			Element element = new Element(ScrumEnum.PARTNERS);
			element.setText(partners);
			history.addContent(element);
		}
		if (notes != null) {
			if (!unplanned.getNotes().equals(notes)) {
				Element element = new Element(ScrumEnum.NOTES);
				element.setText(notes);
				history.addContent(element);
			}
		}
		if (actualHour != null && !actualHour.equals("")) {
			if (!unplanned.getActualHour().equals(actualHour)) {
				Element element = new Element(ScrumEnum.ACTUALHOUR);
				element.setText(actualHour);
				history.addContent(element);
			}
		}

		if (sprintId != null && !sprintId.equals("")
		        && Integer.parseInt(sprintId) >= 0) {
			Element element = new Element(ScrumEnum.SPRINT_ID);
			element.setText(sprintId);
			history.addContent(element);
		}

		if (history.getChildren().size() > 0) {
			unplanned.addTagValue(history);
			// 最後將修改的結果更新至DB
			updateTagValue(unplanned);
			// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
			mUpdateFlag = true;
			return true;
		}
		return false;
	}

	private void updateTagValue(IIssue issue) {
		mMantisService.updateBugNote(issue);
	}

	private void modifyHandler(long unplannedId, String handler, Date modifyDate) {
		IIssue unplanned = mMantisService.getIssue(unplannedId);
		mMantisService.updateHandler(unplanned, handler, modifyDate);
	}
	
	private void modifyStatus(long unplannedId, String newStatus, String name,
			String notes, Date date) {
		
		if (newStatus.equals(ITSEnum.S_NEW_STATUS)) {
			mMantisService.resetStatusToNew(unplannedId, name, notes, date);
		}
		
		if (newStatus.equals(ITSEnum.S_ASSIGNED_STATUS)) {
			mMantisService.reopenStatusToAssigned(unplannedId, name, notes, date);
		}
		
		if (newStatus.equals(ITSEnum.S_CLOSED_STATUS)) {
			mMantisService.changeStatusToClosed(unplannedId, ITSEnum.FIXED_RESOLUTION,
					notes, date);
		}
	}

	public void addHistory(long issueId, int issueType, int historyType,
			String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(issueId, issueType, historyType,
				oldValue, newValue, System.currentTimeMillis());
		history.save();
	}
}
