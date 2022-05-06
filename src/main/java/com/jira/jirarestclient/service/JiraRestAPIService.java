package com.jira.jirarestclient.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JiraRestAPIService {

	private String loginId = null;
	private String tk = null;
	private String jira_url = "";
	private String jql= null;
    private int maxResults=1000;
	
	ConfigDAO config;
	
    Logger logger = LoggerFactory.getLogger(JiraRestAPIService.class);

	private final String CONFIG_FILE_PATH="configs/configs.json"; 
	private List<String> fields_needed = null;
	public JiraRestAPIService() {
		// TODO Auto-generated constructor stub
	}

	/*public static void main(String[] args) {
		//https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-search/#api-rest-api-3-search-post
		//https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-search/#api-rest-api-3-search-get
		JiraRestAPIService jiraRestAPIService = new JiraRestAPIService();
		logger.info( jiraRestAPIService.getProjectJqlData());
	}*/
	public List<JSONObject> getProjectJqlData() {
		List<JSONObject> finalResult = new ArrayList<JSONObject>();
        String formatOutput = "N";
        try{
            config = new ConfigDAO();
            //JSONObject allConfigs = config.getJSONData(CONFIG_FILE_PATH);
            JSONObject allConfigs = config.getAllConfigs();
            //logger.info("ALL Configurations are "+allConfigs);
            if(allConfigs != null){

                //logger.info("Found configurations");
                if(allConfigs.get("jira_url")!=null) {
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    jira_url = allConfigs.get("jira_url").toString();
                    logger.info(jira_url);
                    
                }
                if(allConfigs.get("jira_user")!=null) {
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    loginId = allConfigs.get("jira_user").toString();
                    //logger.info(loginId);
                    
                }
                if(allConfigs.get("dont_tell")!=null) {
                    //GLUsdKCx9TZBJr8ISrtP9D2C
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    tk = allConfigs.get("dont_tell").toString();
                    //logger.info(tk);
                    
                }
                if(allConfigs.get("jqlForProjects")!=null) {
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    jql = allConfigs.get("jqlForProjects").toString();
                    //jql = jql.replace("doublequote", "\"");
                    //jql="project = \"FIRST\"";
                    jql = jql.replace("doublequote", "\"");
                    logger.info("JQL is: "+jql);
                    
                }
                if(allConfigs.get("fieldsNeeded")!=null) {
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    fields_needed = new ArrayList<>(Arrays.asList(allConfigs.get("fieldsNeeded").toString().split(","))) ;
                    
                    logger.info("fieldsNeeded are:  "+fields_needed);
                    
                }
                if(allConfigs.get("maxResults")!=null) {
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    maxResults = Integer.parseInt(allConfigs.get("maxResults").toString()) ;
                    
                    logger.info("maxResults is:  "+maxResults);
                    
                }
                if(allConfigs.get("formatOutput")!=null) {
                    //logger.info("products Configures are "+allConfigs.get("SITBQUKProductsToFix"));
                    formatOutput = allConfigs.get("formatOutput").toString() ;
                    
                    logger.info("formatOutput is:  "+formatOutput);
                    
                }
            }
        }
        catch(Exception e){
            JSONObject tmp = new JSONObject();
            tmp.put("ERROR","ERROR in getting the configuration");
            tmp.put("ERROR_DETAILS",e.getMessage().toString());
            finalResult.add(tmp);
            e.printStackTrace();
            return finalResult;
        }
		
		JSONObject results1 = new JSONObject();
        try{
            results1 = jiraConnector();
            //logger.info(results1.toString());
            //finalResult.add(results1);
        }
        catch(Exception e){
            JSONObject tmp = new JSONObject();
            tmp.put("ERROR","ERROR in fetching Jira Data");
            tmp.put("ERROR_DETAILS",e.getMessage().toString());
            finalResult.add(tmp);
            e.printStackTrace();
            return finalResult;
        }
		
        try{
		
            
            if(formatOutput.equalsIgnoreCase("Y")){
                logger.info("Formatting the results now");
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
            else{
                logger.info("Not Formatting the results");
                finalResult.add(results1);
            }
		}
        catch(Exception e){
            JSONObject tmp = new JSONObject();
            tmp.put("ERROR","ERROR in Mapping Result");
            tmp.put("ACTUAL_RESULT",results1);
            tmp.put("ERROR_DETAILS",e.getMessage().toString());
            finalResult.add(tmp);
            logger.error("Issue in formatting the result");
            e.printStackTrace();
        }
        
		
		return finalResult;
	}

	public  JSONObject jiraConnector(){
		JSONObject resp=new JSONObject();
        logger.info("Connecting to Jira now ");
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
			  payload.put("maxResults", maxResults);
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
			  //logger.info(requestBodyEntity.asString().getBody());
			  HttpResponse<JsonNode> response = requestBodyEntity
					  							.asJson();

			
			/*logger.info("Response is ");
			logger.info(response.getBody());*/
			JSONParser parser=new JSONParser(); 
			 JSONObject temp = (JSONObject) parser.parse(response.getBody().toString());
             
             if(i==0) {
                 resp.putAll(temp);
            }
            else if(!resp.isEmpty()){
                List<JSONObject> tempIssues1 = (ArrayList<JSONObject>)(temp.get("issues"));
                List<JSONObject> tempIssues2 = (ArrayList<JSONObject>)(resp.get("issues"));
                tempIssues2.addAll(tempIssues1);
                resp.put("issues", tempIssues2);

            }
             
             logger.info("Response is OK");
             i += Integer.parseInt(temp.get("maxResults").toString());
             totalissues = Integer.parseInt(temp.get("total").toString());
             logger.info("Index is "+i);
             logger.info("Total issues are "+totalissues);
             logger.info("maxResults are "+maxResults);
             payload.put("startAt", i);
            }
			 return resp;
			
		}
		catch(UnirestException e) {
			logger.info("ERROR OCCURED UnirestException");
			try {
				logger.info(requestBodyEntity.asString().getBody());
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return resp;
		}
		catch(Exception e) {
			logger.info("ERROR OCCURED general Exception");
			try {
				logger.info(requestBodyEntity.asString().getBody());
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return resp;
		}
		
		}

	
}
