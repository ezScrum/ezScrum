package ntut.csie.ezScrum.plugin.util;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;
import ntut.csie.protocal.PluginConfig;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class PluginConfigManagerTest {
	
	private final String folderNamePath = "./WebContent/Workspace";
	private final String projectName = "/PluginForTest";
	private final String metadataFolderName = "/_metadata";
	private final String configFileName = "/pluginConfig.conf";
	private final String pluginID = "plugin";
	private PluginConfigManager mPluginConfigManager;
	private String mPluginConfigfileContent;

	@Before
	public void setUp() throws Exception {
		PluginMockDataHelper.createResourcePropertyFile();
		mPluginConfigManager  = new PluginConfigManager( projectName );
		
		//	新增專案資料夾
		mPluginConfigfileContent = PluginMockDataHelper.createPluginConfigFile();
	}

	@Test
	public void tearDown() throws Exception {
		mPluginConfigManager = null;
		
		//	刪除檔案
		PluginMockDataHelper.deleteResourcePropertyFile();
		this.deletePluginConfigMockFile();
	}
	
	/**
	 * 測試專案有啟用的plugin是否有記錄在pluginConfig.conf
	 */
	@Test
	public void testGetAvailablePluginConfigByPluginId(){
		PluginConfig pluginConfig;
		
		//	測試在專案底下有啟用的plugin id
		pluginConfig = mPluginConfigManager.getAvailablePluginConfigByPluginId( pluginID+"1" );
		assertEquals( pluginID+"1", pluginConfig.getId() );
		assertEquals( true, pluginConfig.isAvailable() );
		
		//	測試在專案底下有啟用過，後來在取消的plugin id
		pluginConfig = mPluginConfigManager.getAvailablePluginConfigByPluginId( pluginID+"2" );
		assertNull( pluginConfig );
		
		//	測試在專案底下沒有啟用的plugin id
		pluginConfig = mPluginConfigManager.getAvailablePluginConfigByPluginId("not existed");
		assertNull( pluginConfig );
	}
	
	/**
	 * 測試移除 plugin 後，pluginConfig.conf 的 plugin id 記錄是否會被刪除
	 */
	@Test
	public void testRemoveConfigUIByPluginUIId(){
		PluginConfig pluginConfig;
		
		mPluginConfigManager.removeConfigUIByPluginUIId( pluginID+"2" );
		pluginConfig = mPluginConfigManager.getAvailablePluginConfigByPluginId( pluginID+"2" );
		assertNull( pluginConfig );
	}
	
	/**
	 * 測試更改pluginConfig.conf
	 */
	@Test
	public void testReplaceFileContent(){
		String content = this.createReplaceFileContentMockData();
		PluginConfig pluginConfig;
		
		//	pluginConfig.conf檔案存在
		mPluginConfigManager.replaceFileContent( content );
		pluginConfig = mPluginConfigManager.getAvailablePluginConfigByPluginId( pluginID );
		assertEquals( pluginID, pluginConfig.getId() );
		assertEquals( true, pluginConfig.isAvailable() );
		
		//	pluginConfig.conf檔案不存在		
		String configFileNamePath = folderNamePath + projectName + metadataFolderName + configFileName;
		File configFile = new File(configFileNamePath);
		if( configFile.exists() ){
			configFile.delete();
		}
		mPluginConfigManager.replaceFileContent( content );
		pluginConfig = mPluginConfigManager.getAvailablePluginConfigByPluginId( pluginID );
		assertEquals( pluginID, pluginConfig.getId() );
		assertEquals( true, pluginConfig.isAvailable() );
	}
	
	/**
	 * 測試pluginConfig.conf檔案內容是否正確
	 */
	@Test
	public void testReadFileContent(){
		String testContent = mPluginConfigManager.readFileContent();
		String correctContent = this.mPluginConfigfileContent;
		assertEquals(correctContent, testContent);
	}
	
	/**
	 * 測試 pluginConfig.conf 的資料轉成 PluginConfig 的數量
	 */
	@Test
	public void testGetPluginConfigList(){
		try {
			Method getPluginConfigList = mPluginConfigManager.getClass().getDeclaredMethod( "getPluginConfigList" );
			getPluginConfigList.setAccessible(true);
			List<PluginConfig> pluginConfigList = (List<PluginConfig>) getPluginConfigList.invoke(mPluginConfigManager);
			getPluginConfigList.setAccessible(false);
			assertEquals( 2, pluginConfigList.size() );
		} catch (IllegalAccessException e) {
			System.out.println( "Class:PluginConfigManagerTest.java, method:testGetPluginConfigList( PluginConfigManager.class.newInstance() ), exception:IllegalAccessException, " + e.toString());
			e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println( "Class:PluginConfigManagerTest.java, method:testGetPluginConfigList( getClass().getDeclaredMethod() ), exception:SecurityException, " + e.toString());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println( "Class:PluginConfigManagerTest.java, method:testGetPluginConfigList( getClass().getDeclaredMethod() ), exception:NoSuchMethodException, " + e.toString());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println( "Class:PluginConfigManagerTest.java, method:testGetPluginConfigList( getPluginConfigList.invoke(pluginConfigManager)), exception:IllegalArgumentException, " + e.toString());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.out.println( "Class:PluginConfigManagerTest.java, method:testGetPluginConfigList( getPluginConfigList.invoke(pluginConfigManager)), exception:InvocationTargetException, " + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 製造取代pluginConfig.conf的資料
	 * @return
	 */
	private String createReplaceFileContentMockData() {
		String content = "";
		JSONArray pluginConfigJSONArr;
		JSONObject pluginProperty;
		
		pluginConfigJSONArr = new JSONArray();
		pluginProperty = new JSONObject();
		try {
			pluginProperty.put("id", pluginID);
			pluginProperty.put("available", "true");
			pluginConfigJSONArr.put(pluginProperty);
			content = pluginConfigJSONArr.toString();
		} catch (JSONException e) {
			System.out.println( "Class:PluginConfigManagerTest.java, method:testReplaceFileContent, exception:JSONException, " + e.toString());
			e.printStackTrace();
		}
		return content;
	}
	
	private void deletePluginConfigMockFile(){
		String folderPath = folderNamePath + projectName;
		File file = new File( folderPath );
		PluginMockDataHelper.isMockFileExisted( file );
		file = null;
	}
	
}
