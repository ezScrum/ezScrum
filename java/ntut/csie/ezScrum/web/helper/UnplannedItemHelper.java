package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
import ntut.csie.ezScrum.web.mapper.UnplannedItemMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

public class UnplannedItemHelper {

	private UnplannedItemMapper mUnplannedMapper;
	private ProjectObject mProject;
	
	public UnplannedItemHelper(ProjectObject project) {
		mUnplannedMapper = new UnplannedItemMapper(project);
		mProject = project;
	}
	
	public long addUnplanned(long projectId, long sprintId, UnplannedInfo unplannedInfo) {
		return mUnplannedMapper.addUnplanned(projectId, sprintId, unplannedInfo);
	}
	
	public UnplannedObject getUnplanned(long unplannedId) {
		return UnplannedObject.get(unplannedId);
	}
	
	public void updateUnplanned(long unplannedId, UnplannedInfo unplannedInfo) {
		mUnplannedMapper.updateUnplanned(unplannedId, unplannedInfo);
	}
	
	public void deleteUnplanned(long unplannedId) {
		mUnplannedMapper.deleteUnplanned(unplannedId);
	}
	
	public ArrayList<UnplannedObject> getUnplannedsIbSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		return sprint.getUnplanneds();
	}
	
	public ArrayList<UnplannedObject> getAllUnplanneds() {
		return mProject.getUnplanneds();
	}

	/*
	 * 以下為 Response 給前端之資料格式轉換函式
	 */
	
	public StringBuilder getListXML(String sprintId) {
		ArrayList<UnplannedObject> unplanneds = null;

		if (sprintId.equals("ALL")) {
			unplanneds = mUnplannedMapper.getAllUnplanneds();
		} else {
			unplanneds = mUnplannedMapper.getUnplannedsInSprint(Long.parseLong(sprintId));
		}

		TranslateSpecialChar tsc = new TranslateSpecialChar();

		// write stories to XML format
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems><Sprint>")
			.append("<Id>").append(sprintId).append("</Id>")
			.append("<Name>Sprint ").append(sprintId).append("</Name>")
			.append("</Sprint>");
		for (int i = 0; i < unplanneds.size(); i++) {
			result.append("<UnplannedItem>");
			result.append("<Id>").append(unplanneds.get(i).getId()).append("</Id>");
			result.append("<Link></Link>");
			result.append("<Name>").append(tsc.TranslateXMLChar(unplanneds.get(i).getName())).append("</Name>");
			result.append("<SprintID>").append(unplanneds.get(i).getSprintId()).append("</SprintID>");
			result.append("<Estimate>").append(unplanneds.get(i).getEstimate()).append("</Estimate>");
			result.append("<Status>").append(unplanneds.get(i).getStatus()).append("</Status>");
			result.append("<ActualHour>").append(unplanneds.get(i).getActual()).append("</ActualHour>");
			result.append("<Handler>").append(unplanneds.get(i).getHandlerName()).append("</Handler>");
			result.append("<Partners>").append(tsc.TranslateXMLChar(unplanneds.get(i).getPartnersUsername())).append("</Partners>");
			result.append("<Notes>").append(tsc.TranslateXMLChar(unplanneds.get(i).getNotes())).append("</Notes>");
			result.append("</UnplannedItem>");
		}
		result.append("</UnplannedItems>");

		return result;
	}

}
