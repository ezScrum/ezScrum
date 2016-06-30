package ntut.csie.ezScrum.web.dataInfo;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.UnplanObject;

public class UnplanInfo {
	public long id = -1, sprintId = -1, projectId = -1;
	public long serialId, handlerId = -1, specificTime = 0;
	public String name, notes, statusString;
	public int estimate, actual, status = UnplanObject.STATUS_UNCHECK;
	public ArrayList<Long> partnersId = new ArrayList<Long>();
}
