


import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;

import org.junit.Test;

public class ATTeardown {

	private Configuration mConfig = null;
	
	@Test
	public void testCreateAccount() throws Exception {
		mConfig = new Configuration();
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
	}

}
