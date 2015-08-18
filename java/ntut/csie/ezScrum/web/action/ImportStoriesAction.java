package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.support.ExcelHandler;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.form.UploadForm;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.FileUtil;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.ResourceFacade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class ImportStoriesAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ImportStoriesAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Import Stories in ImportStoriesAction.");

		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);

		// 處理上傳附件所新增的form bean
		// get parameter info
		UploadForm fileForm = (UploadForm) form;
		FormFile file = fileForm.getFile();
		int file_size = file.getFileSize();

		String result = "{\"success\":false}";

		// 有附件檔案時
		if (file_size > 0) {
			// 將檔案從暫存區移動至專案底下的資料夾
			String fileName = file.getFileName();
			IPath fullPath = ResourceFacade.createPath(new Configuration().getWorkspacePath());
			String targetPath = fullPath.getPathString() + "/" + fileName;
			copy(file, targetPath);
			
			// 利用jxl讀取xls中的檔案
			Workbook workbook = null;
			try {
				workbook = Workbook.getWorkbook(new File(targetPath));
				Sheet sheet = workbook.getSheet("BACKLOG");
				if (sheet == null) {
					result = "{\"success\":false, \"msg\":\"檔案規格不符\"}";
				}

				// 將sheet丟給ExcelHandler做處理
				ExcelHandler handler = new ExcelHandler(project.getId(), sheet);
				handler.load();

				ArrayList<StoryObject> stories = handler.getStories();
				if (stories.size() > 0) {
					for (StoryObject story : stories) {
						story.save();
					}
					result = "{\"success\":true}";
				} else {
					result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
				}
			} catch (BiffException e) {
				result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
			} catch (IOException e) {
				result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
			} finally {
				if (workbook != null) {
					workbook.close();
				}

				// 移除在web server上的檔案
				try {
					FileUtil.delete(targetPath);
				} catch (IOException e) {
					e.printStackTrace();
					result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
				}
			}
		}

		return new StringBuilder(result);
	}

	private void copy(FormFile file, String targetPath) {
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(targetPath);
			fileOutput.write(file.getFileData());
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.destroy();
		}
	}
}
