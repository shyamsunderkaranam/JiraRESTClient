package com.jira.jirarestclient.config;
import com.jira.jirarestclient.entities.ConfigEntity;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.jira.jirarestclient.repository.ConfigRepo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
@Component
public class ConfigDAO {

	Logger logger = LoggerFactory.getLogger(ConfigDAO.class);

    @Value("${spring.jiraconfigfile}")
    private String configFileLocation;

    private static final String CONFIG_FILE_PATH = "../webapps/EnvironmentsDashboard/configs/configs.json";



    @Autowired
    ConfigRepo configRepo;

    private JSONObject allConfigs=null;

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

		JSONParser jsonParser = new JSONParser();
		JSONArray ja=null;
		Object obj = null;
		try (FileReader reader = new FileReader(configFilePath))
        {
            //Read JSON file
			obj =  jsonParser.parse(reader);;
			ja= (JSONArray)obj;
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
        	ja = new JSONArray();
        	ja.add( obj);
        } catch (Exception e) {
        	 e.printStackTrace();
        }
		
		return ja;
	}
	public JSONObject getJSONData(String configFilePath) {

        logger.info("Config file exists at "+configFilePath);
		JSONParser jsonParser = new JSONParser();
		JSONObject ja=null;
		try (FileReader reader = new FileReader(configFilePath))
        {
            //Read JSON file
			ja= (JSONObject) jsonParser.parse(reader);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //logger.info("Configs are "+ja);
		return ja;
	}

    public JSONObject getAllConfigs() {

        if(configRepo == null) {
            logger.warn("repository object is null");
            logger.info("Config file exists at "+getConfigFileLocation());
            if(getConfigFileLocation() == null) {
                setConfigFileLocation(CONFIG_FILE_PATH);
                logger.info("Config file path is "+getConfigFileLocation());
            }
            return getJSONData(getConfigFileLocation());
        }
        
        List<ConfigEntity> configs = configRepo.findAll();
        
        if(configs !=null) {
            allConfigs = new JSONObject();
            configs.stream()
                    .forEach(con -> {
                        JSONObject tmp = new JSONObject();
                        tmp.put(con.getConfigKey(), con.getConfigValue());
                        allConfigs.putAll(tmp);
                    });
            logger.info("All Configurations are: " + configs);
        }
        return allConfigs;
    }

    public String getConfigFileLocation(){
        return configFileLocation;
    }
	public void setConfigFileLocation(String configFileLocation){
        this.configFileLocation = configFileLocation;
    }


}
