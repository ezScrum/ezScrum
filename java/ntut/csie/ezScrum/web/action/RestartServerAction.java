package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RestartServerAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Boolean isAdministration = (Boolean) request.getSession().getAttribute(
				"Administration");
		String result = "{\"success\":false}";
	
		if (isAdministration) {
			restartServer();
		} else {
			try {
				response.setContentType("text/html; charset=utf-8");
				response.getWriter().write(result);
				response.getWriter().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private void restartServer() throws IOException {
		Socket s = new Socket(InetAddress.getByName("127.0.0.1"), 8079);
		OutputStream out = s.getOutputStream();
		out.write(("Restart Server by StopJetty").getBytes());
		out.flush();
		s.close();
	}

}