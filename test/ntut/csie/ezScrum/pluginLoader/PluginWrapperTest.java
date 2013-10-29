package ntut.csie.ezScrum.pluginLoader;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;
import ntut.csie.protocal.Action;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.PluginUI;
import ntut.csie.ui.protocol.UIConfig;

import org.junit.Test;

import junit.framework.TestCase;

public class PluginWrapperTest extends TestCase{
	private final String pluginTestDataPath = "./TestData/PluginData/";
	private final String pluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private final String pluginName = "redminePlugin.war";
	private Map<String,String> pluginWrapperMapList;
	private PluginWrapper pluginWrapper;
	private String pluginDirPath;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.addPluginToWorkspace();
		this.createPluginWrapperMapList();
		
		pluginWrapper = new PluginWrapper(pluginDirPath, pluginWrapperMapList);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.deletePluginFolder();
		pluginWrapper = null;
		pluginWrapperMapList = null;
	}
	
	/**
	 * 測試 Action 的 Url Name id是否正確
	 */
	@Test
	public void testGetPluginWithAction(){
		//	Action
		try {
			Action action = (Action)pluginWrapper.getPlugin("Action");
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
		String correctPluginID = pluginName.replace(".war", "");
		try {
			UIConfig uiConfig = (UIConfig)pluginWrapper.getPlugin("UIConfig");
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
		String outsidePluginPath = pluginTestDataPath + pluginName;
		PluginManager pluginManager = new PluginManager();
		pluginManager.addPlugin( outsidePluginPath );
		pluginManager = null;
	}
	
	private void createPluginWrapperMapList() {
		pluginWrapperMapList = new HashMap<String, String>();
		pluginWrapperMapList.put( "UIConfig", "plugin.redmine.protocol.PluginImp" );
		pluginWrapperMapList.put( "Action", "plugin.redmine.protocol.RedmineAction" );
		pluginDirPath = pluginWorkspacePath + pluginName.replace(".war", "");
	}
	
	private void deletePluginFolder(){
		String folderPath = pluginWorkspacePath + pluginName.replace(".war", "");
		File file = new File( folderPath );
		PluginMockDataHelper.isMockFileExisted( file );
		file = null;
	}
}
