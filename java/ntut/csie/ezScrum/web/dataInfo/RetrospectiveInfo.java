package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;

public class RetrospectiveInfo {
	public long id = -1;
	public long serialId = -1;
	public long sprintId = -1;
	public String name = "";
	public String description = "";
	public String type = RetrospectiveObject.TYPE_GOOD;
	public String status = RetrospectiveObject.STATUS_NEW;
}
