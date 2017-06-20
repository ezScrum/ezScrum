package ntut.csie.ezScrum.pic.internal;

import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScru.web.microservice.CallAccountMicroservice;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;

public class ProjectInfoCenter {
	private static ProjectInfoCenter m_pic = null;

	private ProjectInfoCenter() {

	}

	public static ProjectInfoCenter getInstance() {
		if (m_pic == null) {
			m_pic = new ProjectInfoCenter();
		}

		return m_pic;
	}

	/**
	 * 進行登入, 若帳號密碼為guest則不進行檢查
	 * @throws Exception 
	 */
	public IUserSession login(String id, String password) throws Exception {
		AccountObject theAccount = null;
//		AccountMapper accountMapper = new AccountMapper();
//		theAccount = accountMapper.confirmAccount(id, password);
		theAccount = confirmAccount(id, password);
		IUserSession theUserSession = new UserSession(theAccount);
		return theUserSession;
	}
	
	public AccountObject confirmAccount(String username, String password) throws Exception{
		AccountObject theAccount = null;
		CallAccountMicroservice accountMicroservice = new CallAccountMicroservice();
		String token = accountMicroservice.Login(username, password);
		if(token == "Fail")
			throw new LogonException(false, false);
		accountMicroservice.setToken(token);
		String account = accountMicroservice.sendGetAccountByUsernamePassword(username, password);
		JSONObject accountJSON = new JSONObject(account);
		boolean checkEnabled = Boolean.valueOf(accountJSON.getString("enabled"));
		if(checkEnabled == false){
			throw new LogonException(false, false);
		}
		theAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
		theAccount.setEmail(accountJSON.getString("email"));
		theAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
		theAccount.setNickName(accountJSON.getString("nickname"));	
		theAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
		theAccount.setToken(token);
		
		return theAccount;
	}
}
