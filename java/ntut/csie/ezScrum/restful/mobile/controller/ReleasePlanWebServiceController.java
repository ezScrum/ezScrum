package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.restful.mobile.service.ReleasePlanWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.jcis.account.core.LogonException;

@Path("{projectName}/release-plan/")
public class ReleasePlanWebServiceController {
	private ReleasePlanWebService mReleasePlanWebService;
	/**
	 * 取release底下所有Story Get
	 * http://IP:8080/ezScrum/web-service/{projectName}/release-plan/{releaseId}/all?username={userName}&password={password}
	 **/
	@GET
	@Path("{releaseId}/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReleasePlan(@QueryParam("username") String username,
								 @QueryParam("password") String password,
								 @PathParam("projectName") String projectName, 
								 @PathParam("releaseId") String releaseId) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectName);
			mReleasePlanWebService = new ReleasePlanWebService(decodeAccount.getDecodeUserName(), decodeAccount.getDecodePwd(), projectName);
			jsonString = mReleasePlanWebService.getReleasePlan(releaseId);
		} catch (LogonException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
			Response.status(410).entity("Parameter error.").build();
		} catch (IOException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: decode, " +
								"exception: " + e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
	
	/**
	 * 取得專案底下所有ReleasePlan Get
	 * http://IP:8080/ezScrum/web-service/{projectName}/release-plan/all?username={userName}&password={password}
	 **/
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllReleasePlan(@QueryParam("username") String username,
								    @QueryParam("password") String password,
								    @PathParam("projectName") String projectName) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectName);
			mReleasePlanWebService = new ReleasePlanWebService(decodeAccount.getDecodeUserName(), decodeAccount.getDecodePwd(), projectName);
			jsonString = mReleasePlanWebService.getAllReleasePlan();
		} catch (LogonException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getAllReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getAllReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
	
	/**
	 * 取得專案底下所有ReleasePlan並帶有所有item
	 * http://IP:8080/ezScrum/web-service/{projectName}/release-plan/all/all?username={userName}&password={password}
	 **/
	@GET
	@Path("all/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllReleasePlanWithAllItem(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectName);
			mReleasePlanWebService = new ReleasePlanWebService(decodeAccount.getDecodeUserName(), decodeAccount.getDecodePwd(), projectName);
			jsonString = mReleasePlanWebService.getAllReleasePlanWithAllItem();
		} catch (LogonException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getAllReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getAllReleasePlan, " +
								"exception: " + e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("class: ReleasePlanWebServiceController, " +
								"method: getAllReleasePlanWithAllItem, " +
								"exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
}
