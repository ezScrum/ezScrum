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
	
	public UnplanItemHelper(ProjectObject project) {
		mUnplanMapper = new UnplanItemMapper(project);
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
	
	public UnplanObject getUnplan(Long projectId, long serialId) {
		return UnplanObject.get(projectId, serialId);
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
		mUnplanMapper.updateUnplan(unplanInfo);
	}
	
	public void deleteUnplan(long unplanId) {
		mUnplanMapper.deleteUnplan(unplanId);
	}
	
	public ArrayList<UnplanObject> getUnplansInSprint(long sprintId) {
		return mUnplanMapper.getUnplansInSprint(sprintId);
	}
	
	public ArrayList<UnplanObject> getAllUnplans() {
		return mUnplanMapper.getAllUnplans();
	}

	/*
	 * 以下為 Response 給前端之資料格式轉換函式
	 */
	
	public StringBuilder getListXML(String sprintIdString) {
		/**
		 * 如果沒有指定 sprint 的 id，則以目前的 sprint 為準
		 * 如果也沒有的話，則以最後一個 sprint 為準
		 * 如果一個 sprint 都沒有的話，則回傳空的 unplan list
		 */ 
		ArrayList<UnplanObject> unplans = new ArrayList<UnplanObject>();
		final String specialSprintId = "ALL"; 
		if (sprintIdString != null && sprintIdString.equals(specialSprintId)) {
			unplans = mUnplanMapper.getAllUnplans();
		} else {
			long serialSprintId = -1;
			try {
				serialSprintId = Long.parseLong(sprintIdString);
			} catch (Exception e) {
				SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(mUnplanMapper.getProject());
				// if there is not current sprint, then get latest sprint
				SprintObject sprint = sprintPlanHelper.getCurrentSprint();
				// The project has no any sprint
				if (sprint != null) {
					serialSprintId = sprint.getSerialId();
				}
			}
			unplans = mUnplanMapper.getUnplansInSprint(serialSprintId);
		}

		// write stories to XML format
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems><Sprint>")
			.append("<Id>").append(sprintIdString).append("</Id>")
			.append("<Name>Sprint ").append(sprintIdString).append("</Name>")
			.append("</Sprint>");
		for (UnplanObject unplan : unplans) {
			SprintObject sprint = SprintObject.get(unplan.getSprintId());
			result.append("<UnplannedItem>");
			result.append("<Id>").append(unplan.getSerialId()).append("</Id>");
			result.append("<Link></Link>");
			result.append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getName())).append("</Name>");
			result.append("<SprintID>").append(sprint.getSerialId()).append("</SprintID>");
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
