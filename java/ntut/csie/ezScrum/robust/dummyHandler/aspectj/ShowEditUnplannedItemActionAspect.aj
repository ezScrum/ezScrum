package ntut.csie.ezScrum.robust.dummyHandler.aspectj;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.struts.action.ActionForward;

import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import javax.servlet.http.HttpServletResponse;
import ntut.csie.ezScrum.web.action.unplan.ShowEditUnplannedItemAction;

public aspect ShowEditUnplannedItemActionAspect {
	pointcut findGetWriter(HttpServletResponse response) : 
		call (PrintWriter HttpServletResponse.getWriter()) 
		&& target(response) 
		&& withincode(ActionForward ShowEditUnplannedItemAction.execute(..));
	
	PrintWriter around(HttpServletResponse response) throws IOException : findGetWriter(response){
		if (AspectJSwitch.getInstance().isSwitchOn("ShowEditUnplannedItemAction")) {
			throw new IOException();
		} else {
			return response.getWriter();
		}
	}
}
