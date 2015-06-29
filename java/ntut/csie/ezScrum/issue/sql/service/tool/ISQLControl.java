package ntut.csie.ezScrum.issue.sql.service.tool;

import java.sql.Connection;
import java.sql.ResultSet;

public interface ISQLControl {
	public void setUser(String user);

	public void setPassword(String password);

	public Connection getconnection();

	public void connect();

	public void connectToServer();

	public ResultSet executeQuery(String query);

	public boolean execute(String query);

	public boolean execute(String query, boolean return_keys);

	public String[] getKeys();

	public void close();
}
