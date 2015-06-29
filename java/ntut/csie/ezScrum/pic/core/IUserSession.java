package ntut.csie.ezScrum.pic.core;

import java.io.Serializable;
import java.util.Date;

import ntut.csie.ezScrum.web.dataObject.AccountObject;


public interface IUserSession extends Serializable {
    public AccountObject getAccount();

    public Date getLoginTime();

    public String getIP();
}
