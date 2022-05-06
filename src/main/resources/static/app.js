var myApp = angular.module('ppsJiraProjectsClientApp',['ngClipboard','angular-loading-bar']);
myApp.controller("ppsJiraProjectsClientCtrl",function($scope,$http,ngClipboard,$window){
	$scope.currentTabActiveClass = ["is-active",""];
	$scope.tabs = [ {'tab_name':'Project wise data','style':'is-active','tab_area':'status'},
					{'tab_name':'Tier wise data','style':'','tab_area':'Tier'}];
	$scope.loggingEnabled = true;
	//$scope.hostAndPort = 'http://localhost:8080/jirarestclient';
    //$scope.hostAndPort = 'http://app2900.gha.kfplc.com:9080/EnvironmentsDashboard';
	//$scope.hostAndPort = 'http://lnxs0639.ghanp.kfplc.com:9052';
    $scope.hostAndPort = 'https://ccrd.kfplc.com/EnvironmentsDashboard';
	$scope.jiraUrl = 'https://jira.kfplc.com/issues/?jql=project = PPS AND status not in (Done, Rejected, Closed) AND ';
	$scope.uri = '/ticketsProjectWise';
	$scope.allOk = false;
	$scope.alt_text = 'Loading the data. Please wait..';
	$scope.resetALL = function(){
		$scope.totalView = false;
		$scope.allOk = false;
		$scope.alt_text = '';
	};

	$scope.navToJira = function(param1, param2, param3){
		$scope.resetALL();
		let url = $scope.jiraUrl+'"Project Team" = "'+param1+"\"";
		if($scope.loggingEnabled) console.log(param2);
		if(param2 === "total"){
			
		}
		else{
			url += ' AND '+param3+' = '+"\""+ param2+"\"";
		}
		if($scope.loggingEnabled) console.log(url);
		url=url.replace('+','%2B');
		url=url.replace('&','%26');
		/*let encodedUrl = encodeURI(url);
		if($scope.loggingEnabled) console.log(encodedUrl);
		$window.open(encodedUrl);*/
		$window.open(url);
	};

	
	$scope.getProjectwiseData = function(idx){
		
		$scope.resetALL();
		let tab_area=$scope.tabs[idx].tab_area;
		$scope.apiCallUrl = $scope.hostAndPort + $scope.uri;
		$scope.alt_text = 'Loading the data. Please wait..';
		//if($scope.loggingEnabled) console.log($scope.apiCallUrl);
 		$http.get($scope.apiCallUrl)
			.then(function successCallback(response) {
				$scope.jiraPPSData = response.data;
                if($scope.jiraPPSData.ERROR === undefined || $scope.jiraPPSData.ERROR === null){
					$scope.allOk = true;
					//$scope.jiraPPSData = angular.copy($scope.responseData);
					//if($scope.loggingEnabled) console.log($scope.jiraPPSData); 
					$scope.projects = $scope.getUniqVals($scope.jiraPPSData, "project_team");
					/*let tiers = $scope.getUniqVals($scope.jiraPPSData, "Tier");
					let statuses = $scope.getUniqVals($scope.jiraPPSData, "status");*/
					let column_data = $scope.getUniqVals($scope.jiraPPSData, tab_area);
					$scope.finalResult=[];
					for (let i=0; i< $scope.projects.length; i++){
						let total=0;
						let tmpObj = {};
						let tmpArray = [];
						let anotherTmpObj = {};
						anotherTmpObj.project_team = angular.copy($scope.projects[i].project_team);
						anotherTmpObj.total = angular.copy(total);
						anotherTmpObj.tab_area = angular.copy(tab_area);
						//tmpArray.push(anotherTmpObj);
						for (let j=0; j< column_data.length; j++){
							tmpObj = {};
							
							let count=0;
							for (let k=0; k< $scope.jiraPPSData.length; k++){
								if($scope.jiraPPSData[k].project_team === $scope.projects[i].project_team && $scope.jiraPPSData[k][tab_area] === column_data[j][tab_area]){
									count++;
									total++;
									
								}
								
							}
							
							tmpObj.columnValue = angular.copy(column_data[j][tab_area]);
							tmpObj.count = angular.copy(count);
							tmpArray.push(tmpObj);
						}
						
						anotherTmpObj.total = angular.copy(total);
						
						anotherTmpObj.columns = angular.copy(tmpArray);
						$scope.finalResult.push(anotherTmpObj);
						
					}
					
					$scope.columns = [];
					column_data.forEach(e => {
						let obj = {};
						obj.columnValue = e[tab_area];
						$scope.columns.push(obj);
					});

					//finalResult=angular.copy(tmpArray);
					if($scope.loggingEnabled) console.log($scope.finalResult);
					//if($scope.loggingEnabled) console.log(tiers);
					if($scope.loggingEnabled) console.log($scope.columns);
					$scope.tabs = $scope.tabs.map((elem, index) => { 
						let style="nav-link";
						if(index ==idx){
							style="nav-link active";
						}
						//console.log(style);
						elem.style = style;
						return elem;
					});
					if($scope.loggingEnabled) console.log($scope.tabs);
					$scope.alt_text = '';
				} else{
					$scope.allOk = false;
					$scope.alt_text = 'Something went wrong while fetching the data. Please contact support team OR Try after sometime.';
				}

			},function errorCallback(response) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$scope.allOk = false;
				$scope.alt_text = 'Something went wrong while fetching the data. Please contact support team OR Try after sometime.';
				$scope.errorInGettingData = 'Error occurred while fetching the data. Please try after sometime or contact support team';
				console.log("Some thing wrong");
				console.log(response);
			 }); 

	};

	
	$scope.getUniqVals = function(collection, keyname) {
		// we define our output and keys array;
		var output = [],
		  keys = [];

		// we utilize angular's foreach function
		// this takes in our original collection and an iterator function
		angular.forEach(collection, function(item) {
		  // we check to see whether our object exists
		  var key = item[keyname];
		  // if it's not already part of our keys array
		  if (keys.indexOf(key) === -1) {
			// add it to our keys array
			keys.push(key);
			// push this item to our final output array
			output.push(item);
		  }
		});
		// return our array which should be devoid of
		// any duplicates
		return output;
	 };
	 


	});
	