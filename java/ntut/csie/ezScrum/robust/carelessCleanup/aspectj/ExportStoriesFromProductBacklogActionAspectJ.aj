package ntut.csie.ezScrum.robust.carelessCleanup.aspectj;

import java.io.IOException;

import jxl.write.WritableWorkbook;
import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import ntut.csie.ezScrum.web.action.backlog.ExportStoriesFromProductBacklogAction;

public aspect ExportStoriesFromProductBacklogActionAspectJ {
	pointcut findWrite(WritableWorkbook workbook) : call(void WritableWorkbook.write()) 
	&& target(workbook)
    && withincode(void ExportStoriesFromProductBacklogAction.writeDataToTempFile(..));
	
	void around(WritableWorkbook workbook) throws IOException : findWrite(workbook){
		if (AspectJSwitch.getInstance().isSwitchOn("ExportStoriesFromProductBacklogAction")) {
			throw new IOException();
		} else {
			workbook.write();
		}
	}
}
