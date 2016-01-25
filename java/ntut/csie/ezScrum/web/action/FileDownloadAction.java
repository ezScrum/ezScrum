package ntut.csie.ezScrum.web.action;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.support.SessionManager;


public class FileDownloadAction extends DownloadAction {
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws Exception {
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		ProjectObject projectObject = (ProjectObject) SessionManager.getProjectObject(request);
		AccountObject userObject = session.getAccount();

		// attach file的資訊
		long fileId = Long.parseLong(request.getParameter("fileId"));
		String fileName = request.getParameter("fileName");
		
		// 用 file id 取得檔案
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(projectObject);
		ProjectHelper projectHelper = new ProjectHelper();
		AttachFileObject attachFile = productBacklogHelper.getAttachFile(fileId);
		boolean validDownload = productBacklogHelper.checkAccountInProject(projectHelper.getProjectMemberList(projectObject), userObject);
		
		if(validDownload) {
			/*
			 * 將字串的 UTF-8 編碼轉成 response 預設編碼 ISO-8859-1 jetty預設處理getParameter的編碼是UTF-8 tomcat預設處理getParameter的邊碼是ISO-8859-1 也就是 jetty server可以跑 new String( fileName.getBytes("UTF-8"),"ISO-8859-1"); tomcat
			 * server可以跑 new String( fileName.getBytes("ISO-8859-1"),"ISO-8859-1");
			 */
			fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			response.setHeader("Content-disposition", "inline; filename=\"" + fileName + "\"");

			// 用fileType預設檔案類型
			File file = new File(attachFile.getPath());
			StreamInfo fileStream = new FileStreamInfo(attachFile.getContentType(), file);
			return fileStream;
		} else {
			response.setStatus(403);
			response.getWriter().write("Can not download this file!");
			return null;
		}
		
	}
}