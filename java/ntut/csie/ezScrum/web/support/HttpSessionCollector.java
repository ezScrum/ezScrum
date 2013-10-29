package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import ntut.csie.ezScrum.pic.core.ScrumRole;

public class HttpSessionCollector implements HttpSessionListener {
	private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.put( session.getId(), session );
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		sessions.remove(event.getSession().getId());
	}
	
	/**
	 * 取得特定帳號的所有Session記錄。
	 * @param sessionAttributeKey
	 * @return
	 */
	public static List<HttpSession> getSessionList( String sessionAttributeKey ){
		List<HttpSession> sessionList = new ArrayList<HttpSession>();
		for( Entry<String, HttpSession> entry : sessions.entrySet() ) {
			Map<String, ScrumRole> scrumRolesMap = (Map<String, ScrumRole>) entry.getValue().getAttribute(sessionAttributeKey);
			if( scrumRolesMap != null ){
				sessionList.add(entry.getValue());
			}
		}
		return sessionList;
	}
	
	/**
	 * 取的所有上線的Session記錄。
	 * @return
	 */
	public static Map<String, HttpSession> getAllSession(){
		return sessions;
	}
}
