package ntut.csie.ezScrum.web.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
		FileReader fileReader = null;
        try {
        	fileReader = new FileReader(mEzScrumFile);
            JSONObject wholeJson = (JSONObject) parser.parse(fileReader);
            fileReader.close();
            JSONArray projectJsonArray = (JSONArray) wholeJson.get(KEY_OF_PROJECTS);
            ArrayList<ProjectObject> projects = JsonParser.parseProjects(projectJsonArray);
            for (ProjectObject project : projects) {
            	project.save();
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
    		try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return isSuccess;
	}
}
