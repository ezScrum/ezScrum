package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.HashMap;

import ntut.csie.ezScrum.dao.AccountDAO;

public class AccountObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;
	
	private long mId;
	private String mAccount;
	private String mPassword;
	private String mEmail;
	private String mName;
	private boolean mEnable;
	private HashMap<String, ProjectRole> mRoles;
	
	public AccountObject() {}
	
	public AccountObject(long id, String account, String name, String password, String email, boolean enable, HashMap<String, ProjectRole> roles) {
		setId(id);
		setAccount(account);
		setPassword(password);
		setName(name);
		setEmail(email);
		setEnable(enable);
		setRoles(roles);
	}
	
	public AccountObject(long id, String account, String name, String password, String email, boolean enable) {
		setId(id);
		setAccount(account);
		setPassword(password);
		setName(name);
		setEmail(email);
		setEnable(enable);
	}
	
	public AccountObject(String account, String name, String password, String email, boolean enable) {
		setAccount(account);
		setPassword(password);
		setName(name);
		setEmail(email);
		setEnable(enable);
	}
	
	public String toString() {
		String user = "account :" + getAccount() + 
				", password :" + getPassword() +
				", email :" + getEmail() +
				", name :" + getName() +
				", enable :" + getEnable();
		return user;
	}

	public long getId() {
	    return mId;
    }

	public AccountObject setId(long id) {
	    mId = id;
	    return this;
    }

	public String getAccount() {
	    return mAccount;
    }

	public AccountObject setAccount(String account) {
	    mAccount = account;
	    return this;
    }

	public String getPassword() {
	    return mPassword;
    }

	public AccountObject setPassword(String password) {
	    mPassword = password;
	    return this;
    }

	public String getEmail() {
	    return mEmail;
    }

	public AccountObject setEmail(String email) {
	    mEmail = email;
	    return this;
    }

	public String getName() {
	    return mName;
    }

	public AccountObject setName(String name) {
	    mName = name;
	    return this;
    }

	public boolean getEnable() {
	    return mEnable;
    }

	public AccountObject setEnable(boolean enable) {
	    mEnable = enable;
	    return this;
    }

	public HashMap<String, ProjectRole> getRoles() {
	    return mRoles;
    }

	public AccountObject setRoles(HashMap<String, ProjectRole> roles) {
	    mRoles = roles;
	    return this;
    }
	
	/**
	 * 取得一筆 account
	 * @param id
	 */
	public static AccountObject get(long id) {
		return AccountDAO.getInstance().get(id);
	}
	
	public static ArrayList<AccountObject> getAccounts() {
		return AccountDAO.getInstance().getAccounts();
	}
	
	public HashMap<String, ProjectRole> getProjectRoleList() {
		return AccountDAO.getInstance().getProjectRoleList(mId);
	}
	
	public static ProjectRole getSystemRole(long id) {
		return AccountDAO.getInstance().getSystemRole(id);
	}
	
	@Override
	public boolean delete() {
		boolean success = AccountDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
		}
		return success;
	}

	@Override
    public void save() {
		if (recordExists()) {
			doUpdate();			
		} else {
			doCreate();
		}
    }

	@Override
    public void reload() throws Exception {
		if (recordExists()) {
			AccountObject account = AccountDAO.getInstance().get(mId);
			if (account != null) {
				resetData(account);
			}
		}
    }
	
	private boolean recordExists() {
		return mId > 0;
	}
	
	private void resetData(AccountObject account) {
		mId = account.getId();
		mAccount = account.getAccount();
		mPassword = account.getPassword();
		mEmail = account.getEmail();
		mName = account.getName();
		mEnable = account.getEnable();
		mRoles = account.getRoles();
	}
	
	private void doCreate() {
		mId = AccountDAO.getInstance().create(this);
		try {
			reload();
		} catch (Exception e) {
		}
	}
	
	private void doUpdate() {
		AccountDAO.getInstance().update(this);
	}
}
