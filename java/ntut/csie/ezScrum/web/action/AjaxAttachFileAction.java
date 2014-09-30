package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.form.UploadForm;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.account.core.internal.Account;
import ntut.csie.jcis.core.util.FileUtil;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;

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
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		StringBuilder result = new StringBuilder("");
		if (project == null) {
			result.append("{\"success\":false}");
		} else {
			ProjectHelper projectHelper = new ProjectHelper();
			ProjectInfoForm projectInfo = projectHelper.getProjectInfoForm(project);

			int fileMaxSize_int = Integer.parseInt(projectInfo.getAttachFileSize());
			fileMaxSize_int = fileMaxSize_int * 1048576; // (1MB = 1024 KB = 1048576 bytes)

			String issueId_string = request.getParameter("issueID");
			long issueId = Long.parseLong(issueId_string);

			ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, session);
			UploadForm fileForm = (UploadForm) form;

			FormFile fromfile = fileForm.getFile();
			File file = new File(fromfile.getFileName());
			String fileName = file.getName();
			int file_size = (int) file.length();

			if (file_size > fileMaxSize_int) {
				result = new StringBuilder("{\"success\":false, \"msg\":\"Maximum file size is " + projectInfo.getAttachFileSize() + "Mb\"}");
			} else if (file_size < 0) {
				result = new StringBuilder("{\"success\":false, \"msg\":\"File error\"}");
			} else {
				AttachFileInfo attachFileInfo = new AttachFileInfo();
	            attachFileInfo.issueId = issueId;
	            attachFileInfo.name = fileName;
	            attachFileInfo.projectName = project.getName();
	            
				try {
					long id = pbHelper.addAttachFile(attachFileInfo, file);
				} catch (IOException e) {
				}
				
				IIssue issue = pbHelper.getIssue(issueId);
				result = new StringBuilder(new Translation().translateStoryToJson(issue));
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

	/**
	 * 進行檔案複製
	 */
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
