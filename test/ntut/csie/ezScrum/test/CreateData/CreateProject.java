package ntut.csie.ezScrum.test.CreateData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.issue.sql.service.internal.TestConnectException;
import ntut.csie.ezScrum.iteration.iternal.MantisProjectManager;
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
	private static Log log = LogFactory.getLog(CreateProject.class);
	private Configuration configuration = new Configuration();

	private int ProjectCount = 1;
	private List<IProject> ProjectList;
	private List<ProjectObject> projectObjectList;
	public String PJ_NAME = "TEST_PROJECT_";				// TEST_PROJECT_X
	public String PM_NAME = "Project_Manager_";				// Project_Manager_X
	public String COMMENT_NAME = "This is Test Project - ";	// This is Test Project - X
	private String FILESIZE = "2";							// attach file size

	private String[] operation = {"ProductOwner", "ScrumMaster", "ScrumTeam", "Stakeholder", "Guest"};

	public CreateProject(int count) {
		this.ProjectCount = count;
		this.ProjectList = new LinkedList<IProject>();
		this.projectObjectList = new LinkedList<ProjectObject>();
	}

	public List<IProject> getProjectList() {
		return this.ProjectList;
	}
	
	// ezScrum v1.8
	public List<ProjectObject> getProjectObjectList() {
		return this.projectObjectList;
	}

	// ezScrum v1.8
	public void exeCreateForDb() {
		String projectName = "";
		String comment = "";
		String projectManager = "";
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectObject project;
		for (int i = 0; i < this.ProjectCount; i++) {
			projectName = this.PJ_NAME + Integer.toString((i + 1));	// TEST_PROJECT_X
			comment = this.COMMENT_NAME + Integer.toString((i + 1));	// This is Test Project - X
			projectManager = this.PM_NAME + Integer.toString((i + 1));		// Project_Manager_X
			project = new ProjectObject(projectName, projectName, comment, projectManager, "");
			project = projectMapper.createProjectForDb(project);
			projectObjectList.add(project);
			projectMapper.createScrumRole(project.getId());
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
		for (int i = 0; i < this.ProjectCount; i++) {
			projectName = this.PJ_NAME + Integer.toString((i + 1));	// TEST_PROJECT_X
			comment = this.COMMENT_NAME + Integer.toString((i + 1));	// This is Test Project - X
			pmName = this.PM_NAME + Integer.toString((i + 1));		// Project_Manager_X

			// save project info to test worksapce
			IProject project = saveWorkSpace(projectName, comment, pmName, this.FILESIZE);

			// save project info to ITS_config.xml
			saveITS_config(project);

			// save in the permission
			createPermission(projectName);

			// save in the Role
			createRole(projectName);

			// copy scrum role
			copyScrumRoleSetting(project.getFullPath().getPathString());

			// save in DB
			try {
				saveDB(project);

				// add to list
				this.ProjectList.add(project);

				success = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (success || this.ProjectCount == 0) {
			System.out.println("Create " + this.ProjectCount + " test projects success.");
			this.log.info("Create " + this.ProjectCount + " test projects success.");
		} else {
			this.log.info("Create project fail.");
			System.out.println("新增專案失敗了辣！！！");
			System.out.println("怎麼辦～怎麼辦～怎麼辦～是誰寫的出來面對！！！");
		}
	}

	// save project to workspace and session
	private IProject saveWorkSpace(String PJ_Name, String Comment, String PJ_Manager, String FileSize) {
		IProject project = (new ProjectMapper()).getProjectByID(PJ_Name);
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
		desc.setAttachFileSize(FileSize);

		// project 不存在，建立專案資訊後再存入
		if (project.exists()) {
			project.save();
		} else {
			project.create();
		}

		return project;
	}

	// save project info to ITS_config
	private void saveITS_config(IProject project) {
		Configuration configuration = new Configuration();
		configuration.setServerUrl(configuration.getServerUrl());
		configuration.setWebServicePath(configuration.getWebServicePath());
		configuration.setDBAccount(configuration.getDBAccount());
		configuration.setDBPassword(configuration.getDBPassword());
		configuration.setDBType(configuration.getDBType());
		configuration.setDBName(configuration.getDBName());
		configuration.store();
	}

	// 儲存 account permission 資訊
	private void createPermission(String resource) {
		IAccountManager am = AccountFactory.getManager();
		for (String oper : this.operation) {
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
		for (String oper : this.operation) {
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
		Configuration config = new Configuration(configuration.getUserSession());
		MantisService M_service = new MantisService(config);
		try {
			M_service.TestConnect();		// 測試連線
		} catch (TestConnectException e) {
			try {
				if (e.getType().equals(TestConnectException.TABLE_ERROR)) {
					// 資料庫尚未建立的錯誤，重新建立並且匯入乾淨的資料表
					M_service.createDB();
					this.log.info("Create a new DataBase : " + configuration.getDBName());
					M_service.initiateDB();
					this.log.info("Initialize the database from sql file.");
				} else if (e.getType().equals(TestConnectException.DATABASE_ERROR)) {
					// 資料表不正確的錯誤，重新建立並且匯入乾淨的資料表
					M_service.createDB();
					this.log.info("Create a new DataBase : " + configuration.getDBName());
					M_service.initiateDB();
					this.log.info("Initialize the database from sql file.");
				} else {
					this.log.info("class: CreateProject, method: saveDB, something error");
				}
			} catch (SQLException sqle) {
				this.log.info("class: CreateProject, method: saveDB, SQL exception.");
			}

			// 如果project Create失敗，就把目前產生到一半的Project檔案刪除
			try {
				project.delete();
				return;
			} catch (IOException e1) {
				this.log.info("class: CreateProject, method: project.delete(), exception: " + e1.toString());
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
		File srcScrumRolePath = new File(configuration.getTestDataPath() + File.separator + "InitialData" + File.separator + "ScrumRole.xml");
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
			this.log.debug("copyScrumRoleSetting error" + e.toString());
		}
	}
}
