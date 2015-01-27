package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.jcis.account.core.IAccount;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateAccount {
	private static Log log = LogFactory.getLog(CreateRelease.class);

	private int AccountCount = 0;
	private String Account_ID = "TEST_ACCOUNT_ID_";
	private String Account_NAME = "TEST_ACCOUNT_REALNAME_";
	private String Account_PWD = "TEST_ACCOUNT_PWD_";
	private String Account_Mail = "TEST_ACCOUNT_MAIL_";

	private List<AccountObject> mAccountList;
	private AccountHelper mAccountHelper;

	public CreateAccount(int ACcount) {
		AccountCount = ACcount;
		mAccountList = new ArrayList<AccountObject>();
		mAccountHelper = new AccountHelper();
	}

	/**	
	 * 自動產生建構時給的 count 個數
	 */
	public void exe() {
		AccountInfo user;
		for (int i = 0; i < AccountCount; i++) {	// ID = 1 為預設 admin 
			String ID = Integer.toString(i + 1);
			String Acc_userNmae = Account_ID + ID;
			String Acc_nickName = Account_NAME + ID;
			String Acc_PWD = Account_PWD + ID;
			String Acc_Mail = Account_Mail + ID;
			user = new AccountInfo();
			user.userName = Acc_userNmae;
			user.nickName = Acc_nickName;
			user.password = Acc_PWD;
			user.email = Acc_Mail;
			
			AccountObject account = mAccountHelper.createAccount(user);
			mAccountList.add(account);
			log.info("Create " + AccountCount + " accounts success.");
		}
		System.out.println("Create " + AccountCount + " accounts success.");
	}

	/**
	 * return ID = TEST_ACCOUNT_ID_X
	 */
	public String getAccount_ID(int i) {
		return (Account_ID + Integer.toString(i));
	}

	/**
	 * return Name = TEST_ACCOUNT_NAME_X
	 */
	public String getAccount_RealName(int i) {
		return (Account_NAME + Integer.toString(i));
	}

	/**
	 * return PWD = TEST_ACCOUNT_PWD_X
	 */
	public String getAccount_PWD(int i) {
		return (Account_PWD + Integer.toString(i));
	}

	/**
	 * return MAIL = TEST_ACCOUNT_MAIL_
	 */
	public String getAccount_Mail(int i) {
		return (Account_Mail + Integer.toString(i));
	}

	/**
	 * return the added account Object
	 */
	public List<AccountObject> getAccountList() {
		return mAccountList;
	}

	/**
	 * return Account counts
	 */
	public int getAccountCount() {
		return AccountCount;
	}

	/**
	 * reset Account Name through accountId
	 * @param accountIndex
	 */
	public void setAccount_RealName(int accountIndex) {
		AccountObject userObject = mAccountList.get(accountIndex - 1);
		long id = userObject.getId();
		String userNmae = userObject.getUsername();
		String password = userObject.getPassword();
		String mail = userObject.getEmail();
		String nickName = Account_NAME + "NEW_" + id;
		AccountInfo user = new AccountInfo();
		user.id = id;
		user.userName = userNmae;
		user.nickName = nickName;
		user.password = password;
		user.email = mail;
		user.enable = true;
		mAccountList.set(accountIndex - 1, mAccountHelper.updateAccount(user));
	}
}
