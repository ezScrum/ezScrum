package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class StoryInfo {
	public long id = -1;
	public String name;
	public String notes;
	public String howToDemo;
	public int importance = 0;
	public int value = 0;
	public int estimate = 0;
	public int status = StoryObject.STATUS_UNCHECK;
	public long sprintId = -1;
	public String tags; // aaa,bbb,ccc
}
