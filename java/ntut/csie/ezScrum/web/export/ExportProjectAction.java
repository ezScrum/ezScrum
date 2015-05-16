package ntut.csie.ezScrum.web.export;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

public class ExportProjectAction extends DownloadAction {
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String projectsName = "";
		try {
			projectsName = request.getParameter("projectsName");
			System.out.println(projectsName);
		} catch (Exception e) {
		}
		EzScrumExporter exporter = new EzScrumExporter(projectsName);
		File file = exporter.getJsonFile();
		;
		/*
		 * 將字串的 UTF-8 編碼轉成 response 預設編碼 ISO-8859-1
		 * jetty預設處理getParameter的編碼是UTF-8 tomcat預設處理getParameter的邊碼是ISO-8859-1
		 * 也就是 jetty server可以跑 new String(
		 * fileName.getBytes("UTF-8"),"ISO-8859-1"); tomcat server可以跑 new
		 * String( fileName.getBytes("ISO-8859-1"),"ISO-8859-1");
		 */
		String fileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");
		response.setHeader("Content-disposition",
				"inline; filename=\"" + fileName + "\"");

		// 用fileType預設檔案類型
		String contentType = "application/gzip";
		return new FileStreamInfo(contentType, file);
	}
}
