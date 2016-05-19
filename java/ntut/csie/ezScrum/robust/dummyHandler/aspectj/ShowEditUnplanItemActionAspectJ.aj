package ntut.csie.ezScrum.robust.dummyHandler.aspectj;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.struts.action.ActionForward;

import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import ntut.csie.ezScrum.web.action.unplan.ShowEditUnplanItemAction;
import javax.servlet.http.HttpServletResponse;

public aspect ShowEditUnplanItemActionAspectJ {
	pointcut findGetWriter(HttpServletResponse response) : call (PrintWriter HttpServletResponse.getWriter()) 
		&& target(response) 
		&& withincode(ActionForward ShowEditUnplanItemAction.execute(..));
	
	PrintWriter around(HttpServletResponse response) throws IOException : findGetWriter(response){
		if (AspectJSwitch.getInstance().isSwitchOn("ShowEditUnplanItemAction")) {
			throw new IOException();
		} else {
			return response.getWriter();
		}
	}
}
