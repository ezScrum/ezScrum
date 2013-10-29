package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.sql.*;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class TestConnAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

        String driver = "com.mysql.jdbc.Driver";
        String username = "";
        String password = "";
        String sqlurl = "jdbc:mysql://";
        
        sqlurl += request.getParameter("sqlurl");
        username = request.getParameter("username");
        password = request.getParameter("password");
        
        try { 
            Class.forName(driver); 
            Connection conn = DriverManager.getConnection(sqlurl, username, password);
			// build connection
            if(conn != null && !conn.isClosed()) {
        		response.getWriter().print("success");
                conn.close();
            }
        }
        catch(com.mysql.jdbc.CommunicationsException e) {
        	response.getWriter().print("CommunicationsException");
        }
        catch(ConnectException e) {
        	response.getWriter().print("ConnectionException");
        }
        catch(ClassNotFoundException e) {  
            response.getWriter().print("ClassNotFoundException");
        } 
        catch(SQLException e) { 
			response.getWriter().print("SQLException"); 
        }
        catch(UnknownHostException e) {
			response.getWriter().print("UnknownHostException");
        }
        finally {
        	response.getWriter().close();
        }
        return null;
	}
}
