package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskInfo {
	public long serialId, handlerId = -1, projectId, storyId, specificTime = 0;
	public String name, notes;
	public int estiamte, remains, actual, status = TaskObject.STATUS_UNCHECK;
}