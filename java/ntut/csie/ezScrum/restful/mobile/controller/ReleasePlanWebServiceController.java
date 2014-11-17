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

@Path("{projectID}/release-plan/")
public class ReleasePlanWebServiceController {
	private ReleasePlanWebService mReleasePlanWebService;
	/**
	 * 取release底下所有Story Get
	 * http://IP:8080/ezScrum/web-service/{projectID}/release-plan/{releaseID}/all?userName={userName}&password={password}
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
	 * http://IP:8080/ezScrum/web-service/{projectID}/release-plan/all?userName={userName}&password={password}
	 **/
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllReleasePlan(@QueryParam("userName") String username,
								    @QueryParam("password") String password,
								    @PathParam("projectID") String projectID) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectID);
			mReleasePlanWebService = new ReleasePlanWebService(decodeAccount.getDecodeUserName(), decodeAccount.getDecodePwd(), projectID);
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
	 * http://IP:8080/ezScrum/web-service/{projectID}/release-plan/all/all?userName={userName}&password={password}
	 **/
	@GET
	@Path("all/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllReleasePlanWithAllItem(@QueryParam("userName") String username,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectID) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectID);
			mReleasePlanWebService = new ReleasePlanWebService(decodeAccount.getDecodeUserName(), decodeAccount.getDecodePwd(), projectID);
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
