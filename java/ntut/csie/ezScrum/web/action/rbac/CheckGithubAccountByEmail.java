package ntut.csie.ezScrum.web.action.rbac;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.SecurityRequestProcessor;

public class CheckGithubAccountByEmail extends Action {
	private static Log log = LogFactory.getLog(CheckGithubAccountByEmail.class);

	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) {
		log.info(" Getting github account by given token.");

		String token = request.getParameter("token");
		log.info(token);
		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(token);

			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time :");
			log.info("Sending data to frontend.");
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
