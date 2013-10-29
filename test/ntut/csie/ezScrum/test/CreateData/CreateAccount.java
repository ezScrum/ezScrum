package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;

public class CreateAccount {
	private static Log log = LogFactory.getLog(CreateRelease.class);

	private int AccountCount = 0;
	private String Account_ID = "TEST_ACCOUNT_ID_";
	private String Account_NAME = "TEST_ACCOUNT_REALNAME_";
	private String Account_PWD = "TEST_ACCOUNT_PWD_";
	private String Account_Mail = "TEST_ACCOUNT_MAIL_";

	private List<IAccount> mAccountList;
	private AccountHelper mAccountHelper;

	public CreateAccount(int ACcount) {
		AccountCount = ACcount;
		mAccountList = new ArrayList<IAccount>();
		mAccountHelper = new AccountHelper();
	}

	/**
	 * 自動產生建構時給的 count 個數
	 */
	public void exe() {
		UserInformation user;
		String roles = "user";
		for (int i = 0; i < AccountCount; i++) {
			String ID = Integer.toString(i + 1);
			String Acc_ID = Account_ID + ID;
			String Acc_RLNAME = Account_NAME + ID;
			String Acc_PWD = Account_PWD + ID;
			String Acc_Mail = Account_Mail + ID;
			user = new UserInformation(Acc_ID, Acc_RLNAME, Acc_PWD, Acc_Mail, "true");
			IAccount account = mAccountHelper.createAccount(user, roles);
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
	public List<IAccount> getAccountList() {
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
		String id = Integer.toString(accountIndex);
		String account = getAccount_ID(accountIndex);
		String password = getAccount_PWD(accountIndex);
		String mail = getAccount_Mail(accountIndex);
		String name = Account_NAME + "NEW_" + id;
		UserInformation user = new UserInformation(account, name, password, mail, "true");
		mAccountHelper.updateAccount(user);
		mAccountList.get(accountIndex - 1).setName(name);
	}
}
