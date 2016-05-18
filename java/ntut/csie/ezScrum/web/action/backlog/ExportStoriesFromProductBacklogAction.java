package ntut.csie.ezScrum.web.action.backlog;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import ntut.csie.ezScrum.iteration.support.ExcelHandler;
import ntut.csie.ezScrum.robust.resource.tool.ResourceManager;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ExportStoriesFromProductBacklogAction extends DownloadAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws IOException {
		ProjectObject project = SessionManager.getProject(request);
		WritableWorkbook workbook = null;
		File tempFile = null;
		ServletOutputStream outputStream = null;
		DataInputStream inputStream = null;
		try {
			// set the path of the temp file
			tempFile = File.createTempFile("ezScrumExcel", Long.toString(System.nanoTime()));
			String tempFilePath = tempFile.getAbsolutePath();
			// create Excel
			workbook = Workbook.createWorkbook(new File(tempFilePath));
			WritableSheet sheet = workbook.createSheet("BACKLOG", 0);
			// delicate to excel handler
			ExcelHandler handler = new ExcelHandler(project.getId(), sheet);
			// get all stories
			ArrayList<StoryObject> stories = new ProductBacklogLogic(project).getStories();
			handler.save(stories);
			// write date to excel
			workbook.write();
			// write content of excel for front-end
			outputStream = response.getOutputStream();
			int length = 0;
			byte[] buffer = new byte[10240];
			inputStream = new DataInputStream(new FileInputStream(tempFile));
			while (inputStream != null) { 
				length = inputStream.read(buffer);
				// Break when buffer is empty
				if (length == -1) {
					break;
				}
				outputStream.write(buffer, 0, length);    
			}
			response.setContentType("application/vnd.ms-excel");
			response.setContentLength((int)tempFile.length());
			response.setHeader("Content-disposition", "inline; filename=" + project.getName() + "_ProductBacklog.xls");
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// release resource
			ResourceManager.closeResource(workbook);
			ResourceManager.closeResource(inputStream);
			ResourceManager.closeResource(outputStream);
			if (tempFile != null) {
				tempFile.delete();
			}
		}
		return null;
	}

	@Override
	protected StreamInfo getStreamInfo(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws Exception {
		return null;
	}
}
