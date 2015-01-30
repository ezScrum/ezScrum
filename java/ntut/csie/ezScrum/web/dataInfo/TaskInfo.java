package ntut.csie.ezScrum.web.dataInfo;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskInfo {
	public long projectId = -1, taskId = -1;
	public long serialId, handlerId = -1, storyId, specificTime = 0;
	public String name, notes;
	public int estimate, remains, actual, status = TaskObject.STATUS_UNCHECK;
	public ArrayList<Long> partnersId = new ArrayList<Long>();
}