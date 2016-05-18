package ntut.csie.ezScrum.robust.carelessCleanup.aspectj;

import java.io.IOException;

import ntut.csie.ezScrum.web.action.backlog.ExportStoriesFromProductBacklogAction;
import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import org.apache.struts.action.ActionForward;


public aspect ExportStoriesFromProductBacklogActionAspectJ {
	pointcut findGetWriter() : call(void write())
    && withincode(ActionForward ExportStoriesFromProductBacklogAction.execute(..));
	
	void around() throws IOException : findGetWriter(){
		if (AspectJSwitch.getInstance().isSwitchOn("ExportStoriesFromProductBacklogAction")) {
			throw new IOException();
		}
	}
}
