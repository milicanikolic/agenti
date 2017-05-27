app.controller('ctrl', function($scope, $rootScope, $window, $http,
		service) {

	$scope.initSocket = function() {
		console.log("init soketa");
		service.initSocket();
		// .success(function(data){
		// console.log(data);
		// })
	}
});