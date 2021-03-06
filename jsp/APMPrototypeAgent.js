// html5 properties

var APMPrototypeAgent = new function(){

	//for the navigation timing, please refer to standard http://www.w3.org/TR/navigation-timing/
	this.submitClientPerformanceMetrics = function(){
		var networkTime = performance.timing.requestStart - performance.timing.domainLookupStart;
		var responseTime = performance.timing.responseEnd - performance.timing.requestStart;
		var domTime = performance.timing.domContentLoadedEventEnd - performance.timing.domLoading;
		var enduserTime = performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart;
		var clientTiming = {networkTime: networkTime, responseTime: responseTime, domTime: domTime, enduserTime: enduserTime };
		var client = new XMLHttpRequest();
		client.open("GET", "/apm/browserData?d="+encodeURI(JSON.stringify(clientTiming)));
		client.send();
	}
	
};
window.onload = function(){
	APMPrototypeAgent.submitClientPerformanceMetrics();
}
