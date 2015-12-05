package ntut.csie.ezScrum.restful.dataMigration;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ExportJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;

@Path("dataMigration")
public class IntegratedRESTfulApi {
	@POST
	@Path("/projects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importProjectsJSON(String entity) {
		JSONObject importDataJSON = null;
		// 檢查JSON format
		try {
			importDataJSON = new JSONObject(entity);
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MEESSAGE, "");
		}
		// 檢查 Checksum
		
		// 檢查版本號

		///// 資料擷取 /////
		// Get Accounts
		JSONArray accountJSONArray = null;
		try {
			accountJSONArray = importDataJSON.getJSONArray(ExportJSONEnum.ACCOUNTS);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
