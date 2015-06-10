package ntut.csie.ezScrum.web.importer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.form.UploadForm;
import ntut.csie.jcis.core.util.FileUtil;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.json.simple.JSONObject;

public class ImportProjectAction extends Action {
	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		UploadForm fileForm = (UploadForm) form;

		FormFile formFile = fileForm.getFile();
		File file;
		try {
			file = convertToFile(formFile);
			EzScrumImporter ezScrumImporter = new EzScrumImporter(file);
			ezScrumImporter.doImport();
			int file_size = (int) file.length();
			if (file_size < 0) {
				json.put("success", false);
				json.put("message", "File error");
			} else {
				json.put("success", true);
				json.put("message", "Success");
				try {
					FileUtil.delete(file.getAbsolutePath());
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(json.toJSONString());
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
		File file = new File(tempUploadFolder + File.separator
				+ formFile.getFileName());
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