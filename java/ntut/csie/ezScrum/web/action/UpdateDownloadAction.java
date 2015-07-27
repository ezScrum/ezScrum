package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileUtil;
import org.apache.commons.vfs.VFS;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class UpdateDownloadAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String target = request.getParameter("link");

		String success = "false";
		try {
			FileSystemManager fsManager = VFS.getManager();
			FileObject tempF = fsManager.resolveFile(target);
			String path = System.getProperty("System_UpdateWorkspace") + File.separator + tempF.getName().getBaseName();
			FileObject updatePath = fsManager.resolveFile(path);
			FileUtil.copyContent(tempF, updatePath);
			
			success = "true";
		} catch (FileSystemException e) {
			System.out.println("class : UpdateDownloadAction, method : execute, FileSystemException : " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class : UpdateDownloadAction, method : execute, IOException : " + e.toString());
			e.printStackTrace();
		}

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(success);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}