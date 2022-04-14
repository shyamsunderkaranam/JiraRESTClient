package com.jira.jirarestclient.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jira.jirarestclient.config.ConfigDAO;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

public class JiraRestAPIService {

	private String loginId = null;
	private String tk = null;
	private String jira_url = "";
	private String jql= null;
	ConfigDAO config;
	private final String CONFIG_FILE_PATH="configs/configs.json"; 
	private JSONArray fields_needed = null;
	public JiraRestAPIService() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		//https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-search/#api-rest-api-3-search-post
		//https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-search/#api-rest-api-3-search-get
		JiraRestAPIService jiraRestAPIService = new JiraRestAPIService();
		System.out.println( jiraRestAPIService.getProjectJqlData());
	}
	public JSONObject getProjectJqlData() {
		
		config = new ConfigDAO();
		JSONObject allConfigs = config.getJSONData(CONFIG_FILE_PATH);
		if(allConfigs != null){

            //logger.info("Found configurations");
            if(allConfigs.get("jira_url")!=null) {
                //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
            	jira_url = allConfigs.get("jira_url").toString();
            	System.out.println(jira_url);
                
            }
            if(allConfigs.get("jira_user")!=null) {
                //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
            	loginId = allConfigs.get("jira_user").toString();
            	System.out.println(loginId);
                
            }
            if(allConfigs.get("dont_tell")!=null) {
            	//GLUsdKCx9TZBJr8ISrtP9D2C
                //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
            	tk = allConfigs.get("dont_tell").toString();
            	System.out.println(tk);
                
            }
            if(allConfigs.get("jqlForProjects")!=null) {
                //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
            	jql = allConfigs.get("jqlForProjects").toString();
            	//jql = jql.replace("doublequote", "\"");
            	//jql="project = \"FIRST\"";
            	jql = jql.replace("doublequote", "\"");
            	System.out.println("JQL is: "+jql);
                
            }
            if(allConfigs.get("fieldsNeeded")!=null) {
                //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
            	fields_needed = new JSONArray(Arrays.asList(allConfigs.get("fieldsNeeded").toString().split(","))) ;
            	
            	System.out.println("fieldsNeeded are:  "+fields_needed);
                
            }
		}
		return testMethod4();
		
	}
	public static void testMethod1() {
		// TODO Auto-generated method stub
		try {

			String uid="Testatlassian1@mailinator.com";

			String pwd="JqrTd7Cz3zsIsxxelvTI193C";
			String encoded = Base64.getEncoder().encodeToString((uid+":"+pwd).getBytes(StandardCharsets.UTF_8));
			//String encoded = uid+":"+pwd;
			URL url= new URL("https://demositetemporary.atlassian.net/rest/api/3/issue/FIRST-2");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic "+encoded);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			System.out.println("output is: ");
			String output;
			while((output = br.readLine()) != null) {
				System.out.println(output);
			}
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testMethod2() {
		try {
			// This code sample uses the  'Unirest' library:
			// http://unirest.io/java.html
			HttpResponse<JsonNode> response = Unirest.get("https://demositetemporary.atlassian.net/rest/api/3/search")
			  .basicAuth("Testatlassian1@mailinator.com", "JqrTd7Cz3zsIsxxelvTI193C")
			  .header("Accept", "application/json")
			  .queryString("jql", "project = \"FIRST\"")
			  .asJson();

			System.out.println(response.getBody());
		}
		catch(Exception e) {
			System.out.println("ERROR OCCURED");
			e.printStackTrace();
		}
		}
	public static void testMethod3() {
		try {
			// The payload definition using the Jackson library
			JsonNodeFactory jnf = JsonNodeFactory.instance;
			ObjectNode payload = jnf.objectNode();
			{
			  ArrayNode issueIds = payload.putArray("issueIds");
			  issueIds.add(10999);
			  issueIds.add(10001);
			  //issueIds.add(1004);
			  ArrayNode jqls = payload.putArray("jqls");
			  jqls.add("project = \"FIRST\"");
			  jqls.add("assignee IN (currentUser())");
			  jqls.add("statusCategory in (\"To Do\", \"In Progress\") ORDER BY created DESC");
			}

			// Connect Jackson ObjectMapper to Unirest
			Unirest.setObjectMapper(new ObjectMapper() {
			   private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
			           = new com.fasterxml.jackson.databind.ObjectMapper();

			   public <T> T readValue(String value, Class<T> valueType) {
			       try {
			           return jacksonObjectMapper.readValue(value, valueType);
			       } catch (IOException e) {
			           throw new RuntimeException(e);
			       }
			   }

			   public String writeValue(Object value) {
			       try {
			           return jacksonObjectMapper.writeValueAsString(value);
			       } catch (JsonProcessingException e) {
			           throw new RuntimeException(e);
			       }
			   }
			});

			// This code sample uses the  'Unirest' library:
			// http://unirest.io/java.html
			HttpResponse<JsonNode> response = Unirest.post("https://demositetemporary.atlassian.net/rest/api/3/jql/match")
					  .basicAuth("Testatlassian1@mailinator.com", "JqrTd7Cz3zsIsxxelvTI193C")
			  .header("Accept", "application/json")
			  .header("Content-Type", "application/json")
			  .body(payload)
			  .asJson();

			

			System.out.println(response.getBody());
		}
		catch(Exception e) {
			System.out.println("ERROR OCCURED");
			e.printStackTrace();
		}
		}
	
	public  JSONObject testMethod4() {
		JSONObject resp=null;
		try {
			// The payload definition using the Jackson library
			JsonNodeFactory jnf = JsonNodeFactory.instance;
			ObjectNode payload = jnf.objectNode();
			{
			  /*ArrayNode expand = payload.putArray("expand");
			  expand.add("names");
			  expand.add("schema");*/
			  //expand.add("operations");
			  payload.put("jql", jql);
			  payload.put("maxResults", 1000);
			  payload.put("fieldsByKeys", false);
			  ArrayNode fields = payload.putArray("fields");
			  fields_needed
			  .forEach(fieldNeeded ->{
				  fields.add(fieldNeeded.toString());
			  });
			  /*fields.add("key");
			  fields.add("created");
			  fields.add("summary");
			  fields.add("updated");
			  fields.add("status");
			  fields.add("customfield_10030");
			  fields.add("customfield_10029");
			  fields.add("customfield_10031");*/
			  
			  payload.put("startAt", 0);
			}

			// Connect Jackson ObjectMapper to Unirest
			Unirest.setObjectMapper(new ObjectMapper() {
			   private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
			           = new com.fasterxml.jackson.databind.ObjectMapper();

			   public <T> T readValue(String value, Class<T> valueType) {
			       try {
			           return jacksonObjectMapper.readValue(value, valueType);
			       } catch (IOException e) {
			           throw new RuntimeException(e);
			       }
			   }

			   public String writeValue(Object value) {
			       try {
			           return jacksonObjectMapper.writeValueAsString(value);
			       } catch (JsonProcessingException e) {
			           throw new RuntimeException(e);
			       }
			   }
			});

			
			HttpResponse<String> response = Unirest.post(jira_url)
			  .basicAuth(loginId, tk)
			  .header("Accept", "application/json")
			  .header("Content-Type", "application/json")
			  .body(payload)
			  .asString();

			
			System.out.println("Response is ");
			System.out.println(response.getBody());
			 resp = new JSONObject(response.getBody());
			 return resp;
			
		}
		catch(Exception e) {
			System.out.println("ERROR OCCURED");
			
			e.printStackTrace();
			return resp;
		}
		
		}
}
