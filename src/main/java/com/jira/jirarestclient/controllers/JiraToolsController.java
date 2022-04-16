package com.jira.jirarestclient.controllers;

import com.jira.jirarestclient.service.*;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JiraToolsController {

	@Autowired
	JiraRestAPIService jiraRestAPIService;

	@CrossOrigin(allowedHeaders = "Access-Control-Allow-Origin")
	@RequestMapping("/jaiganesh")
	public ResponseEntity<String> testMethod(){

		return ResponseEntity.ok("JAI GANESH");
	}

	@CrossOrigin(allowedHeaders = "Access-Control-Allow-Origin")
	@RequestMapping(value = {"/ticketsProjectWise"}, method = RequestMethod.GET)
	public ResponseEntity<List<JSONObject>> getProjectJqlData() {
		
		List<JSONObject> tempList=  jiraRestAPIService.getProjectJqlData();
		
		return ResponseEntity.ok(tempList);
	}

	

}
