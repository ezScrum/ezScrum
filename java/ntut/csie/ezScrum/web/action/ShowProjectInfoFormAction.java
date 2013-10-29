package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IProject;

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
	        HttpSession session = request.getSession();
	        IProject project = (IProject) session.getAttribute("Project");
	        
	        IProjectDescription desc = project.getProjectDesc();
	        
	        String fileSize = desc.getAttachFileSize();
	        String attachFileSize = "";
			if(fileSize==null||fileSize.compareTo("")==0)
				attachFileSize = "2";
			else
				attachFileSize = desc.getAttachFileSize();
	
	        log.info("project=" + project.getName());
	
	        StringBuilder sb = new StringBuilder();
	        sb.append("<Root><ProjectInfo>");
	        sb.append("<Name>" + desc.getName() + "</Name>");
	        sb.append("<DisplayName>" + desc.getDisplayName()+ "</DisplayName>");
	        sb.append("<AttachFileSize>" + attachFileSize + "</AttachFileSize>");
	        sb.append("<Comment>" + desc.getComment()+ "</Comment>");
	        sb.append("<ProjectManager>" + desc.getProjectManager()+ "</ProjectManager>");
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
