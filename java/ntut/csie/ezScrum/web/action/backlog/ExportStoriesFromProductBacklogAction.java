package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import ntut.csie.ezScrum.iteration.support.ExcelHandler;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

public class ExportStoriesFromProductBacklogAction extends DownloadAction {
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		// get all stories
		ArrayList<StoryObject> stories = new ProductBacklogLogic(project).getStories();

		// set the path of the temp file
		File temp = File.createTempFile("ezScrumExcel",
				Long.toString(System.nanoTime()));
		String path = temp.getAbsolutePath();

		try {
			WritableWorkbook workbook = Workbook.createWorkbook(new File(path));
			WritableSheet sheet = workbook.createSheet("BACKLOG", 0);
			// delicate to excel handler
			ExcelHandler handler = new ExcelHandler(sheet);
			handler.save(stories);

			// 寫入excel
			workbook.write();
			// 關閉excel的連結
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setHeader("Content-disposition",
				"inline; filename=" + project.getName() + "_ProductBacklog.xls");

		String contentType = "application/vnd.ms-excel";
		File file = new File(path);

		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project,
				session.getAccount());
		if (sr.getAccessProductBacklog()) {
			return new FileStreamInfo(contentType, file);
		} else {
			return null;
		}
	}
}
