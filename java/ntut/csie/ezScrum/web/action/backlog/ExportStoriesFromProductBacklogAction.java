package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.UnhandledException;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import ntut.csie.ezScrum.iteration.support.ExcelHandler;
import ntut.csie.ezScrum.robust.resource.tool.ResourceManager;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ExportStoriesFromProductBacklogAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ProjectObject project = SessionManager.getProject(request);
		WritableWorkbook workbook = null;
		File tempFile = null;
		ServletOutputStream outputStream = null;
		FileInputStream inputStream = null;
		try {
			// set the path of the temp file
			tempFile = File.createTempFile("ezScrumExcel", Long.toString(System.nanoTime()));
			writeDataToTempFile(project, workbook, tempFile);

			String contentType = "application/vnd.ms-excel";
			// Read the file from server and stream it
			response.setContentType(contentType);
			response.setContentLength((int) tempFile.length());
			response.setHeader("Content-disposition", "inline; filename=" + project.getName() + "_ProductBacklog.xls");

			outputStream = response.getOutputStream();
			inputStream = new FileInputStream(tempFile);
			int bufferSize = 4096;
			byte[] outputBuffer = new byte[bufferSize];
			while (inputStream.read(outputBuffer, 0, bufferSize) != -1) {
				outputStream.write(outputBuffer, 0, bufferSize);
			}
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnhandledException(e);
		} finally {
			// release resource
			ResourceManager.closeResource(inputStream);
			ResourceManager.closeResource(outputStream);
			if (tempFile != null) {
				tempFile.delete();
			}
		}
		return null;
	}

	public void writeDataToTempFile(ProjectObject project, WritableWorkbook workbook, File tempFile) {
		try {
			// 建立 Excel
			workbook = Workbook.createWorkbook(tempFile);
			WritableSheet sheet = workbook.createSheet("BACKLOG", 0);
			// delicate to excel handler
			ExcelHandler handler = new ExcelHandler(project.getId(), sheet);
			// get all stories
			ArrayList<StoryObject> stories = new ProductBacklogLogic(project).getStories();
			handler.save(stories);

			// 寫入excel
			workbook.write();
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnhandledException(e);
		} finally {
			ResourceManager.closeResource(workbook);
		}
	}
}
