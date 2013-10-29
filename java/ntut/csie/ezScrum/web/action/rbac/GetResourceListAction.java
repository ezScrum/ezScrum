package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.helper.ScrumRoleHelper;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetResourceListAction extends Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ScrumRoleHelper srh = new ScrumRoleHelper();
//		ProjectLogic helper = new ProjectLogic();
//		List<IProject> projects = helper.getAllProjects();
//		
//		List<ProjectNameUI> pnui_list = new LinkedList<ProjectNameUI>();
//		for (IProject p : projects) {
//			pnui_list.add(new ProjectNameUI(p));
//		}
//		
//		Gson gson = new Gson();
		
		try {
//			response.getWriter().write(gson.toJson(pnui_list));
			response.getWriter().write(srh.getResourceList());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

//	private class ProjectNameUI {
//		private String text = "";
//		private boolean leaf = true;
//		private String cls = "treepanel-leaf";
//    	private String iconCls = "leaf-icon";
//		
//		public ProjectNameUI(IProject project) {
//			this.text = project.getName();
//		}
//	}
}
