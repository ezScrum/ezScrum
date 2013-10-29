package ntut.csie.ezScrum.plugin.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import ntut.csie.ezScrum.plugin.util.PluginModifier;

import org.junit.Test;

public class PluginModifierTest extends TestCase{
	private String pluginPath = "plugin/redmine";
	private String testFilePath = "./ImportPluginJSList.jsp";
	private String importJSPath = "<jsp:include page=\"../pluginWorkspace/"+pluginPath+"/import.jsp\"/>";
	private PluginModifier pluginModifier;
	
	public void setUp()throws Exception {
		createMockFile();//建立測試檔案
		pluginModifier = new PluginModifier();
		pluginModifier.setTestFilePath( testFilePath );
	}
	
	public void tearDown(){
		pluginModifier = null;
		removeMockFile();//刪除測試檔案
	}
	
	@Test
	public void testAddPluginImportPath() throws Exception {
		try {
			pluginModifier.addPluginImportPath(pluginPath);
		} catch (Exception e) {
			throw e;
		}
		//將舊的內容讀出
		try{
			FileInputStream fstream = new FileInputStream( testFilePath );
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			boolean isImportJSPathExisted = false;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if( strLine.equals( importJSPath ) ){
					isImportJSPathExisted = true;
					break;
				}
			}
			//importJSPath is added
			assertEquals( true, isImportJSPathExisted );
			
			//Close resource
			br.close();
			in.close();
			fstream.close();
		}catch( Exception ex){
			throw ex;
		}
	}
	
	@Test
	public void testAddDuplicatedPluginImportPath() throws Exception {
		try {
			pluginModifier.addPluginImportPath(pluginPath);
			pluginModifier.addPluginImportPath(pluginPath);
		} catch (Exception e) {
			assertEquals( "plugin is existed", e.getMessage() );
		}
	}
	
	@Test
	public void testRemovePluginImportPath() throws Exception {
		//add import plugin path and remove
		try {
			pluginModifier.addPluginImportPath(pluginPath);
			pluginModifier.removePluginImportPath(pluginPath);
		} catch (Exception e) {
			throw e;
		}
		//將舊的內容讀出
		try{
			FileInputStream fstream = new FileInputStream( testFilePath );
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			boolean isImportJSPathExisted = false;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if( strLine.equals( importJSPath ) ){
					isImportJSPathExisted = true;
					break;
				}
			}
			//importJSPath is removed
			assertEquals( false, isImportJSPathExisted );
			
			//Close resource
			br.close();
			in.close();
			fstream.close();
		}catch( Exception ex){
			throw ex;
		}
	}
	
	@Test
	public void testRemoveNoExistPluginImportPath() throws Exception {
		try {
			pluginModifier.removePluginImportPath(pluginPath);
			pluginModifier.removePluginImportPath(pluginPath);
		} catch (Exception e) {
			assertEquals( "plugin is already removed", e.getMessage() );
		}
	}
	
	private void createMockFile() throws Exception{
        try{
	    // Create file 
            FileWriter fstream = new FileWriter( testFilePath );
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>");
			out.newLine();
			out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
			out.newLine();
			out.write("<html>");
			out.newLine();
			out.write("<head>");
			out.newLine();
			out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			out.newLine();
			out.write("<!--Caution:This file(javascript plugin list) is only modified by program  -->");
			out.newLine();
			out.write("</head>");
			out.newLine();
			out.write("<body>");
			out.newLine();
			out.write("</body>");
			out.newLine();
			out.write("</html>");
			out.newLine();			  
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			  throw e;
		}
	}

	private void removeMockFile(){
		File file = new File( testFilePath );
		if( file.exists() ){
			file.delete();
		}
	}
	

}
