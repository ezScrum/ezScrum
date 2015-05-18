package ntut.csie.ezScrum.web.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.UnplannedItemMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

public class UnplannedItemHelper {

	private UnplannedItemMapper mUnplannedMapper;

	public UnplannedItemHelper(IProject project, IUserSession userSession) {
		mUnplannedMapper = new UnplannedItemMapper(project, userSession);
	}
	
	public UnplannedItemHelper(ProjectObject project, IUserSession userSession) {
		IProject iProject = (new ProjectMapper()).getProjectByID(project.getName());
		mUnplannedMapper = new UnplannedItemMapper(iProject, userSession);
	}

	public void modifyUnplannedItemIssue(long issueId, String name,
	        String handler, String status, String partners, String estimated,
	        String actualHour, String notes, String sprintId, Date date) {
		mUnplannedMapper.update(issueId, name, handler, status, partners, estimated, actualHour, notes, sprintId, date);
	}

	public IIssue getIssue(long id) {
		return mUnplannedMapper.getById(id);
	}

	public void delete(String issueId) {
		mUnplannedMapper.delete(issueId);
	}

	// 待修正:回傳統一為List
	public IIssue[] getAllUnplannedItem() throws SQLException {
		List<IIssue> issues = mUnplannedMapper.getAll();
		for (IIssue issue : issues)
		{
			issues.add(issue);
		}
		return issues.toArray(new IIssue[issues.size()]);
	}

	// 待修正:回傳統一為List
	public IIssue[] getUnplannedItemIssue(int iteration) throws SQLException {
		List<IIssue> issuelist = mUnplannedMapper.getList(Integer.toString(iteration));
		return issuelist.toArray(new IIssue[issuelist.size()]);
	}

	public long addUnplannedItem(String name, String estimate,
	        String handler, String partners, String notes, Date date,
	        String unplanneditemIssueType, long SprintId) {
		return mUnplannedMapper.add(name, estimate, handler, partners, notes, date, unplanneditemIssueType, SprintId);
	}

	/*
	 * 以下為Response給前端之資料格式轉換函式
	 */

	public StringBuilder getListXML(String sprintId) throws SQLException {
		ArrayList<IIssue> unplannedItem = null;

		if (sprintId.equalsIgnoreCase("All")) {
			unplannedItem = mUnplannedMapper.getAll();
		} else {
			unplannedItem = mUnplannedMapper.getList(sprintId);
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
			result.append("<Estimate>" + unplannedItem.get(i).getEstimated() + "</Estimate>");
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
