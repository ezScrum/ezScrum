package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class AjaxGetServerInfoAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
        HttpServletRequest request, HttpServletResponse response) {
        ServletContext context = this.getServlet().getServletContext();
        
       ArrayList<keyValue> resultList = new ArrayList<keyValue>();
        
        addkeyValue(resultList,"Server Information",context.getServerInfo());
        addkeyValue(resultList,"Start Time",System.getProperty("System_Start_Time"));
        addkeyValue(resultList,"JVM Version",System.getProperty("java.vm.version"));
        addkeyValue(resultList,"OS Name",System.getProperty("os.name"));
        Gson gson = new Gson();
        String result = gson.toJson(resultList);
        result = "{\"Datas\":"+result+"}";
        
        response.setContentType("text/html;charset=utf-8");
        try
        {
        	response.getWriter().write(result);
        	response.getWriter().close();
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
        
        return mapping.findForward("success");
    }

	private void addkeyValue(ArrayList<keyValue> list, String key, String value) {
		list.add(new keyValue(key, value));
	}

	private class keyValue {
		String name = "";
		String value = "";

		public keyValue(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
}
