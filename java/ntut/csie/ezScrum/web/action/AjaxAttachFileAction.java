package ntut.csie.ezScrum.web.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.form.UploadForm;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.FileUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class AjaxAttachFileAction extends Action {
	private static Log log = LogFactory.getLog(AjaxAttachFileAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info(" Attach File. ");

		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);

		StringBuilder result = new StringBuilder("");
		if (project == null) {
			result.append("{\"success\":false}");
		} else {
			long fileMaxSize_int = project.getAttachFileSize();
			fileMaxSize_int = fileMaxSize_int * 1048576; // (1MB = 1024 KB = 1048576 bytes)
			
			long issueId = Long.parseLong(request.getParameter("issueID"));
			String issueTypeStr = request.getParameter("issueType");
			
			int issueType = 0;
			if (issueTypeStr.equals("Story")) {
				issueType = IssueTypeEnum.TYPE_STORY;
			} else if (issueTypeStr.equals("Task")) {
				issueType = IssueTypeEnum.TYPE_TASK;
			}
			
			ProductBacklogHelper pbHelper = new ProductBacklogHelper(project);
			UploadForm fileForm = (UploadForm) form;

			FormFile formFile = fileForm.getFile();
			File file;
			try {
				file = convertToFile(formFile);
				String fileName = file.getName();
				int file_size = (int) file.length();
				
				if (file_size > fileMaxSize_int) {
					result = new StringBuilder("{\"success\":false, \"msg\":\"Maximum file size is " + project.getAttachFileSize() + "Mb\"}");
				} else if (file_size < 0) {
					result = new StringBuilder("{\"success\":false, \"msg\":\"File error\"}");
				} else {
					AttachFileInfo attachFileInfo = new AttachFileInfo();
		            attachFileInfo.issueId = issueId;
		            attachFileInfo.issueType = issueType;
		            attachFileInfo.name = fileName;
		            attachFileInfo.contentType = formFile.getContentType();
		            attachFileInfo.projectName = project.getName();
		            
					try {
						pbHelper.addAttachFile(attachFileInfo, file);
						FileUtil.delete(file.getAbsolutePath());
					} catch (IOException e) {
						System.out.println(e);
					}
					
					if (issueTypeStr.equals("Story")) {
						StoryObject story = pbHelper.getStory(issueId);
						result = new StringBuilder(Translation.translateStoryToJson(story));
					} else if (issueTypeStr.equals("Task")) {
						TaskObject task = TaskObject.get(issueId);
						result = new StringBuilder(Translation.translateTaskToJson(task));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private File convertToFile(FormFile formFile) throws IOException {
		String tempUploadFolder = "upload_tmp";
		
		File folder = new File(tempUploadFolder);
		folder.mkdirs();
		File file = new File(tempUploadFolder + File.separator + formFile.getFileName());
		OutputStream os = new FileOutputStream(file);
		InputStream is = new BufferedInputStream(formFile.getInputStream());
		int count;
		byte[] buffer = new byte[4096];
		while ((count = is.read(buffer)) > -1) {
			os.write(buffer, 0, count);
		}
		
		os.close();
		is.close();
		return file;
	}
}
