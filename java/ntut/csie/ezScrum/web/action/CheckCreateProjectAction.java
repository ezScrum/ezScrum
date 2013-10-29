package ntut.csie.ezScrum.web.action;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class CheckCreateProjectAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Root><CheckCreateProject>");
		
		//如果專案個數大於三個，則必須判斷license是否為合法的
//		ProjectHelper helper = new ProjectHelper();
//        IProject[] projects = helper.getAllProjects();
//		List<IProject> projects = helper.getAllProjects();
       
        sb.append("<Check>true</Check>");
        
        sb.append("</CheckCreateProject></Root>");
        
        response.setContentType("text/xml; charset=utf-8");
		response.getWriter().write(sb.toString());
		response.getWriter().close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

