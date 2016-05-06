package ntut.csie.ezScrum.robust.dummyHandler.aspectj;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.struts.action.ActionForward;

import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import ntut.csie.ezScrum.web.action.unplan.ShowEditUnplanItemAction;


public aspect ShowEditUnplanItemActionAspectJ {
	before() throws IOException:
        call(PrintWriter getWriter())
        && withincode(ActionForward ShowEditUnplanItemAction.execute(..)) {
		if (AspectJSwitch.getInstance().isSwitchOn("ShowEditUnplanItemAction")) {
			throw new IOException();
		}
	}
}
