package ntut.csie.ezScrum.iteration.iternal;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.resource.core.IProject;

public class MantisProjectManager {
	private IUserSession mUserSession;
	private Configuration mConfig;
	
	public MantisProjectManager(IProject project, IUserSession userSession) {
		mUserSession = userSession;
		
		// 初始 ITS 的設定
		mConfig = new Configuration(mUserSession);
	}
	
	public void CreateProject(String ProjectName) throws Exception {
		MantisService mantisService = new MantisService(mConfig);
		mantisService.openConnect();
		
		try {
			mantisService.createProject(ProjectName);			
		} catch (Exception e) {
			throw e;
		}
		mantisService.closeConnect();
	}
}