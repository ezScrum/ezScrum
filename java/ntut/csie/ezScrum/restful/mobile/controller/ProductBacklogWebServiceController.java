package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.ProductBacklogWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

@Path("{projectName}/product-backlog/")
public class ProductBacklogWebServiceController {
	private ProductBacklogWebService pbws;
	
	/**
	 * 建立一個Story
	 * 需要Post Story JSON 字串
	 * http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/create?username={username}&password={password}
	 */
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createStory(@PathParam("projectName") String projectName,
						      @QueryParam("username") String username,
							  @QueryParam("password") String password,
							  String storyJson) {
		String storyCreateJsonString = "";
		InformationDecoder informationDecoder = new InformationDecoder();
		try {
			informationDecoder.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(informationDecoder.getDecodeUsername(),
											    informationDecoder.getDecodePwd(),
											    informationDecoder.getDecodeProjectName());
			
	        pbws.createStory(storyJson);
	        storyCreateJsonString = pbws.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: createStory, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: createStory, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}  catch (JSONException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
					"method: createStory, " +
					"exception: "+ e.toString());
            e.printStackTrace();
        }
		return storyCreateJsonString;
	}
	
	/**
	 * 取得專案底下所有Story Get
	 * http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/storylist?username={username}&password={password}
	 **/
	@GET
	@Path("storylist")
	@Produces(MediaType.APPLICATION_JSON)
	public String getProductBacklog(@QueryParam("username") String username,
										@QueryParam("password") String password,
										@PathParam("projectName") String projectName) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(
					decodeAccount.getDecodeUsername(),
					decodeAccount.getDecodePwd(), 
					decodeAccount.getDecodeProjectName());
			pbws.getStories();
			jsonString = pbws.getRESTFulResponseString();
		} catch (LogonException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: getProductBacklogList, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: decode, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
	
	/**
	 * update Story
	 * 需要Post Story JSON 字串
	 * http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/update?username={username}&password={password}
	 */
	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateStory(@PathParam("projectName") String projectName,
						      @QueryParam("username") String username,
							  @QueryParam("password") String password,
							  String storyJson) {
		String storyUpdateJsonString = "";
		InformationDecoder informationDecoder = new InformationDecoder();
		try {
			informationDecoder.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(informationDecoder.getDecodeUsername(),
											    informationDecoder.getDecodePwd(),
											    informationDecoder.getDecodeProjectName());
			
	        pbws.updateStory(storyJson);
	        storyUpdateJsonString = pbws.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: updateStory, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: updateStory, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}
		return storyUpdateJsonString;
	}
	
	/**	刪除Story	DELETE
	 * 	http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/storylist/{storyID}?username={username}&password={password}
	 * **/
	@DELETE
	@Path("storylist/{storyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String deleteStory(@QueryParam("username") String username,
							  @QueryParam("password") String password,
							  @PathParam("projectName") String projectName,
							  @PathParam("storyId") long storyId){
		String responseString = "";
		try{
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(
					decodeAccount.getDecodeUsername(),
					decodeAccount.getDecodePwd(), 
					decodeAccount.getDecodeProjectName());
			pbws.deleteStory(storyId);
			responseString += pbws.getRESTFulResponseString();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: deleteStory, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: deleteStory, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}
		return responseString;
	}
	
	/**
	 * 取得專案底下指定Story Get
	 * http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/storylist/{storyID}?username={username}&password={password}
	 **/
	@GET
	@Path("storylist/{storyId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStory(@QueryParam("username") String username, 
								@QueryParam("password") String password, 
								@PathParam("projectName") String projectName, 
								@PathParam("storyId") long storyId) {
		String jsonString = "";
		try {
			InformationDecoder decodeAccount = new InformationDecoder();
			decodeAccount.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(
					decodeAccount.getDecodeUsername(),
					decodeAccount.getDecodePwd(), 
					decodeAccount.getDecodeProjectName());
			pbws.getStory(storyId);
			jsonString = pbws.getRESTFulResponseString();
		} catch (LogonException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: getProductBacklogList, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: decode, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
	
	/**
	 * 取得專案底下所有的tag
	 * http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/taglist?username={username}&password={password}
	 */
	@GET
	@Path("taglist")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTagList(@PathParam("projectName") String projectName,
							 @QueryParam("username") String username,
							 @QueryParam("password") String password) {
		String jsonString = "";
		InformationDecoder informationDecoder = new InformationDecoder();
		try {
			informationDecoder.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(informationDecoder.getDecodeUsername(),
													 informationDecoder.getDecodePwd(),
													 informationDecoder.getDecodeProjectName());
			pbws.getAllTags();
			jsonString = pbws.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: decode, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: getTagList, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}
	
	/**
	 * 取得專案底下Story History
	 * http://IP:8080/ezScrum/web-service/{projectName}/product-backlog/{storyID}/history?username={username}&password={password}
	 */
	@GET
	@Path("{storyId}/history")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStoryHistory(@PathParam("projectName") String projectName,
								  @QueryParam("username") String username,
								  @QueryParam("password") String password,
								  @PathParam("storyId") long storyId) {
		String storyHistoryJsonString = "";
		InformationDecoder informationDecoder = new InformationDecoder();
		try {
			informationDecoder.decode(username, password, projectName);
			pbws = new ProductBacklogWebService(informationDecoder.getDecodeUsername(),
													 informationDecoder.getDecodePwd(),
													 informationDecoder.getDecodeProjectName());
			pbws.getStoryHistory(storyId);
			storyHistoryJsonString = pbws.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: decode, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: ProductBacklogWebServiceController, " +
								"method: getStoryHistory, " +
								"exception: "+ e.toString());
			e.printStackTrace();
		}
		return storyHistoryJsonString;
	}
}
