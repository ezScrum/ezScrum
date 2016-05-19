package ntut.csie.ezScrum.robust.carelessCleanup.aspectj;

import java.io.IOException;

import ntut.csie.ezScrum.web.action.backlog.ExportStoriesFromProductBacklogAction;
import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import org.apache.struts.action.ActionForward;
import jxl.write.WritableWorkbook;


public aspect ExportStoriesFromProductBacklogActionAspectJ {
	pointcut findWrite(WritableWorkbook workbook) : call(void WritableWorkbook.write()) 
	&& target(workbook)
    && withincode(ActionForward ExportStoriesFromProductBacklogAction.execute(..));
	
	void around(WritableWorkbook workbook) throws IOException : findWrite(workbook){
		if (AspectJSwitch.getInstance().isSwitchOn("ExportStoriesFromProductBacklogAction")) {
			throw new IOException();
		} else {
			workbook.write();
		}
	}
}
