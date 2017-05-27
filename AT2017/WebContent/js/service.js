app.factory('service', [
		'$http',
		function($http) {
			var k = {};

			k.initSocket = function() {

				var port = document.location.port;
				var host = "ws://localhost:" + port + "/AT2017/wsEndpoint";
				var poruka = "";
				try {
					socket = new WebSocket(host);
					message('connect. Socket Status: ' + socket.readyState
							+ "\n");

					socket.onopen = function() {
						message('onopen. Socket Status: ' + socket.readyState
								+ ' (open)\n');
						poruka = 'onopen. Socket Status: ' + socket.readyState
								+ ' (open)\n'
					}

					socket.onmessage = function(msg) {
						console.log("u servicu: " + msg);
						var res = JSON.stringify(msg).split(":");
						var dalje = res[1];
						if (dalje == "true") {
							// console.log("usao u true");
						} else {
							// console.log("usao u false");
						}
						return dalje;
					}

					socket.onclose = function() {
						message('onclose. Socket Status: ' + socket.readyState
								+ ' (Closed)\n');
						socket = null;
					}

				} catch (exception) {
					message('Error' + exception + "\n");
				}

				return poruka;
			}
			
		}]);