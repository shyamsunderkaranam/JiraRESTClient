package com.jira.jirarestclient.controllers;

import com.jira.jirarestclient.service.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



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
	public ResponseEntity<JSONArray> getProjectJqlData() {
		
		JSONArray tempList=  jiraRestAPIService.getProjectJqlData();
		
		return ResponseEntity.ok(tempList);
	}

	

}
