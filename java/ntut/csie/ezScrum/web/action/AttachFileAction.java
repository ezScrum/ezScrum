package ntut.csie.ezScrum.web.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.form.UploadForm;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.FileUtil;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class AttachFileAction extends Action {
	private String STATUS_SUCCESS = "success";
	private String STATUS_FAILURE = "failure";
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String issueID_string = request.getParameter("issueID");
		String sprintID = request.getParameter("sprintID");
		String fileMaxSize_string = request.getParameter("fileSize");
		int fileMaxSize_int = Integer.parseInt(fileMaxSize_string);
		fileMaxSize_int = fileMaxSize_int*1048576; //(1MB = 1024 KB = 1048573 bytes)
		long issueID_int = Long.parseLong(issueID_string);
		//上傳的file
		UploadForm fileForm = (UploadForm) form;
		FormFile file = fileForm.getFile();
		int file_size = file.getFileSize();
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		//限制檔案上傳大小
		if(file_size<0 || file_size>fileMaxSize_int){
			setStatus(response, STATUS_FAILURE);
		}
		else{
			setStatus(response, STATUS_SUCCESS);
			//上傳至server端
			String fileName = file.getFileName();
			IPath fullPath = project.getFullPath();
			String targetPath = fullPath.getPathString() + "/" + fileName;
			copy(file, targetPath); 
			SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, sprintID)).getSprintBacklogMapper();
			backlog.addAttachFile(issueID_int, targetPath);
			//移除暫存的檔案
			try {
				FileUtil.delete(targetPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 進行檔案複製
	 * 
	 * @param file
	 * @param targetPath
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
			file.destroy() ;
		}
	}
		
	private void setStatus(HttpServletResponse response, String status){
		  try
		  {
		   response.setHeader("ContentType", "text/html;charset=gbk");
		   PrintWriter out = response.getWriter();
		   out.write("<script>parent.callback('"+status+"')</script>");
		  }catch(IOException e){
		   e.printStackTrace();
		  }
	}
		
}
