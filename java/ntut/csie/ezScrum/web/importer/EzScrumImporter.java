package ntut.csie.ezScrum.web.importer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EzScrumImporter {
	private File mEzScrumFile = null;
	private static String KEY_OF_PROJECTS = "projects";
	
	public EzScrumImporter(File ezScrumFile) {
		mEzScrumFile = ezScrumFile;
	}
	
	public boolean doImport() {
		boolean isSuccess = false;
		JSONParser parser = new JSONParser();
        try {
        	String filePath = mEzScrumFile.getAbsolutePath();
            JSONObject wholeJson = (JSONObject) parser.parse(new FileReader(filePath));;
            JSONArray projectJsonArray = (JSONArray) wholeJson.get(KEY_OF_PROJECTS);
            ArrayList<ProjectObject> projects = JsonParser.parseProjects(projectJsonArray);
            for (ProjectObject project : projects) {
            	project.save();
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return isSuccess;
	}
}
