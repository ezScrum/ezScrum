package ntut.csie.ezScrum.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import ntut.csie.jcis.core.ISystemPropertyEnum;

public class InitialConfigManager {
	private String configeFilePath = "configurations/InitialPluginConfig.properties";
	private String ezScrumConfigPath = "configurations/SystemVersion.properties";
	private Properties props = null;
	
	public InitialConfigManager(String webRoot){
		this.configeFilePath = webRoot+this.configeFilePath;
		this.ezScrumConfigPath = webRoot+this.ezScrumConfigPath;
		this.props = new Properties();
		this.loadConfig();
	}
	
	/**
	 * 儲存Initial properties
	 */
	public void saveConfig()
	{
		try
		{
			FileOutputStream outputFile = new FileOutputStream(configeFilePath);
			this.props.store(outputFile, "JCIS Initail Config");
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 讀取Initial properties
	 */
	public void loadConfig(){
		try {
			FileInputStream in = new FileInputStream(this.configeFilePath);
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			this.props.load(reader);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根據key取得相對應的property value
	 * @param key
	 * @return
	 */
	public String getConfig(String key)
	{
		return props.getProperty(key);
	}
	
	/**
	 * 設置JavaSpace host的ip
	 * @param value
	 */
	public void setJavaspacePath(String value){
		this.props.setProperty(ISystemPropertyEnum.JAVASPACES_PATH, value);
	}
	
	/**
	 * 設置JCIS host的ip
	 * @param value
	 */
	public void setWebRootAddess(String value){
		this.props.setProperty(ISystemPropertyEnum.WEB_ROOT_ADDRESS, value);
	}
	
	//jake, mantis task 616, jcis 632
	public void setScheduler(String value){
		this.props.setProperty(ISystemPropertyEnum.SCHEDULER, value);
		
	}
	
	//jake, mantis task 616, jcis 632
	public void setSnapshotPath(String value){
		this.props.setProperty(ISystemPropertyEnum.SNAPSHOT_PATH, value);
	}
	
	//shchang, 
	public void setVMMSIPAddress(String value){
		this.props.setProperty(ISystemPropertyEnum.VMMS_IPADDRESS, value);
	}
	
	//shchang
	public void setUseVirtualEnvironment(String value){
		this.props.setProperty(ISystemPropertyEnum.USE_VIRTUAL_ENVIRONMENT, value);
	}
	
	public void ezScrumConfigLoad(){
		try {
			FileInputStream in = new FileInputStream(this.ezScrumConfigPath);
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			this.props.load(reader);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
