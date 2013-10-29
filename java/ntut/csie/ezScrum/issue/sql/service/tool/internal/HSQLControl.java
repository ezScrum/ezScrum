package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.io.File;
import java.io.IOException;

import ntut.csie.jcis.core.util.FileUtil;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;

public class HSQLControl extends AbstractSQLControl {
	private String DEFAULTDB_NAME = "DefaultDB_H2.h2.db";
	private String DEFAULTDB_TRACE_NAME = "DefaultDB_H2.trace.db";
	@Override
	protected String getURL() {
		// TODO Auto-generated method stub
		String url = "jdbc:h2:file:" + _hostname+";FILE_LOCK=NO";
		return url;
	}
	@Override
	protected String getServerURL()
	{
		return getURL();
	}
	public HSQLControl(String hostname, String port, String dbname) {
		
		init(hostname, port, dbname, "org.h2.Driver");
		
		/*-----------------------------------------------------------
		 *	檢查DataBase檔案是否存在，如果不存在的話就新建一個
		-------------------------------------------------------------*/
		File dbFile = new File(_hostname + ".h2.db");
		// 如果不存在的話就複製一個空的DB檔案過來
		if (!dbFile.exists()) {
			String defaultFile = ResourceFacade.getWorkspace().getRoot().getFolder(
					IProject.METADATA).getFullPath()
					+ "/" + DEFAULTDB_NAME;
			try {
				FileUtil.copyFile(new File(defaultFile), dbFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
	}


}
