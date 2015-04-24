package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowProjectInfoFormAction extends Action {
    private static Log log = LogFactory.getLog(ShowProjectInfoFormAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
        HttpServletRequest request, HttpServletResponse response) {
    	try {
	        ProjectObject project = SessionManager.getProjectObject(request);
	        
	        long fileSize = project.getAttachFileSize();
	        String attachFileSize = "";
			if(fileSize == 0 || fileSize == -1)
				fileSize = 2;
	
	        log.info("project=" + project.getName());
	
	        StringBuilder sb = new StringBuilder();
	        sb.append("<Root><ProjectInfo>");
	        sb.append("<Name>" + project.getName() + "</Name>");
	        sb.append("<DisplayName>" + project.getDisplayName()+ "</DisplayName>");
	        sb.append("<AttachFileSize>" + attachFileSize + "</AttachFileSize>");
	        sb.append("<Comment>" + project.getComment()+ "</Comment>");
	        sb.append("<ProjectManager>" + project.getManager()+ "</ProjectManager>");
	        sb.append("</ProjectInfo></Root>");
	    	
	        response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			response.getWriter().close();

    	} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
    }
}
