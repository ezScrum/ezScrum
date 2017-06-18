package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScru.web.microservice.CallAccountMicroservice;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.AccountHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class CheckUsernameAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		String username = request.getParameter("id");
		
		// 設置 Header 與編碼
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		try {
			// 將判斷帳號是否有效結果傳給 View
//			response.getWriter().write((new AccountHelper()).validateUsername(username));
			response.getWriter().write(validateUserByUsername(username, userSession.getAccount().getToken()));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String validateUserByUsername(String inputUsername, String token){
		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		Matcher matcher = pattern.matcher(inputUsername);
		boolean doesMatch = matcher.matches();
		
		CallAccountMicroservice accountService = new CallAccountMicroservice(token);
		boolean validateUsername;
		try {
			validateUsername = accountService.checkUsernameIsExist(inputUsername);
			if(doesMatch && !validateUsername && !inputUsername.isEmpty())
				return "true";
			else 
				return "false";
		} catch (IOException e) {
			// TODO Try to connect again
			e.printStackTrace();
		}
		
		return "false";
	}
	
}
