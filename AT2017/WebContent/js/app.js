var app = angular.module('app', []);
app.controller('ctrl', ['$scope','$http','service',function ($scope, $http, service) {
	
	   
	
	$scope.getExisting = function(){
		service.getExisting()
		.success(function(data){
			$scope.agents=data;
			console.log($scope.agents);
		})
	}
	
	$scope.getRunning = function(){
		console.log('usao u ctrl za running');
		service.getRunning()
		.success(function(data){
			$scope.runningAgents=data;
			console.log($scope.runningAgents);
		})
	}
	
	$scope.initSocket = function() {
		console.log("usao u initSocket iz ctrl")
		service.initSocket()
	}
	
	$scope.send=function(){
		if(service.funkcija().getProperty() === "rest") {
			console.log('izabran rest send ctrl ');
		service.sendRest($scope.performative,$scope.sender,$scope.replyTo,$scope.content,$scope.language, $scope.encoding, $scope.ontology,$scope.protocol, $scope.conversationId)
		.success(function(data){})
		}
		if(service.funkcija().getProperty() === "ws") {
			console.log('izabran ws send ctrl ');
			service.sendWS($scope.performative,$scope.sender,$scope.replyTo,$scope.content,$scope.language, $scope.encoding, $scope.ontology,$scope.protocol, $scope.conversationId)
		}
	}
	
	  $scope.firstModal = function () {
	       var selectedCommunication = $scope.radioValue;
	       
	    	   service.funkcija().setProperty(selectedCommunication);
	       	       
	   };

	
	
	
	$scope.itemClick = function(x){
		console.log(x);
		$scope.type = x;
	}
	
	
	$scope.start = function(){
		if(service.funkcija().getProperty() === "rest") {
			console.log('izabran rest start ctrl ' );
		service.startRest($scope.name, $scope.type).success(function(data)
			{	
				var messageToShow=data.type.name +" "+ data.name + " created."
				//	document.getElementById("message").value += messageToShow;
				console.log(messageToShow);
				$scope.getRunning();
		})
		}
		if(service.funkcija().getProperty() === "ws") {
			console.log('izabran ws start ctrl ');
			service.startWS($scope.name, $scope.type);
		}
	}
	
	$scope.performatives = ["ACCEPT_PROPOSAL","AGREE","CANCEL","CALL_FOR_PROPOSAL","CONFIRM","DISCONFIRM","FAILURE","INFORM","INFORM_IF",
		"INFORM_REF","NOT_UNDERSTOOD","PROPAGATE","PROPOSE","PROXY","QUERY_IF","QUERY_REF","REFUSE","REJECT_PROPOSAL","REQUEST",
		"REQUEST_WHEN","REQUEST_WHENEVER","SUBSCRIBE"];
	
}]);

app.service('service',['$http', function($http){
		
	 
	 this.startRest=function(name, type){
			console.log('usao u rest start ' );
			document.getElementById("name").value = '';
			document.getElementById("name").placeholder = "Enter name";
			var port=window.location.port;
			return $http.get('/AT2017/rest/agent/startAgent/'+name+'/'+type + "/" + port);
	 }
	 
	 this.startWS=function(name, type){
			console.log('usao u WS start ' );
			document.getElementById("name").value = '';
			document.getElementById("name").placeholder = "Enter name";
			var port=window.location.port;
			var message = "StartAgent:" + name + ":" + type+":"+port;
			console.log(message);
			socket.send(message);
			
	 }
	 
	 
	 this.sendWS=function(performative,sender,replyTo,content,language,encoding,ontology,protocol,conversationId){
			console.log('usao u WS send ');
			
			
			document.getElementById("content").value = '';
			document.getElementById("content").placeholder = "Enter content of message..."; 
			document.getElementById("language").value = '';
			document.getElementById("encoding").value = '';
			document.getElementById("ontology").value = '';
			document.getElementById("protocol").value = '';
			document.getElementById("conversationId").value = '';
			
			document.getElementById('sendText').value = '';
			document.getElementById('replyText').value = '';
			document.getElementById('performativeText').value = '';
			
			var message = "SendMessage:" + performative + ":" + sender+ ":" +replyTo+ ":" +content;			
			console.log(message);
			
			socket.send(message);
			
	 }
	 
	 
	 
	 
	 this.sendRest=function(performative,sender,replyTo,content,language,encoding,ontology,protocol,conversationId){
			console.log('usao u rest send ');
			
			
			document.getElementById("content").value = '';
			document.getElementById("content").placeholder = "Enter content of message..."; 
			document.getElementById("language").value = '';
			document.getElementById("encoding").value = '';
			document.getElementById("ontology").value = '';
			document.getElementById("protocol").value = '';
			document.getElementById("conversationId").value = '';
			
			document.getElementById('sendText').value = '';
			document.getElementById('replyText').value = '';
			document.getElementById('performativeText').value = '';
			
		
			
			return $http.post('/AT2017/rest/agent/send/' + performative +'/'+ sender +'/'+ replyTo, content);
	 }
	 
	
	 this.initSocket = function(){
	 console.log("usao u initSocket")
	 var host = "ws://localhost:"+window.location.port+"/AT2017/websocket";
	 var poruka="";
	  try {
	   socket = new WebSocket(host);
	   console.log('connect. Socket Status: ' + socket.readyState + "\n");

	   socket.onopen = function() {
	    console.log('onopen. Socket Status: ' + socket.readyState
	      + ' (open)\n');
	    poruka='onopen. Socket Status: ' + socket.readyState
	    + ' (open)\n'
	   }

		socket.onmessage = function(msg) {
			console.log(msg.data);
			var currentdate = new Date(); 
			var datetime = (currentdate.getDate()<10?'0':'') + currentdate.getDate() + "/"
			                + (((currentdate.getMonth())+1)<10?'0':'') + ((currentdate.getMonth())+1)  + "/" 
			                + currentdate.getFullYear() + " "  
			                + (currentdate.getHours()<10?'0':'') + currentdate.getHours()+":"  
			                + (currentdate.getMinutes()<10?'0':'') + currentdate.getMinutes();			
			var show = datetime   + " - " +   msg.data;
			
			document.getElementById("message").value += show + '\n';

		
		    
		   }

	   socket.onclose = function() {
	    console.log('onsclose. Socket Status: ' + socket.readyState
	      + ' (Closed)\n');
	    socket = null;
	   }

	  } catch (exception) {
	   console.log('Error' + exception + "\n");
	  }
	  
	}
	 
	 this.funkcija = function(){
		return {
			getProperty : function(){
				return property;
			},
			setProperty : function(value){
				property = value;
			}
		
		};
	 }
		this.getExisting = function(){
		return $http.get('/AT2017/rest/agent/existing');
		}
		
		this.getRunning = function(){
			console.log('usao sam u service u running');
			return $http.get('/AT2017/rest/agent/running');
		}
	 
	  
}]);



	