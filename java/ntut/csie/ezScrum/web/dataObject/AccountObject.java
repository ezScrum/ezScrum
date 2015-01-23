package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.HashMap;

import ntut.csie.ezScrum.dao.AccountDAO;

public class AccountObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;
	
	private long mId;
	private String mUsername;
	private String mPassword;
	private String mEmail;
	private String mName;
	private boolean mEnable;
	private HashMap<String, ProjectRole> mRoles;
	
	public AccountObject(long id, String username) {
		mUsername = username;
		mId = id;
	}
	
	public AccountObject(String username) {
		mUsername = username;
	}
	
	public String toString() {
		String user = "username :" + getUsername() + 
				", password :" + getPassword() +
				", email :" + getEmail() +
				", name :" + getName() +
				", enable :" + getEnable();
		return user;
	}

	public long getId() {
	    return mId;
    }

	public String getPassword() {
	    return mPassword;
    }

	public AccountObject setPassword(String password) {
	    mPassword = password;
	    return this;
    }
	
	public String getUsername() {
		return mUsername;
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
	
	public static AccountObject get(String account) {
		return AccountDAO.getInstance().get(account);
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
		mUsername = account.getUsername();
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
