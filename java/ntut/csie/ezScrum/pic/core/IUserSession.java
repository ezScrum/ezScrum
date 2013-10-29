package ntut.csie.ezScrum.pic.core;

import java.io.Serializable;
import java.util.Date;

import ntut.csie.jcis.account.core.IAccount;


public interface IUserSession extends Serializable {
    public IAccount getAccount();

    public Date getLoginTime();

    public String getIP();
}
