package ntut.csie.ezScrum.web.dataInfo;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.UnplannedObject;

public class UnplannedInfo {
	public long id = -1, sprintId = -1, projectId = -1;
	public long serialId, handlerId = -1, specificTime = 0;
	public String name, notes;
	public int estimate, actual, status = UnplannedObject.STATUS_UNCHECK;
	public ArrayList<Long> partnerId = new ArrayList<Long>();
}
