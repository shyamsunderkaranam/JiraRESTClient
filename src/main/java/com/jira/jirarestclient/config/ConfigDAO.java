package com.jira.jirarestclient.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
/*import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigDAO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public ConfigDAO() {
	}

	public void putConfigData(JSONArray data, Path filePath) {

		try 
        {
			if(Files.exists(filePath)) {
				  FileChannel.open(filePath, StandardOpenOption.WRITE).truncate(0).close();
			  }
			  Files.write(filePath, data.toString().getBytes());
			  
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		
	}
	
	public void putConfigData(JSONObject data, Path filePath) {

		try 
        {
			if(Files.exists(filePath)) {
				  FileChannel.open(filePath, StandardOpenOption.WRITE).truncate(0).close();
			  }
			  Files.write(filePath, data.toString().getBytes());
			  
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		
	}


	public JSONArray getConfigData(String configFilePath) {

		JSONArray ja=null;
		Object obj = null;
		try
        {
		JSONParser jsonParser = new JSONParser(new FileReader(configFilePath));
		
            //Read JSON file
			obj =  jsonParser.parse();
			ja= (JSONArray)obj;
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
        	ja = new JSONArray();
        	ja.put( obj);
        } catch (Exception e) {
        	 e.printStackTrace();
        }
		
		return ja;
	}
	public JSONObject getJSONData(String configFilePath) {

		JSONObject ja=null;
		Map<String,String> tmp=null;
		try {
			FileReader reader = new FileReader(configFilePath);
			JSONParser jsonParser = new JSONParser(reader);
		    //Read JSON file
			tmp= (HashMap) jsonParser.parse();
			ja = new JSONObject(tmp);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return ja;
	}
	


}
