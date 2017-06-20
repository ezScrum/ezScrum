package ntut.csie.ezScru.web.microservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ServiceConfiguration {
	private JSONObject SERVICE_DATA;
	
	public ServiceConfiguration(String serviceName)throws IOException{

		FileReader fr = new FileReader("./Service.json");
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null){
			sb.append(line +"\n");
		}

		try{
			JSONObject json = new JSONObject(sb.toString());
			SERVICE_DATA = json.getJSONObject(serviceName);
		}catch(JSONException e){
			System.out.println(e);
		}
	}
	
	public String getServiceUrl() throws JSONException{
			return SERVICE_DATA.getString("URL");
	}
	
	public String getServicePort() throws JSONException{
		return SERVICE_DATA.getString("Port");
	}
}
