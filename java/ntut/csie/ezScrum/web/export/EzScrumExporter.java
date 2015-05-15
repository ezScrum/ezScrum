package ntut.csie.ezScrum.web.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.jcis.core.util.DateUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EzScrumExporter {
	private ArrayList<String> mProjectsName = new ArrayList<String>();
	private static String FOLDER_NAME = "ExportBackup";
	private static String BACKUP_TITLE = "ezScrum_";
	private static String SUFFIX = ".json";
	private static String SLASH = "/";

	public EzScrumExporter(ArrayList<String> projectsName) {
		mProjectsName = projectsName;
	}
	
	public EzScrumExporter(String projectsName) {
		System.out.println("EzScrumExporter: "+projectsName);
		mProjectsName = parseProjectsName(projectsName);
		System.out.println("EzScrumExporter name count: "+mProjectsName.size());
	}
	
	public ArrayList<String> parseProjectsName(String projectsName) {
		ArrayList<String> projectNameList = new ArrayList<String>();
		if (projectsName.equals("") || projectsName.equals(",")) {
			return projectNameList;
		}
		String[] splitedProjectsName = projectsName.split(",");
		projectNameList = new ArrayList<String>(Arrays.asList(splitedProjectsName));
		return projectNameList;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getJsonObject() {
		ArrayList<ProjectObject> projects = getFilteredProjects(mProjectsName);
		JSONObject resultJsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (ProjectObject project : projects) {
			JSONObject jsonObject = JsonTranslater.translateProject(project);
			jsonArray.add(jsonObject);
		}
		resultJsonObject.put("projects", jsonArray);
		return resultJsonObject;
	}

	@SuppressWarnings("unchecked")
	public File getJsonFile() throws IOException {
		createFolderIfNotExist();
		JSONObject resultJsonObject = getJsonObject();
		Date date = DateUtil.dayFillter(DateUtil.getNow(),
				DateUtil._16DIGIT_DATE_TIME);
		String dateDescription = DateUtil.format(date,
				DateUtil._16DIGIT_DATE_TIME_2);
		String filePath = FOLDER_NAME + SLASH + BACKUP_TITLE + dateDescription
				+ SUFFIX;
		FileWriter fileWriter = new FileWriter(filePath);
		try {
			fileWriter.write(resultJsonObject.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileWriter.flush();
			fileWriter.close();
		}
		return new File(filePath);
	}

	private void createFolderIfNotExist() {
		File theDir = new File(FOLDER_NAME);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}

	private ArrayList<ProjectObject> getFilteredProjects(
			ArrayList<String> projectsName) {
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		// get all projects
		ProjectLogic projectLogic = new ProjectLogic();
		List<ProjectObject> allProjects = projectLogic.getAllProjectsForDb();
		for (ProjectObject project : allProjects) {
			if (projectsName.contains(project.getName())) {
				projects.add(project);
			}
		}
		return projects;
	}
}