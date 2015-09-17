package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
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
	
	public long addUnplanned(String handlerUsername, String partnersUsername,
			UnplannedInfo unplannedInfo) {
		unplannedInfo.handlerId = getHandlerId(handlerUsername);
		unplannedInfo.partnersId = getPartnersId(partnersUsername);
		return mUnplannedMapper.addUnplanned(unplannedInfo.projectId,
				unplannedInfo.sprintId, unplannedInfo);
	}
	
	public UnplannedObject getUnplanned(long unplannedId) {
		return UnplannedObject.get(unplannedId);
	}
	
	public void updateUnplanned(String handlerUsername, String partnersUsername,
			UnplannedInfo unplannedInfo) {
		unplannedInfo.handlerId = getHandlerId(handlerUsername);
		unplannedInfo.partnersId = getPartnersId(partnersUsername);
		if (unplannedInfo.statusString.equals("new")) {
			unplannedInfo.status = UnplannedObject.STATUS_UNCHECK;
		} else if (unplannedInfo.statusString.equals("assigned")) {
			unplannedInfo.status = UnplannedObject.STATUS_CHECK;
		} else {
			unplannedInfo.status = UnplannedObject.STATUS_UNCHECK;
		}
		mUnplannedMapper.updateUnplanned(unplannedInfo.id, unplannedInfo);
	}
	
	public void deleteUnplanned(long unplannedId) {
		mUnplannedMapper.deleteUnplanned(unplannedId);
	}
	
	public ArrayList<UnplannedObject> getUnplannedsInSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		ArrayList<UnplannedObject> unplanneds = new ArrayList<>();
		if (sprint != null) {
			unplanneds = sprint.getUnplanneds();
		}
		return unplanneds;
	}
	
	public ArrayList<UnplannedObject> getAllUnplanneds() {
		return mProject.getUnplanneds();
	}

	/*
	 * 以下為 Response 給前端之資料格式轉換函式
	 */
	
	public StringBuilder getListXML(String selectedSprint) {
		ArrayList<UnplannedObject> unplanneds = null;
		/**
		 * 如果沒有指定 sprint 的 id，則以目前的 sprint 為準
		 * 如果也沒有的話，則以最後一個 sprint 為準
		 * 如果一個 sprint 都沒有的話，則回傳空的 unplanned list
		 */ 
		// Get all unplanneds
		if (selectedSprint.equals("ALL")) {
			unplanneds = getAllUnplanneds();
		}
		// Get current sprint unplanneds
		else if (selectedSprint.equals("")) {
			SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(mProject);
			// if there is not current sprint, then get latest sprint
			SprintObject sprint = sprintPlanHelper.getCurrentSprint();
			// The project has no any sprint
			if (sprint == null) {
				unplanneds = new ArrayList<UnplannedObject>();
				selectedSprint = "-1";
			} else {
				unplanneds = getUnplannedsInSprint(sprint.getId());
				selectedSprint = String.valueOf(sprint.getId());
			}
		}
		// Get select sprint unplanneds
		else {
			unplanneds = getUnplannedsInSprint(Long.parseLong(selectedSprint));
		}

		// write stories to XML format
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems><Sprint>")
			.append("<Id>").append(selectedSprint).append("</Id>")
			.append("<Name>Sprint ").append(selectedSprint).append("</Name>")
			.append("</Sprint>");
		for (UnplannedObject unplanned : unplanneds) {
			result.append("<UnplannedItem>");
			result.append("<Id>").append(unplanned.getId()).append("</Id>");
			result.append("<Link></Link>");
			result.append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getName())).append("</Name>");
			result.append("<SprintID>").append(unplanned.getSprintId()).append("</SprintID>");
			result.append("<Estimate>").append(unplanned.getEstimate()).append("</Estimate>");
			result.append("<Status>").append(unplanned.getStatusString()).append("</Status>");
			result.append("<ActualHour>").append(unplanned.getActual()).append("</ActualHour>");
			result.append("<Handler>").append(unplanned.getHandlerName()).append("</Handler>");
			result.append("<Partners>").append(unplanned.getPartnersUsername()).append("</Partners>");
			result.append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getNotes())).append("</Notes>");
			result.append("</UnplannedItem>");
		}
		result.append("</UnplannedItems>");

		return result;
	}

	private long getHandlerId(String handlerUsername) {
		long handlerId = -1;
		AccountObject handler = AccountObject.get(handlerUsername);
		if (handler != null) {
			handlerId = handler.getId();
		}
		return handlerId;
	}
	
	private ArrayList<Long> getPartnersId(String partnersUsername) {
		ArrayList<Long> partnersId = new ArrayList<Long>();
		for (String partnerUsername : partnersUsername.split(";")) {
			AccountObject partner = AccountObject.get(partnerUsername);
			if (partner != null) {
				partnersId.add(partner.getId());
			}
		}
		return partnersId;
	}
}
