package com.jira.jirarestclient.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jira.jirarestclient.config.ConfigDAO;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;

@Service
public class JiraRestAPIService {

	private String loginId = null;
	private String tk = null;
	private String jira_url = "";
	private String jql= null;
	
	ConfigDAO config;
	
	private final String CONFIG_FILE_PATH="configs/configs.json"; 
	private List<String> fields_needed = null;
	public JiraRestAPIService() {
		// TODO Auto-generated constructor stub
	}

	/*public static void main(String[] args) {
		//https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-search/#api-rest-api-3-search-post
		//https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-search/#api-rest-api-3-search-get
		JiraRestAPIService jiraRestAPIService = new JiraRestAPIService();
		System.out.println( jiraRestAPIService.getProjectJqlData());
	}*/
	public List<JSONObject> getProjectJqlData() {
		List<JSONObject> finalResult = new ArrayList<JSONObject>();
        try{
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
                    //System.out.println(loginId);
                    
                }
                if(allConfigs.get("dont_tell")!=null) {
                    //GLUsdKCx9TZBJr8ISrtP9D2C
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    tk = allConfigs.get("dont_tell").toString();
                    //System.out.println(tk);
                    
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
                    fields_needed = new ArrayList<>(Arrays.asList(allConfigs.get("fieldsNeeded").toString().split(","))) ;
                    
                    System.out.println("fieldsNeeded are:  "+fields_needed);
                    
                }
            }
        }
        catch(Exception e){
            JSONObject tmp = new JSONObject();
            tmp.put("ERROR","ERROR in getting the configuration");
            tmp.put("ERROR_DETAILS",e.getStackTrace().toString());
            finalResult.add(tmp);
        }
		
		JSONObject results1 = new JSONObject();
        try{
            results1 = jiraConnector();
            //finalResult.add(results1);
        }
        catch(Exception e){
            JSONObject tmp = new JSONObject();
            tmp.put("ERROR","ERROR in fetching Jira Data");
            tmp.put("ERROR_DETAILS",e.getStackTrace().toString());
            finalResult.add(tmp);
        }
		
        try{
		
            List<JSONObject> issues = (ArrayList<JSONObject>)(results1.get("issues"));
            finalResult = issues.stream()
            .map(issue -> {
                JSONObject tmp = new JSONObject();
                tmp.put("key", issue.get("key"));
                tmp.put("summary", ((JSONObject)issue.get("fields")).get("summary"));
                tmp.put("created", ((JSONObject)issue.get("fields")).get("created"));
                tmp.put("updated", ((JSONObject)issue.get("fields")).get("updated"));
                tmp.put("status", ((JSONObject)((JSONObject)issue.get("fields")).get("status")).get("name"));
                tmp.put("project_team", ((JSONObject)((JSONObject)issue.get("fields")).get("customfield_12903")).get("value"));
                
                String reolve_date =  (((JSONObject)issue.get("fields")).get("resolutiondate")!=null)?((JSONObject)issue.get("fields")).get("resolutiondate").toString():"Unresolved";
                tmp.put("resolution_date",reolve_date);
                List<JSONObject> tmp1 = ((List<JSONObject>)((JSONObject)issue.get("fields")).get("customfield_15201"));
                
                List<String> tiers = tmp1.stream()
                .map(tier -> {
                    return tier.get("value").toString();
                })
            
                .collect(Collectors.toList());
                
                tmp.put("Tier", String.join(",",tiers ));
                return tmp;
            
            })
            .collect(Collectors.toList());
		}
        catch(Exception e){
            JSONObject tmp = new JSONObject();
            tmp.put("ERROR","ERROR in Mapping Result");
            tmp.put("ERROR_DETAILS",e.getStackTrace().toString());
            finalResult.add(tmp);
        }
        
		
		return finalResult;
	}

	public  JSONObject jiraConnector(){
		JSONObject resp=new JSONObject();
		RequestBodyEntity requestBodyEntity = null;
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
			  //payload.put("fieldsByKeys", false);
			  ArrayNode fields = payload.putArray("fields");
			  fields_needed
			  .forEach(fieldNeeded ->{
				  fields.add(fieldNeeded.toString());
			  });
			  
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

            int totalissues=1000;
			for (int i=0; i<=totalissues; i++){
			requestBodyEntity = Unirest.post(jira_url)
				  //.basicAuth(loginId, tk)
				  .header("Authorization", "Bearer "+tk)
				  .header("Accept", "application/json")
				  .header("Content-Type", "application/json")
				  .body(payload) ;
			  //System.out.println(requestBodyEntity.asString().getBody());
			  HttpResponse<JsonNode> response = requestBodyEntity
					  							.asJson();

			
			/*System.out.println("Response is ");
			System.out.println(response.getBody());*/
			JSONParser parser=new JSONParser(); 
			 JSONObject temp = (JSONObject) parser.parse(response.getBody().toString());
             
             if(i==0) {resp.putAll(temp);
            }
            else if(!resp.isEmpty()){
                List<JSONObject> tempIssues1 = (ArrayList<JSONObject>)(temp.get("issues"));
                List<JSONObject> tempIssues2 = (ArrayList<JSONObject>)(resp.get("issues"));
                tempIssues2.addAll(tempIssues1);
                resp.put("issues", tempIssues2);

            }
             
             System.out.println("Response is OK");
             i += Integer.parseInt(temp.get("maxResults").toString());
             totalissues = Integer.parseInt(temp.get("total").toString());
             System.out.println("Index is "+i);
             System.out.println("Total issues are "+totalissues);
             payload.put("startAt", i);
            }
			 return resp;
			
		}
		catch(UnirestException e) {
			System.out.println("ERROR OCCURED");
			try {
				System.out.println(requestBodyEntity.asString().getBody());
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return resp;
		}
		catch(Exception e) {
			System.out.println("ERROR OCCURED");
			try {
				System.out.println(requestBodyEntity.asString().getBody());
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return resp;
		}
		
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

			

			System.out.println(response.getBody().toString());
		}
		catch(Exception e) {
			System.out.println("ERROR OCCURED");
			e.printStackTrace();
		}
		}
	
}
