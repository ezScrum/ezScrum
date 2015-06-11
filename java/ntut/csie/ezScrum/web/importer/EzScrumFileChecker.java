package ntut.csie.ezScrum.web.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EzScrumFileChecker {
	public static String EXTENSION = ".json";
	public static String MSG_INVALID_EXTENSION = "File extension invalid !!!";
	public static String MSG_INVALID_FORMAT = "File format invalid !!!";
	public static String KEY_CREATE_TIME = "createTime";
	public static String KEY_ATTACH_MAX_SIZE = "attachMaxSize";
	public static String KEY_PRODUCT_OWNER = "productOwner";
	public static String KEY_NAME = "name";
	public static String KEY_COMMENT = "comment";
	public static String KEY_DISPLAY_NAME = "displayName";
	private File mEzScrumFile;
	private ArrayList<String> mMissingKeys = new ArrayList<>();
	private ArrayList<String> mInvalidKeys = new ArrayList<>();
	private ArrayList<String> mOriginKeys = new ArrayList<>();
	private ArrayList<String> mInvalidValueType = new ArrayList<>();
	private ArrayList<String> mInvalidProjectNames = new ArrayList<>();
	private ArrayList<String> mProjectNames = new ArrayList<>();
	
	public EzScrumFileChecker(File file) {
		mEzScrumFile = file;
		mOriginKeys.add(KEY_CREATE_TIME);
		mOriginKeys.add(KEY_ATTACH_MAX_SIZE);
		mOriginKeys.add(KEY_PRODUCT_OWNER);
		mOriginKeys.add(KEY_NAME);
		mOriginKeys.add(KEY_COMMENT);
		mOriginKeys.add(KEY_DISPLAY_NAME);
	}
	
	public boolean isValid() {
		boolean isValid = true;
		isValid &= isExtensionValid();
		isValid &= isJsonFormatValid();
		if (isValid) {
			isValid &= isKeysetValid();
		}
		if (isValid) {
			isValid &= isValueTypeValid();
		}
		if (isValid) {
			isValid &= !isProjectsExisting();
		}
		return isValid;
	}
	
	public String getReport() {
		String report = "";
		if (!isExtensionValid()) {
			report = MSG_INVALID_EXTENSION;
		} else if (!isJsonFormatValid()) {
			report = MSG_INVALID_FORMAT;
		} else if (!isKeysetValid()) {
			report = getKeysetReport();
		} else if (!isValueTypeValid()) {
			report = getValueTypeReport();
		} else if (isProjectsExisting()) {
			report = getProjectsExistingReport();
		} else {
			report = getImportSuccessReport();
		}
		return report;
	}
	
	private String getImportSuccessReport() {
		String report = "Import ";
		if (!mProjectNames.isEmpty()) {
			for (int i = 0; i < mProjectNames.size(); i++) {
				String key = mProjectNames.get(i);
				if (i == mProjectNames.size() - 1) {
					report += key;
				} else {
					report += key + ", ";
				}
			}
		}
		report += " successfully !!!";
		return report;
	}
	
	private String getProjectsExistingReport() {
		String report = "";
		if (!mInvalidProjectNames.isEmpty()) {
			report += "Existing projects: ";
			for (int i = 0; i < mInvalidProjectNames.size(); i++) {
				String key = mInvalidProjectNames.get(i);
				if (i == mInvalidProjectNames.size() - 1) {
					report += key;
				} else {
					report += key + ", ";
				}
			}
		}
		return report;
	}
	
	private String getKeysetReport() {
		String report = "";
		if (!mMissingKeys.isEmpty()) {
			report += "Missing key: ";
			for (int i = 0; i < mMissingKeys.size(); i++) {
				String key = mMissingKeys.get(i);
				if (i == mMissingKeys.size() - 1) {
					report += key;
				} else {
					report += key + ", ";
				}
			}
			if (!mInvalidKeys.isEmpty()) {
				report += ";";
			}
		}
		if (!mInvalidKeys.isEmpty()) {
			report += "Invalid key: ";
			for (int i = 0; i < mInvalidKeys.size(); i++) {
				String key = mInvalidKeys.get(i);
				if (i == mInvalidKeys.size() - 1) {
					report += key;
				} else {
					report += key + ", ";
				}
			}
		}
		return report;
	}
	
	private String getValueTypeReport() {
		String report = "";
		if (!mInvalidValueType.isEmpty()) {
			report += "Invalid type of key: ";
			for (int i = 0; i < mInvalidValueType.size(); i++) {
				String key = mInvalidValueType.get(i);
				if (i == mInvalidValueType.size() - 1) {
					report += key;
				} else {
					report += key + ", ";
				}
			}
		}
		return report;
	}
	
	private boolean isExtensionValid() {
		String extension = getFileExtension();
		return extension.equals(EXTENSION);
	}
	
	private String getFileExtension() {
	    String filename = mEzScrumFile.getName();
	    try {
	        return filename.substring(filename.lastIndexOf("."));
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	private boolean isJsonFormatValid() {
		boolean isJsonFormat = true;
		JSONObject jsonObject;
		try {
			JSONParser parser = new JSONParser();
			jsonObject = (JSONObject) parser.parse(new FileReader(
			        mEzScrumFile));
		} catch (Exception e) {
			isJsonFormat = false;
		} 
		return isJsonFormat;
	}
	
	private boolean isKeysetValid() {
		String KEY_OF_PROJECTS = "projects";
		boolean isKeysetValid = true;
		int keysetSize = 6;
		JSONParser parser = new JSONParser();
		FileReader fileReader = null;
        try {
        	fileReader = new FileReader(mEzScrumFile);
            JSONObject wholeJson = (JSONObject) parser.parse(fileReader);
            fileReader.close();
            JSONArray projectJsonArray = (JSONArray) wholeJson.get(KEY_OF_PROJECTS);
            // clear missing keys
        	mMissingKeys.clear();
        	// clear invalid keys
        	mInvalidKeys.clear();
            for (int i = 0; i < projectJsonArray.size(); i++) {
            	JSONObject projectJson = (JSONObject) projectJsonArray.get(i);
            	ArrayList<String> keys = new ArrayList<>(projectJson.keySet());
            	isKeysetValid &= keys.size() == keysetSize;
            	
            	// check is any missing key
            	for (String key : mOriginKeys) {
            		if (!keys.contains(key) && !mMissingKeys.contains(key)) {
            			mMissingKeys.add(key);
            			isKeysetValid = false;
            		}
            	}
            	
            	// check is any invalid key
            	for (String key : keys) {
            		if (!mOriginKeys.contains(key) && !mInvalidKeys.contains(key)) {
            			mInvalidKeys.add(key);
            			isKeysetValid = false;
            		}
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
    		try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return isKeysetValid;
	}
	
	private boolean isValueTypeValid() {
		String KEY_OF_PROJECTS = "projects";
		boolean isValueTypeValid = true;
		JSONParser parser = new JSONParser();
		FileReader fileReader = null;
		try {
        	fileReader = new FileReader(mEzScrumFile);
            JSONObject wholeJson = (JSONObject) parser.parse(fileReader);
            fileReader.close();
            JSONArray projectJsonArray = (JSONArray) wholeJson.get(KEY_OF_PROJECTS);
            // clear invalid value type keys
            mInvalidValueType.clear();
            for (int i = 0; i < projectJsonArray.size(); i++) {
            	JSONObject projectJson = (JSONObject) projectJsonArray.get(i);
            	isValueTypeValid &= isProjectJsonValueValid(projectJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
    		try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return isValueTypeValid;
	}
	
	private boolean isProjectJsonValueValid(JSONObject projectJson) {
		boolean isProjectJsonValid = true;
		if (!(projectJson.get(KEY_CREATE_TIME) instanceof Long) && !mInvalidValueType.contains(KEY_CREATE_TIME)) {
			mInvalidValueType.add(KEY_CREATE_TIME);
			isProjectJsonValid = false;
		}
		if (!(projectJson.get(KEY_ATTACH_MAX_SIZE) instanceof Long) && !mInvalidValueType.contains(KEY_ATTACH_MAX_SIZE)) {
			mInvalidValueType.add(KEY_ATTACH_MAX_SIZE);
			isProjectJsonValid = false;
		}
		if (!(projectJson.get(KEY_PRODUCT_OWNER) instanceof String) && !mInvalidValueType.contains(KEY_PRODUCT_OWNER)) {
			mInvalidValueType.add(KEY_PRODUCT_OWNER);
			isProjectJsonValid = false;
		}
		if (!(projectJson.get(KEY_NAME) instanceof String) && !mInvalidValueType.contains(KEY_NAME)) {
			mInvalidValueType.add(KEY_NAME);
			isProjectJsonValid = false;
		}
		if (!(projectJson.get(KEY_COMMENT) instanceof String) && !mInvalidValueType.contains(KEY_COMMENT)) {
			mInvalidValueType.add(KEY_COMMENT);
			isProjectJsonValid = false;
		}
		if (!(projectJson.get(KEY_DISPLAY_NAME) instanceof String) && !mInvalidValueType.contains(KEY_DISPLAY_NAME)) {
			mInvalidValueType.add(KEY_DISPLAY_NAME);
			isProjectJsonValid = false;
		}
		return isProjectJsonValid;
	}
	
	private boolean isProjectsExisting() {
		String KEY_OF_PROJECTS = "projects";
		boolean isProjectsExisting = false;
		JSONParser parser = new JSONParser();
		FileReader fileReader = null;
		try {
        	fileReader = new FileReader(mEzScrumFile);
            JSONObject wholeJson = (JSONObject) parser.parse(fileReader);
            fileReader.close();
            JSONArray projectJsonArray = (JSONArray) wholeJson.get(KEY_OF_PROJECTS);
            // clear invalid value type keys
            mInvalidValueType.clear();
        	ArrayList<ProjectObject> existingProjects = ProjectDAO.getInstance().getAllProjects();
        	ArrayList<ProjectObject> projects = JsonParser.parseProjects(projectJsonArray);
        	ArrayList<String> existingProjectNames = getProjectNames(existingProjects);
        	ArrayList<String> projectNames = getProjectNames(projects);
        	// clear project names
        	mProjectNames.clear();
        	// clear invalid project names
        	mInvalidProjectNames.clear();
        	mProjectNames.addAll(projectNames);
        	for (String projectName : projectNames) {
        		if (existingProjectNames.contains(projectName)) {
        			mInvalidProjectNames.add(projectName);
        			isProjectsExisting = true;
        		}
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
    		try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return isProjectsExisting;
	}
	
	private ArrayList<String> getProjectNames(ArrayList<ProjectObject> projects) {
		ArrayList<String> projectNames = new ArrayList<>();
		for (ProjectObject project : projects) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}
}
