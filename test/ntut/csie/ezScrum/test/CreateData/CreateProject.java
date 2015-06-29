package ntut.csie.ezScrum.test.CreateData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.issue.sql.service.internal.TestConnectException;
import ntut.csie.ezScrum.iteration.iternal.MantisProjectManager;
import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.project.core.ICVS;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IWorkspace;
import ntut.csie.jcis.resource.core.ResourceFacade;
import ntut.csie.jcis.resource.core.internal.Workspace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateProject {
	private static Log mlog = LogFactory.getLog(CreateProject.class);
	private Configuration mConfig = new Configuration();
	private int mProjectCount = 1;
	private ArrayList<IProject> mProjects;
	public String mProjectName = "TEST_PROJECT_";			// TEST_PROJECT_X
	public String mProjectDisplayName = "TEST_DISPLAYNAME_";// TEST_DISPLAYNAME_X
	public String mProjectMaNager = "Project_Manager_";		// Project_Manager_X
	public String mProjectCommon = "This is Test Project - ";// This is Test Project - X
	private long mFileSize = 2;								// attach file size

	private String[] mOperation = {"ProductOwner", "ScrumMaster", "ScrumTeam", "Stakeholder", "Guest"};

	private ProjectMapper mProjectMapper;
	
	public CreateProject(int count) {
		mProjectCount = count;
		mProjects = new ArrayList<IProject>();
		mProjectMapper = new ProjectMapper();
	}

	public ArrayList<IProject> getProjectList() {
		return mProjects;
	}
	
	// ezScrum v1.8
	public ArrayList<ProjectObject> getAllProjects() {
		return mProjectMapper.getAllProjects();
	}

	// ezScrum v1.8
	public void exeCreateForDb() {
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectInfo projectInfo = new ProjectInfo();
		for (int i = 0; i < mProjectCount; i++) {
			projectInfo.name = mProjectName + (i + 1);				// TEST_PROJECT_X
			projectInfo.displayName = mProjectDisplayName + (i + 1);// TEST_DISPLAYNAME_X
			projectInfo.common = mProjectCommon + (i + 1);			// This is Test Project - X
			projectInfo.manager = mProjectMaNager + (i + 1);		// Project_Manager_X
			
			projectMapper.createProject(projectInfo.name, projectInfo);
		}
	}
	
	public void exeCreate() {
		exeCreateForDb();	// 等全部改完後，就可以全部用此method了
		boolean success = false;
		String projectName = "";
		String comment = "";
		String pmName = "";

		IWorkspace ws = new Workspace();	// set workspace path
		ResourceFacade.setWorkSpace(ws);

		// 自動產生輸入的專案個數
		for (int i = 0; i < mProjectCount; i++) {
			projectName = mProjectName + Integer.toString((i + 1));	// TEST_PROJECT_X
			comment = mProjectCommon + Integer.toString((i + 1));	// This is Test Project - X
			pmName = mProjectMaNager + Integer.toString((i + 1));		// Project_Manager_X

			// save project info to test workspace
			IProject project = saveWorkSpace(projectName, comment, pmName, mFileSize);

			// save in the permission
			createPermission(projectName);

			// save in the Role
			createRole(projectName);

			copyScrumRoleSetting(project.getFullPath().getPathString());

			// save in DB
			try {
				saveDB(project);

				// add to list
				mProjects.add(project);

				success = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (success || mProjectCount == 0) {
			System.out.println("Create " + mProjectCount + " test projects success.");
			mlog.info("Create " + mProjectCount + " test projects success.");
		} else {
			mlog.info("Create project fail.");
			System.out.println("新增專案失敗了辣！！！");
			System.out.println("怎麼辦～怎麼辦～怎麼辦～是誰寫的出來面對！！！");
		}
	}

	// save project to workspace and session
	private IProject saveWorkSpace(String PJ_Name, String Comment, String PJ_Manager, long FileSize) {
		IProject project = mProjectMapper.getProjectByID(PJ_Name);
		IProjectDescription desc = project.getProjectDesc();

		// ================ JCIS Project mockup data ===============
		desc.setVersion("");
		desc.setState("");
		desc.setOutput(null);
		desc.setSrc(new IPath[0]);

		ICVS cvs = desc.getCVS();
		cvs.setServerType("SVN");
		cvs.setConnectionType("pserver");
		cvs.setSvnHook("Close");

		cvs.setHost("127.0.0.1");
		cvs.setUserID("");
		cvs.setPassword("");
		cvs.setRepositoryPath("");
		// ================ JCIS Project mockup data ===============

		// ezScrum project information
		desc.setName(PJ_Name);
		desc.setDisplayName(PJ_Name);
		desc.setComment(Comment);
		desc.setProjectManager(PJ_Manager);
		desc.setAttachFileSize(String.valueOf(FileSize));

		// project 不存在，建立專案資訊後再存入
		if (project.exists()) {
//			project.save();
		} else {
			project.create();
		}

		return project;
	}

	// 儲存 account permission 資訊
	private void createPermission(String resource) {
		IAccountManager am = AccountFactory.getManager();
		for (String oper : mOperation) {
			String name = resource + "_" + oper;
			IPermission perm = AccountFactory.createPermission(name, resource, oper);
			am.addPermission(perm);
			perm = am.getPermission(name);
			if (perm == null) {
				am.referesh();
			}
			am.save();
		}
	}

	// 儲存 account role 資訊
	private void createRole(String resource) {
		IAccountManager am = AccountFactory.getManager();
		for (String oper : mOperation) {
			String name = resource + "_" + oper;
			IRole role = AccountFactory.createRole(name, name);
			am.addRole(role);
			List<String> permissionNameList = new ArrayList<String>();
			permissionNameList.add(name);
			// 加入成功則進行群組成員與Role的設置
			if (am.getRole(name) != null) {
				am.updateRolePermission(name, permissionNameList);
			}
			am.save();
		}
	}

	// 儲存專案資訊於資料庫
	private void saveDB(IProject project) {
		Configuration config = new Configuration(mConfig.getUserSession());
		MantisService M_service = new MantisService(config);
		try {
			M_service.testConnect();		// 測試連線
		} catch (TestConnectException e) {
			try {
				if (e.getType().equals(TestConnectException.TABLE_ERROR)) {
					// 資料庫尚未建立的錯誤，重新建立並且匯入乾淨的資料表
					M_service.createDB();
					mlog.info("Create a new DataBase : " + mConfig.getDBName());
					M_service.initiateDB();
					mlog.info("Initialize the database from sql file.");
				} else if (e.getType().equals(TestConnectException.DATABASE_ERROR)) {
					// 資料表不正確的錯誤，重新建立並且匯入乾淨的資料表
					M_service.createDB();
					mlog.info("Create a new DataBase : " + mConfig.getDBName());
					M_service.initiateDB();
					mlog.info("Initialize the database from sql file.");
				} else {
					mlog.info("class: CreateProject, method: saveDB, something error");
				}
			} catch (SQLException sqle) {
				mlog.info("class: CreateProject, method: saveDB, SQL exception.");
			}

			// 如果project Create失敗，就把目前產生到一半的Project檔案刪除
			try {
				project.delete();
				return;
			} catch (IOException e1) {
				mlog.info("class: CreateProject, method: project.delete(), exception: " + e1.toString());
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		MantisProjectManager pm = new MantisProjectManager(project, null);
		try {
			pm.CreateProject(project.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 複製 ScrumRole 檔案
	private void copyScrumRoleSetting(String ectpprojectpathath) {
		File srcScrumRolePath = new File(mConfig.getDataPath() + File.separator + "InitialData" + File.separator + "ScrumRole.xml");
		File destScrumRolePath = new File(ectpprojectpathath + File.separator + "_metadata" + File.separator + "ScrumRole.xml");

		try {
			InputStream in = new FileInputStream(srcScrumRolePath);
			OutputStream out = new FileOutputStream(destScrumRolePath);

			byte[] buf = new byte[1024];		// buffer
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			mlog.debug("copyScrumRoleSetting error" + e.toString());
		}
	}
}
