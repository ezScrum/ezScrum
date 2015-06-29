package ntut.csie.ezScrum.test.CreateData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CopyProject {
	private static Log mlog = LogFactory.getLog(CopyProject.class);
	private List<IProject> mProjects = null;
	private Configuration mConfig = new Configuration();
	private ProjectMapper mProjectMapper = new ProjectMapper();
	
	public CopyProject(CreateProject CP) {
		mProjects = CP.getProjectList();
	}
	
	/**
	 * 複製並且刪除測試資料夾
	 */
	public void exeCopy_Delete_Project() throws IOException, Exception {
		String projectName = "";

		for (IProject p : mProjects) {
			projectName = p.getName();	// TEST_PROJECT_X
			
			// ..\TestData\MyWorkspace
//			String srcPath = ResourceFacade.getWorkspace().getRoot().getProject(projectName).getFullPath().getPathString();
			IProject project = mProjectMapper.getProjectByID(projectName);
			String srcPath = project.getFullPath().getPathString();
			File srcFile = new File(srcPath);
			
			// ..\UserName\Desktop\ezScrum_TestData
			String destPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "ezScrum_TestData" + File.separator;
			File destFile = new File(destPath);
			if (!destFile.exists()) {
				destFile.mkdirs();
			}
			destPath = destPath + projectName + File.separator;
			destFile = new File(destPath);
			
			// copy from workspace
			copyDirectory(srcFile, destFile);
			mlog.info("測試專案已成功複製到 " + destPath);
			
			// delete from workspace
			deleteDirectory(srcFile);
			mlog.info("測試專案已成功從 " + srcPath + " 刪除");
		}
		
		// 備份以及還原  RoleBase.xml
		copyRoleBase();
	}
	
	/**
	 *  複製測試資料夾
	 */
	public void exeCopy_Project() throws IOException, Exception {
		String projectName = "";

		for (IProject p : mProjects) {
			projectName = p.getName();	// TEST_PROJECT_X
			
			// ..\TestData\MyWorkspace
//			String srcPath = ResourceFacade.getWorkspace().getRoot().getProject(projectName).getFullPath().getPathString();
			IProject project = mProjectMapper.getProjectByID(projectName);
			String srcPath = project.getFullPath().getPathString();
			File srcFile = new File(srcPath);
			
			// ..\UserName\Desktop\ezScrum_TestData
			String destPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "ezScrum_TestData" + File.separator;
			File destFile = new File(destPath);
			if (!destFile.exists()) {
				destFile.mkdirs();
			}
			destPath = destPath + projectName + "\\";
			destFile = new File(destPath);
			
			// copy from workspace
			copyDirectory(srcFile, destFile);
			mlog.info("測試專案已成功複製到 " + destPath);
		}
		
		// 備份以及還原  RoleBase.xml
		copyRoleBase();
	}
	
	/** 
	 * 刪除測試資料夾
	 */
	public void exeDelete_Project() {
		String projectName = "";

		for (IProject p : mProjects) {
			projectName = p.getName();	// TEST_PROJECT_X
			
			// ..\TestData\MyWorkspace
//			String srcPath = ResourceFacade.getWorkspace().getRoot().getProject(projectName).getFullPath().getPathString();
			IProject project = mProjectMapper.getProjectByID(projectName);
			String srcPath = project.getFullPath().getPathString();
			File srcFile = new File(srcPath);
			
			// delete from workspace
			deleteDirectory(srcFile);
			mlog.info("Delete test project from " + srcPath + " success.");
		}
		
		// 刪除專案時不用複製到桌面，所以直接初始化
		init_RoleBase();
	}
	
	/**
	 *  將資料從 srcPath 複製到 destPath
	 */
	private void copyDirectory(File srcPath, File dstPath) throws IOException {
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}
			
			String files[] = srcPath.list();
			
		    for(int i=0; i<files.length; i++) {
		    	copyDirectory(new File(srcPath, files[i]), new File(dstPath, files[i]));
		    }
		} else {
			if(!srcPath.exists()) {
				mlog.info("File is not exist.");
			} else {
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				
				byte[] buf = new byte[1024];		// buffer
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
	}
	
	/**
	 * 將資料從 path 移除
	 */
	private void deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		
		path.delete();
	}
	
	/**
	 * 備份 rolebase.xml 以及還原 rolebase.xml 的初始檔
	 */
	private void copyRoleBase() throws IOException {
		File srcRoleBase = new File(System.getProperty("ntut.csie.jcis.accountManager.path"));
		String destPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "ezScrum_TestData" + File.separator + "RoleBase.xml";
		File destRoleBase = new File(destPath);
		
		copyDirectory(srcRoleBase, destRoleBase);
		mlog.info(srcRoleBase.getAbsolutePath() + " 已成功複製到 " + destRoleBase.getAbsolutePath());
		
		// 初始化 RoleBase.xml 為最初的檔案，因為執行過後會對此檔案做修改，避免影響到下次的執行
		init_RoleBase();
	}
	
	/**
	* 還原 rolebase.xml 的初始檔
	*/
	private void init_RoleBase() {
		// 初始化 RoleBase.xml 為最初的檔案，因為執行過後會對此檔案做修改，這樣會影響到下次的執行
		File srcRoleBase = new File(mConfig.getDataPath() + File.separator + "InitialData" + File.separator + "RoleBase.xml");
		File destRoleBase = new File(System.getProperty("ntut.csie.jcis.accountManager.path"));
		try {
			copyDirectory(srcRoleBase, destRoleBase);
		} catch (IOException e) {
			mlog.debug("class: CopyProject, method: init_RoleBase, IO Exception: " + e.toString());
			e.printStackTrace();
		}
		mlog.info("Initialize BoleBase.xml");		
	}
}
