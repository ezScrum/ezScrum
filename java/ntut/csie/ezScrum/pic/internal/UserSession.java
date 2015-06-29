package ntut.csie.ezScrum.pic.internal;

import java.util.Date;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;


/**
 * @author Administrator
 *
 */
public class UserSession implements IUserSession {
    private static final long serialVersionUID = -3450844229523749074L;
    private AccountObject _account = null;
    private Date _loginTime = null;
    private String _IP = "";

    /**
     *
     */
    public UserSession(AccountObject account) {
        _account = account;
        _loginTime = new Date();
    }

    public UserSession(AccountObject account, String ip) {
        this(account);
        _IP = ip;
    }

    /* (non-Javadoc)
     * @see ntut.csie.jcis.pic.core.IUserSession#getAccount()
     */
    public AccountObject getAccount() {
        return _account;
    }

    /* (non-Javadoc)
     * @see ntut.csie.jcis.pic.core.IUserSession#getLoginTime()
     */
    public Date getLoginTime() {
        return _loginTime;
    }

    /* (non-Javadoc)
     * @see ntut.csie.jcis.pic.core.IUserSession#getIP()
     */
    public String getIP() {
        return _IP;
    }
}
