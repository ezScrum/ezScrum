package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateAccount {
	private static Log mlog = LogFactory.getLog(CreateRelease.class);
	private int mAccountCount = 0;
	private String mAccountUsername = "TEST_ACCOUNT_ID_";
	private String mAccountNickname = "TEST_ACCOUNT_REALNAME_";
	private String mAccountPassword = "TEST_ACCOUNT_PWD_";
	private String mAccountMail = "TEST_ACCOUNT_MAIL_";

	private List<AccountObject> mAccounts;
	private AccountHelper mAccountHelper;

	public CreateAccount(int count) {
		mAccountCount = count;
		mAccounts = new ArrayList<AccountObject>();
		mAccountHelper = new AccountHelper();
	}

	/**	
	 * 自動產生建構時給的 count 個數
	 */
	public void exe() {
		AccountInfo user;
		for (int i = 0; i < mAccountCount; i++) {	// ID = 1 為預設 admin 
			String ID = Integer.toString(i + 1);
			String Acc_userNmae = mAccountUsername + ID;
			String Acc_nickName = mAccountNickname + ID;
			String Acc_PWD = mAccountPassword + ID;
			String Acc_Mail = mAccountMail + ID;
			user = new AccountInfo();
			user.username = Acc_userNmae;
			user.nickName = Acc_nickName;
			user.password = Acc_PWD;
			user.email = Acc_Mail;
			
			AccountObject account = mAccountHelper.createAccount(user);
			mAccounts.add(account);
			mlog.info("Create " + mAccountCount + " accounts success.");
		}
		System.out.println("Create " + mAccountCount + " accounts success.");
	}

	/**
	 * return ID = TEST_ACCOUNT_ID_X
	 */
	public String getAccount_ID(int i) {
		return (mAccountUsername + Integer.toString(i));
	}

	/**
	 * return Name = TEST_ACCOUNT_NAME_X
	 */
	public String getAccount_RealName(int i) {
		return (mAccountNickname + Integer.toString(i));
	}

	/**
	 * return PWD = TEST_ACCOUNT_PWD_X
	 */
	public String getAccount_PWD(int i) {
		return (mAccountPassword + Integer.toString(i));
	}

	/**
	 * return MAIL = TEST_ACCOUNT_MAIL_
	 */
	public String getAccount_Mail(int i) {
		return (mAccountMail + Integer.toString(i));
	}

	/**
	 * return the added account Object
	 */
	public List<AccountObject> getAccountList() {
		return mAccounts;
	}

	/**
	 * return Account counts
	 */
	public int getAccountCount() {
		return mAccountCount;
	}

	/**
	 * reset Account Name through accountId
	 * @param accountIndex
	 */
	public void setAccount_RealName(int accountIndex) {
		AccountObject userObject = mAccounts.get(accountIndex - 1);
		long id = userObject.getId();
		String username = userObject.getUsername();
		String password = userObject.getPassword();
		String mail = userObject.getEmail();
		String nickname = mAccountNickname + "NEW_" + id;
		AccountInfo user = new AccountInfo();
		user.id = id;
		user.username = username;
		user.nickName = nickname;
		user.password = password;
		user.email = mail;
		user.enable = true;
		mAccounts.set(accountIndex - 1, mAccountHelper.updateAccount(user));
	}
}
