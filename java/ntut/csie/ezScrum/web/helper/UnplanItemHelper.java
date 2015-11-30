package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.UnplanInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.mapper.UnplanItemMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

public class UnplanItemHelper {

	private UnplanItemMapper mUnplanMapper;
	private ProjectObject mProject;
	
	public UnplanItemHelper(ProjectObject project) {
		mUnplanMapper = new UnplanItemMapper(project);
		mProject = project;
	}
	
	public long addUnplan(String handlerUsername, String partnersUsername,
			UnplanInfo unplanInfo) {
		unplanInfo.handlerId = getHandlerId(handlerUsername);
		unplanInfo.partnersId = getPartnersId(partnersUsername);
		return mUnplanMapper.addUnplan(unplanInfo.projectId,
				unplanInfo.sprintId, unplanInfo);
	}
	
	public UnplanObject getUnplan(long unplanId) {
		return UnplanObject.get(unplanId);
	}
	
	public void updateUnplan(String handlerUsername, String partnersUsername,
			UnplanInfo unplanInfo) {
		unplanInfo.handlerId = getHandlerId(handlerUsername);
		unplanInfo.partnersId = getPartnersId(partnersUsername);
		if (unplanInfo.statusString.equals("new")) {
			unplanInfo.status = UnplanObject.STATUS_UNCHECK;
		} else if (unplanInfo.statusString.equals("assigned")) {
			unplanInfo.status = UnplanObject.STATUS_CHECK;
		} else {
			unplanInfo.status = UnplanObject.STATUS_DONE;
		}
		mUnplanMapper.updateUnplan(unplanInfo.id, unplanInfo);
	}
	
	public void deleteUnplan(long unplanId) {
		mUnplanMapper.deleteUnplan(unplanId);
	}
	
	public ArrayList<UnplanObject> getUnplansInSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		ArrayList<UnplanObject> unplans = new ArrayList<>();
		if (sprint != null) {
			unplans = sprint.getUnplans();
		}
		return unplans;
	}
	
	public ArrayList<UnplanObject> getAllUnplans() {
		return mProject.getUnplans();
	}

	/*
	 * 以下為 Response 給前端之資料格式轉換函式
	 */
	
	public StringBuilder getListXML(String selectedSprint) {
		ArrayList<UnplanObject> unplans = null;
		/**
		 * 如果沒有指定 sprint 的 id，則以目前的 sprint 為準
		 * 如果也沒有的話，則以最後一個 sprint 為準
		 * 如果一個 sprint 都沒有的話，則回傳空的 unplan list
		 */ 
		// Get all unplans
		if (selectedSprint.equals("ALL")) {
			unplans = getAllUnplans();
		}
		// Get current sprint unplans
		else if (selectedSprint.equals("")) {
			SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(mProject);
			// if there is not current sprint, then get latest sprint
			SprintObject sprint = sprintPlanHelper.getCurrentSprint();
			// The project has no any sprint
			if (sprint == null) {
				unplans = new ArrayList<UnplanObject>();
				selectedSprint = "-1";
			} else {
				unplans = getUnplansInSprint(sprint.getId());
				selectedSprint = String.valueOf(sprint.getId());
			}
		}
		// Get select sprint unplans
		else {
			unplans = getUnplansInSprint(Long.parseLong(selectedSprint));
		}

		// write stories to XML format
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems><Sprint>")
			.append("<Id>").append(selectedSprint).append("</Id>")
			.append("<Name>Sprint ").append(selectedSprint).append("</Name>")
			.append("</Sprint>");
		for (UnplanObject unplan : unplans) {
			result.append("<UnplannedItem>");
			result.append("<Id>").append(unplan.getId()).append("</Id>");
			result.append("<Link></Link>");
			result.append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getName())).append("</Name>");
			result.append("<SprintID>").append(unplan.getSprintId()).append("</SprintID>");
			result.append("<Estimate>").append(unplan.getEstimate()).append("</Estimate>");
			result.append("<Status>").append(unplan.getStatusString()).append("</Status>");
			result.append("<ActualHour>").append(unplan.getActual()).append("</ActualHour>");
			result.append("<Handler>").append(unplan.getHandlerName()).append("</Handler>");
			result.append("<Partners>").append(unplan.getPartnersUsername()).append("</Partners>");
			result.append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getNotes())).append("</Notes>");
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
