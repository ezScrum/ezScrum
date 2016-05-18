package ntut.csie.ezScrum.robust.carelessCleanup.aspectj;

import java.io.IOException;

import ntut.csie.ezScrum.web.action.backlog.ExportStoriesFromProductBacklogAction;
import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import org.apache.struts.action.ActionForward;


public aspect ExportStoriesFromProductBacklogActionAspectJ {
	before() throws IOException:
        call(void write())
        && withincode(ActionForward ExportStoriesFromProductBacklogAction.execute(..)) {
		if (AspectJSwitch.getInstance().isSwitchOn("ExportStoriesFromProductBacklogAction")) {
			throw new IOException();
		}
	}
}
