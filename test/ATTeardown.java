


import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;

public class ATTeardown {

	private Configuration mConfig = null;
	
	@Test
	public void testCreateAccount() throws Exception {
		mConfig = new Configuration();
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}

}
