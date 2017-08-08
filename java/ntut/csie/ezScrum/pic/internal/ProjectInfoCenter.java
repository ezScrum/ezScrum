package ntut.csie.ezScrum.pic.internal;

import ntut.csie.ezScru.web.microservice.IAccount;
import ntut.csie.ezScru.web.microservice.MicroserviceProxy;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

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
		IAccount accountMicroservice = new MicroserviceProxy();
		theAccount = accountMicroservice.confirmAccount(id, password);
		IUserSession theUserSession = new UserSession(theAccount);
		return theUserSession;
	}
	
//	public AccountObject confirmAccount(String username, String password) throws Exception{
//		AccountObject theAccount = null;
//		AccountRESTClientProxy accountMicroservice = new AccountRESTClientProxy();
//		String token = accountMicroservice.Login(username, password);
//		if(token == "Fail")
//			throw new LogonException(false, false);
//		accountMicroservice.setToken(token);
//		String account = accountMicroservice.sendGetAccountByUsernamePassword(username, password);
//		JSONObject accountJSON = new JSONObject(account);
//		boolean checkEnabled = Boolean.valueOf(accountJSON.getString("enabled"));
//		if(checkEnabled == false){
//			throw new LogonException(false, false);
//		}
//		theAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
//		theAccount.setEmail(accountJSON.getString("email"));
//		theAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
//		theAccount.setNickName(accountJSON.getString("nickname"));	
//		theAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
//		theAccount.setToken(token);
//		
//		return theAccount;
//	}
}
