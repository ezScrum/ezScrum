package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.ReleasePlanWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.jcis.account.core.LogonException;

@Path("{projectID}/release-plan/")
public class ReleasePlanWebServiceController {
	private ReleasePlanWebService mReleasePlanWebService;
	/**
	 * 取得專案底下所有Story Get
	 * http://IP:8080/ezScrum/web-service/{projectID}/product-backlog/storylist?userName={userName}&password={password}
	 **/
	@GET
	@Path("{releaseID}/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReleasePlan(@QueryParam("userName") String username,
								 @QueryParam("password") String password,
								 @PathParam("projectID") String projectID, 
								 @PathParam("releaseID") String releaseID) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectID);
			mReleasePlanWebService = new ReleasePlanWebService(decodeAccount.getDecodeUserName(), decodeAccount.getDecodePwd(), projectID);
			jsonString = mReleasePlanWebService.getReleasePlan(releaseID);
		} catch (LogonException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: decode, " +
								"exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
}
