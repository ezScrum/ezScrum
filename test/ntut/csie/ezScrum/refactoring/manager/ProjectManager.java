package ntut.csie.ezScrum.refactoring.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectManager {
	private static Log mlog = LogFactory.getLog(ProjectManager.class);

	public ProjectManager() {
	}

	public void createProject() {
	}

	/**
	 * 還原 rolebase.xml 的初始檔
	 * 
	 * @param testDataPath
	 */
	public void initialRoleBase(String testDataPath) {
		// 初始化 RoleBase.xml 為最初的檔案，因為執行過後會對此檔案做修改，這樣會影響到下次的執行
		File srcRoleBase = new File(testDataPath + File.separator + "InitialData" + File.separator + "RoleBase.xml");
		File destRoleBase = new File(testDataPath + File.separator + "TestWorkspace" + File.separator + "RoleBase.xml");
		try {
			copyDirectory(srcRoleBase, destRoleBase);
		} catch (IOException e) {
			mlog.debug("class: CopyProject, method: init_RoleBase, IO Exception: " + e.toString());
			e.printStackTrace();
		}
		mlog.info("Initialize BoleBase.xml");
	}

	private void copyDirectory(File srcPath, File dstPath) throws IOException {
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}
			String files[] = srcPath.list();

			for (int i = 0; i < files.length; i++) {
				copyDirectory(new File(srcPath, files[i]), new File(dstPath, files[i]));
			}
		} else {
			if (!srcPath.exists()) {
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
	 * 刪除所有測試專案的資料夾
	 */
	public void deleteAllProject() {
		ProjectMapper projectMapper = new ProjectMapper();
		List<IProject> projectList = projectMapper.getAllProjectList();
		for (IProject project : projectList) {
			// ..\TestData\MyWorkspace
			String projectName = project.getName();
			String srcPath = projectMapper.getProjectByID(projectName).getFullPath().getPathString();

			// delete from workspace
			File srcFile = new File(srcPath);
			deleteDirectory(srcFile);
		}
	}

	/**
	 * 將資料從 path 移除
	 */
	private void deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		path.delete();
	}
}
