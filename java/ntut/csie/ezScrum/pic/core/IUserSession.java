package ntut.csie.ezScrum.pic.core;

import java.io.Serializable;
import java.util.Date;

import ntut.csie.ezScrum.web.dataObject.UserObject;


public interface IUserSession extends Serializable {
    public UserObject getAccount();

    public Date getLoginTime();

    public String getIP();
}
