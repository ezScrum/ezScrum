package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.jcis.core.ISystemPropertyEnum;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.ResourceFacade;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class FileUploadAction extends Action {
	private Log log = LogFactory.getLog(this.getClass());
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		log.info("接收到Upload File訊息");
		
		//取得上傳檔案的存放位置
		IPath tempDirectory = ResourceFacade.createPath(System.getProperty(ISystemPropertyEnum.TEMPWORKSPACE_PATH));

		if (isMultipart) {

			// create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Set factory constraints
			// the size threshold(buffer) , in bytes
			factory.setSizeThreshold(8192);

			// The directory in which temporary files will be located
			factory.setRepository(new File(tempDirectory.getPathString()));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// The maximum allowed size , in bytes . The default value of -1
			// indicates , that there is no limit
			upload.setSizeMax(-1);

			List<FileItem> items = null;

			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}

			for (FileItem item : items) {
				if (!item.isFormField()) {
					String fieldName = item.getFieldName();
					String fileName = item.getName();
					boolean contentType = item.isInMemory();
					long sizeInBytes = item.getSize();

					if (sizeInBytes > 0) {
						File uploadedFile = new File(tempDirectory.getPathString(),fileName);
						try
						{
							item.write(uploadedFile);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

		return super.execute(mapping, form, request, response);
	}

}
