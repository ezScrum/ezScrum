package ntut.csie.ezScrum.web.helper;

import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.UnplannedItemMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

public class UnplannedItemHelper {

	private UnplannedItemMapper uiMapper;

	public UnplannedItemHelper(IProject project, IUserSession userSession) {
		this.uiMapper = new UnplannedItemMapper(project, userSession);
	}

	public void modifyUnplannedItemIssue(long issueID, String name,
	        String handler, String status, String partners, String estimated,
	        String actualHour, String notes, String sprintID, Date date) {
		uiMapper.update(issueID, name, handler, status, partners, estimated, actualHour, notes, sprintID, date);
	}

	public IIssue getIssue(long id) {
		return uiMapper.getById(id);
	}

	public void delete(String issueID) {
		uiMapper.delete(issueID);
	}

	// 待修正:回傳統一為List
	public IIssue[] getAllUnplannedItem() {
		List<IIssue> issues = uiMapper.getAll();
		for (IIssue issue : issues)
		{
			issues.add(issue);
		}
		return issues.toArray(new IIssue[issues.size()]);
	}

	// 待修正:回傳統一為List
	public IIssue[] getUnplannedItemIssue(int iteration) {
		List<IIssue> issuelist = uiMapper.getList(Integer.toString(iteration));
		return issuelist.toArray(new IIssue[issuelist.size()]);
	}

	public long addUnplannedItem(String name, String estimation,
	        String handler, String partners, String notes, Date date,
	        String unplanneditemIssueType, String SprintID) {
		return uiMapper.add(name, estimation, handler, partners, notes, date, unplanneditemIssueType, SprintID);
	}

	/*
	 * 以下為Response給前端之資料格式轉換函式
	 */

	public StringBuilder getListXML(String sprintId) {
		List<IIssue> unplannedItem = null;

		if (sprintId.equalsIgnoreCase("All")) {
			unplannedItem = this.uiMapper.getAll();
		} else {
			unplannedItem = this.uiMapper.getList(sprintId);
		}

		TranslateSpecialChar tsc = new TranslateSpecialChar();

		// write stories to XML format
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems><Sprint><Id>" + sprintId + "</Id><Name>Sprint " + sprintId + "</Name></Sprint>");
		for (int i = 0; i < unplannedItem.size(); i++) {
			result.append("<UnplannedItem>");
			result.append("<Id>" + unplannedItem.get(i).getIssueID() + "</Id>");
			result.append("<Link>" + tsc.TranslateXMLChar(unplannedItem.get(i).getIssueLink()) + "</Link>");
			result.append("<Name>" + tsc.TranslateXMLChar(unplannedItem.get(i).getSummary()) + "</Name>");
			result.append("<SprintID>" + unplannedItem.get(i).getSprintID() + "</SprintID>");
			result.append("<Estimation>" + unplannedItem.get(i).getEstimated() + "</Estimation>");
			result.append("<Status>" + unplannedItem.get(i).getStatus() + "</Status>");
			result.append("<ActualHour>" + unplannedItem.get(i).getActualHour() + "</ActualHour>");
			result.append("<Handler>" + unplannedItem.get(i).getAssignto() + "</Handler>");
			result.append("<Partners>" + tsc.TranslateXMLChar(unplannedItem.get(i).getPartners()) + "</Partners>");
			result.append("<Notes>" + tsc.TranslateXMLChar(unplannedItem.get(i).getNotes()) + "</Notes>");
			result.append("</UnplannedItem>");
		}
		result.append("</UnplannedItems>");

		return result;
	}

}
