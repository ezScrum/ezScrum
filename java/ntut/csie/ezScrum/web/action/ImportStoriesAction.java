package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.support.ExcelHandler;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.form.UploadForm;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.FileUtil;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class ImportStoriesAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ImportStoriesAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}
	
	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Import Stories in ImportStoriesAction.");
		
		// get session info
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		ProjectObject projectObject = (ProjectObject) SessionManager.getProjectObject(request);
		
		// 處理上傳附件所新增的form bean
		// get parameter info
		UploadForm fileForm = (UploadForm) form; 
		FormFile file = fileForm.getFile(); 
		int file_size = file.getFileSize();
		
		String result = "{\"success\":false}";
		
		// 有附件檔案時
		if( file_size > 0 ){
			//上傳至web server
			String fileName = file.getFileName();
			IPath fullPath = project.getFullPath();
			String targetPath = fullPath.getPathString() + "/" + fileName;
			copy(file, targetPath); 
			//利用jxl讀取xls中的檔案
			Workbook workbook = null;
			try {
				workbook = Workbook.getWorkbook(new File(targetPath));
				Sheet sheet = workbook.getSheet("BACKLOG");
				if(sheet==null){
					result = "{\"success\":false, \"msg\":\"檔案規格不符\"}";
				}
				//將sheet丟給ExcelHandler做處理
				ExcelHandler handler = new ExcelHandler(sheet);
				handler.load();
				//如果取得的stories為null，代表可能發生了error的情況
				List<IIssue> stories = handler.getStories();
				ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(projectObject);
				if(stories!=null) {
					for(int i=0;i<stories.size();i++){
						String summary = stories.get(i).getSummary();
						String des ="";
						String imp = stories.get(i).getImportance();
						String estimate = stories.get(i).getEstimated();
						String howToDemo = stories.get(i).getHowToDemo();
						String notes = stories.get(i).getNotes();
						String value = stories.get(i).getTagValue(ScrumEnum.VALUE);
						String sprintID = stories.get(i).getSprintID();
						String tagIDs = "";
						String releaseID = "";
						
						StoryInfo storyInfo = new StoryInfo();
						storyInfo.name = name;
						storyInfo.importance = Integer.parseInt(importance);
						storyInfo.estimate = Integer.parseInt(estimate);
						storyInfo.value = Integer.parseInt(value);
						storyInfo.howToDemo = howToDemo;
						storyInfo.notes = notes;
						storyInfo.sprintId = Long.parseLong(sprintId);
						storyInfo.tags = tags;
						productBacklogHelper.addNewStory(storyInformation);
					}
					
					result = "{\"success\":true}";
				} else {
					result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
				}
			} catch (BiffException e) {
				result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
			} catch (IOException e) {
				result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
			} finally{
				if (workbook!=null) {
					workbook.close();
				}
				
				// 移除在web server上的檔案
				try {
					FileUtil.delete(targetPath);
				} catch (IOException e) {
					e.printStackTrace();
					result = "{\"success\":false, \"msg\":\"檔案格式出錯\"}";
				}
			}
		}
		
		return new StringBuilder(result);
	}

	private void copy(FormFile file, String targetPath) {
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(targetPath);
			fileOutput.write(file.getFileData()); 
			fileOutput.flush(); 
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.destroy() ;
		}
	}
}
