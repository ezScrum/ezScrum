package ntut.csie.ezScrum.pluginLoader;


import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;
import ntut.csie.protocal.Action;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.PluginUI;
import ntut.csie.ui.protocol.UIConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PluginWrapperTest {
	private final String mPluginTestDataPath = "./TestData/PluginData/";
	private final String mPluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private final String mPluginName = "redminePlugin.war";
	private Map<String,String> mPluginWrapperMapList;
	private PluginWrapper mPluginWrapper;
	private String mPluginDirPath;
	private Configuration mConfig  = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		addPluginToWorkspace();
		createPluginWrapperMapList();
		
		mPluginWrapper = new PluginWrapper(mPluginDirPath, mPluginWrapperMapList);
	}

	@After
	public void tearDown() throws Exception {
		deletePluginFolder();
		mPluginWrapper = null;
		mPluginWrapperMapList = null;
		mConfig = null;
	}
	
	/**
	 * 測試 Action 的 Url Name id是否正確
	 */
	@Test
	public void testGetPluginWithAction(){
		//	Action
		try {
			Action action = (Action)mPluginWrapper.getPlugin("Action");
			assertEquals( "redmine", action.getUrlName() );
		} catch (InstantiationException e) {
			System.out.println( "Class:PluginWrapperTest.java, method:testGetPlugin, exception:InstantiationException, " + e.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println( "Class:PluginWrapperTest.java, method:testGetPlugin, exception:IllegalAccessException, " + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 測試 UIConfig 的plugin id是否正確
	 */
	@Test
	public void testGetPluginWithUIConfig(){
		//	UIConfig
		String correctPluginID = mPluginName.replace(".war", "");
		try {
			UIConfig uiConfig = (UIConfig)mPluginWrapper.getPlugin("UIConfig");
			List<EzScrumUI> ezScrumUIList = new ArrayList<EzScrumUI>();
			uiConfig.setEzScrumUIList( ezScrumUIList );
			
			for( EzScrumUI ezScrumUI : ezScrumUIList ){
				if( ezScrumUI instanceof PluginUI ){
					String testPluginID = ((PluginUI) ezScrumUI).getPluginID();
					assertEquals( correctPluginID, testPluginID);
				}
			}
		} catch (InstantiationException e) {
			System.out.println( "Class:PluginWrapperTest.java, method:testGetPlugin, exception:InstantiationException, " + e.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println( "Class:PluginWrapperTest.java, method:testGetPlugin, exception:IllegalAccessException, " + e.toString());
			e.printStackTrace();
		}
	}
	
	private void addPluginToWorkspace() {
		String outsidePluginPath = mPluginTestDataPath + mPluginName;
		PluginManager pluginManager = new PluginManager();
		pluginManager.addPlugin( outsidePluginPath );
		pluginManager = null;
	}
	
	private void createPluginWrapperMapList() {
		mPluginWrapperMapList = new HashMap<String, String>();
		mPluginWrapperMapList.put( "UIConfig", "plugin.redmine.protocol.PluginImp" );
		mPluginWrapperMapList.put( "Action", "plugin.redmine.protocol.RedmineAction" );
		mPluginDirPath = mPluginWorkspacePath + mPluginName.replace(".war", "");
	}
	
	private void deletePluginFolder(){
		String folderPath = mPluginWorkspacePath + mPluginName.replace(".war", "");
		File file = new File( folderPath );
		PluginMockDataHelper.isMockFileExisted( file );
		file = null;
	}
}
