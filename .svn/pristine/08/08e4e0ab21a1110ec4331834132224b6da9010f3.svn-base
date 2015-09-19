function drawRequestStatChart(result){
	//good, fair, poor, error
	//var result = [200, 50, 20, 8]
	var data = [];
	data.push({value:result[0], color:'rgb(151,187,205)'});
	data.push({value:result[1], color:'#E2EAE9'});
	data.push({value:result[2], color:'#D4CCC5'});
	data.push({value:result[3], color:'#F7464A'});
	$('#goodTxnCount').html(result[0]);
	$('#fairTxnCount').html(result[1]);
	$('#poorTxnCount').html(result[2]);
	$('#errorTxnCount').html(result[3]);
	var total = 0;
	for(var i=0; i<result.length; i++){
		total = total+ result[i];
	}
	$('#totalTxnCount').html(total);
	var ctx = $("#requestStatChart").get(0).getContext("2d");
	new Chart(ctx).Doughnut(data);
}

function drawResponseTimeChart(result){
//	var result = [{'label':"11:00", 'value':[650,550,50,30]},
//		{'label':"11:05",'value':[590,480,32,50]},
//		{'label':"11:10",'value':[290,100,50,45]},
//		{'label':"11:15",'value':[510,410,45,50]},
//		{'label':"11:20",'value':[390,300,20,60]},
//		{'label':"11:25",'value':[850,780,20,50]},
//		{'label':"11:30",'value':[900,700,30,60]},
//		{'label':"11:35",'value':[300,250,10,30]},
//		{'label':"11:40",'value':[420,340,60,10]},
//		{'label':"11:45",'value':[490,380,70,10]},
//		{'label':"11:50",'value':[690,600,40,30]},
//		{'label':"11:55",'value':[580,500,20,40]}];
	var data = {
		datasets : [
			{
				fillColor: "rgba(151,187,205,0.2)",
				strokeColor: "rgba(151,187,205,1)",
				pointColor: "rgba(151,187,205,1)",
				pointStrokeColor : "#fff",
				title: "End User Time (ms)"
			},
			{
				fillColor : "rgba(220,220,220,0.5)",
				strokeColor : "rgba(220,220,220,1)",
				pointColor : "rgba(220,220,220,1)",
				pointStrokeColor : "#fff",
				title: "Response Time (ms)"
			},
			{
				fillColor : "rgba(180,150,160,0.5)",
				strokeColor : "rgba(180,150,160,1)",
				pointColor : "rgba(180,150,160,1)",
				pointStrokeColor : "#fff",
				title: "Network Time (ms)"
			},
			{
				fillColor: "rgba(131,127,190,0.2)",
				strokeColor: "rgba(131,127,190,1)",
				pointColor: "rgba(131,127,190,1)",
				pointStrokeColor : "#fff",
				title: "DOM Time (ms)"
			}
		]
	}
	populateMultiChartData(result, data);
	var ctx = $("#responseTimeChart").get(0).getContext("2d");
	var chart = new Chart(ctx).Line(data,{datasetFill:true,pointDot:false});
	legend($('#responseTimeChartLegend')[0], data);
}

function populateSingleChartData(result, data){
	var labels = [];
	var values = [];
	for(var i=0; i<result.length; i++){
		labels.push(result[i].label);
		values.push(result[i].value);
	}
	data.labels = labels;
	data.datasets[0].data = values;
}

function populateMultiChartData(result, data){
	var labels = [];
	var values = [];
	for(var i=0; i<result.length; i++){
		labels.push(result[i].label);
		for(var j=0; j<result[i].value.length; j++){
			if(values[j] == null){
				values[j] = [];
			}
			values[j].push(result[i].value[j]);
		}
	}
	data.labels = labels;
	for(var i=0; i<values.length; i++){
		data.datasets[i].data = values[i];
	}
}

function drawAllTransactionsTable(data){
//	var data = [
//		{'id':'axaaaxxa1', 'time':'2014-05-30T22:15:00', 'name':'/login.jsp', 'responseTime': 400, 
//			'dbTiming':[{'exeTime':350,'sql': 'SELECT * FROM USER WHERE NAME=?','parameters':['frank']}]
//		},
//		{'id':'axaaaxxa2', 'time':'2014-05-30T22:15:02', 'name':'/detail.jsp', 'responseTime': 1020,
//			'dbTiming':[{'exeTime':200,'sql': 'SELECT * FROM USER WHERE NAME=?','parameters':['frank']},{'exeTime':300,'sql': 'SELECT * FROM PRODUCTS','parameters':[]},{'exeTime':200,'sql': 'SELECT * FROM PRODUCT_DESCRIPTION WHERE ID=?','parameters':['90']},{'exeTime':200,'sql': 'SELECT * FROM PRODUCT_DESCRIPTION WHERE ID=?','parameters':['100']}]
//		},
//		{'id':'axaaaxxa3', 'time':'2014-05-30T22:15:04', 'name':'/index.jsp', 'responseTime': 150},
//		{'id':'axaaaxxa4', 'time':'2014-05-30T22:15:05', 'name':'/checkout.jsp', 'responseTime': 270,
//			'dbTiming':[{'exeTime':150,'sql': 'INSERT (?,?,?) INTO ORDERS','parameters':["product","chocolate","10"]}]
//		},
//		{'id':'axaaaxxa5', 'time':'2014-05-30T22:15:08', 'name':'/bid.jsp', 'responseTime': 300},
//		{'id':'axaaaxxa6', 'time':'2014-05-30T22:15:10', 'name':'/check.jsp', 'responseTime': 100},
//		{'id':'axaaaxxa7', 'time':'2014-05-30T22:15:12', 'name':'/login.jsp', 'responseTime': 500},
//		{'id':'axaaaxxa8', 'time':'2014-05-30T22:15:13', 'name':'/view.jsp', 'responseTime': 302},
//		{'id':'axaaaxxa9', 'time':'2014-05-30T22:15:15', 'name':'/search.jsp', 'responseTime': 307},
//		{'id':'axaaaxxa10', 'time':'2014-05-30T22:15:190', 'name':'/login.jsp', 'responseTime': 420}
//	]
	$('#allTransactionsTable tbody tr').remove();
	for(var i=0; i<data.length; i++){
		var html = '<tr'+(i==0?' class="selected"':'')+' transactionId="'+data[i].id+'">';
		html += '<td>'+data[i].time+'</td>';
		html += '<td>'+data[i].name+'</td>';
		html += '<td>'+data[i].responseTime+' ms</td>';
		if(!data[i].dbTiming || data[i].dbTiming.length == 0){
			html += '<td>'+(data[i].dbTime?(data[i].dbTime+' ms'):'-')+' </td>';
			html += '<td>-</td></tr>';
		}else{
			var totalDBTime = 0;
			var sqlCount = 0;
			for(var j=0; j<data[i].dbTiming.length; j++){
				totalDBTime += data[i].dbTiming[j].exeTime;
				sqlCount++;
			}
			html += '<td>'+totalDBTime+' ms</td>';
			html += '<td>'+sqlCount+'</td></tr>';
		}
		$('#allTransactionsTable tbody').append(html);
	}
	var fillSQLDetail = function(transaction){
		var sqlStatmentToHTML = function(dbTime){
			var html = dbTime.sql+'<br>&nbsp;&nbsp;&nbsp;<span class="dim">Execution Time:</span>&nbsp;'+dbTime.exeTime+' ms';
			if(dbTime.parameters && dbTime.parameters.length>0){
				html += '<br>&nbsp;&nbsp;&nbsp;<span class="dim">Parameters:</span>&nbsp;'+dbTime.parameters.join(',&nbsp;')+'</span>';
			}
			return html;
		}
		var html = '';
		if(!transaction.dbTiming || transaction.dbTiming.length == 0){
			html = '<span class="dim">There is no SQL statement captured.</span>'
		}else{
			var htmlDBTiming = [];
			for(var i=0; i<transaction.dbTiming.length; i++){
				htmlDBTiming.push(sqlStatmentToHTML(transaction.dbTiming[i]));
			}
			html = htmlDBTiming.join('<br><br>');
		}
		$('#sqlStatements').html(html);
	}
	$('#allTransactionsTable').tableScroll({height:180});
	fillSQLDetail(data[0]);
	$("div.tablescroll tr").click(function(){
		$(this).addClass("selected").siblings().removeClass("selected");
		var transactionId = $(this).attr('transactionId');
		var transaction = null;
		for(var i=0; i<data.length; i++){
			if(data[i].id == transactionId){
				transaction = data[i];
			}
		}
		fillSQLDetail(transaction);
	});
}

function drawJVMCharts(heap, gcRate, cpuTime, threadCount){
	if(!heap || heap.length == 0){
		heap = [{'label':"", 'value':[0,0]}];
	}
	if(!gcRate || gcRate.length == 0){
		gcRate = [{'label':"", 'value': 0}];
	}
	if(!cpuTime || cpuTime.length == 0){
		cpuTime = [{'label':"", 'value': 0}];
	}
	if(!threadCount || threadCount.length == 0){
		threadCount = [{'label':"", 'value': 0}];
	}
//	var gcRate = [{'label':"11:00", 'value':0.65},{'label':"11:05",'value':0.59},{'label':"11:10",'value':0.29},{'label':"11:15",'value':0.51}];
//	var cpuTime = [{'label':"11:00", 'value':0.67},{'label':"11:05",'value':0.79},{'label':"11:10",'value':0.99},{'label':"11:15",'value':0.11}];
//	var threadCount = [{'label':"11:00", 'value':0.35},{'label':"11:05",'value':0.29},{'label':"11:10",'value':0.29},{'label':"11:15",'value':0.11}];

	var singleDataTemplate = {
		datasets : [
			{
				fillColor : "rgba(220,220,220,0.5)",
				strokeColor : "rgba(220,220,220,1)",
				pointColor : "rgba(220,220,220,1)",
				pointStrokeColor : "#fff",
			}
		]
	}
	var multiDataTemplate = {
		datasets : [
			{
				fillColor : "rgba(220,220,220,0.5)",
				strokeColor : "rgba(220,220,220,1)",
				pointColor : "rgba(220,220,220,1)",
				pointStrokeColor : "#fff",
			},{
				fillColor: "rgba(151,187,205,0.2)",
				strokeColor: "rgba(151,187,205,1)",
				pointColor: "rgba(151,187,205,1)",
				pointStrokeColor: "#fff",
				pointHighlightFill: "#fff",
				pointHighlightStroke: "rgba(151,187,205,1)",
			}
		]
	}
	var cloneTemplate = function(template){
		return JSON.parse(JSON.stringify(template));
	}
	var heapData = cloneTemplate(multiDataTemplate);
	populateMultiChartData(heap, heapData);
	heapData.datasets[0].title = "Used Heap(M)";
	heapData.datasets[1].title = "Committed Heap(M)";
	var ctx = $("#jvmHeapChart").get(0).getContext("2d");
	new Chart(ctx).Line(heapData, {datasetFill:true,pointDot:false});
	legend($('#jvmHeapChartLegend')[0], heapData);
	
	var gcData = cloneTemplate(singleDataTemplate);
	populateSingleChartData(gcRate, gcData);
	gcData.datasets[0].title = "GC Rate(count/minute)";
	ctx = $("#jvmGCChart").get(0).getContext("2d");
		new Chart(ctx).Line(gcData, $.extend({datasetFill:true,pointDot:false}, getScaleSettingStartFromZero(gcData.datasets[0].data, 10, 2)));
	legend($('#jvmGCChartLegend')[0], gcData);
	
	var cpuData = cloneTemplate(singleDataTemplate);
	populateSingleChartData(cpuTime, cpuData);
	cpuData.datasets[0].title = "Average CPU Time(ms)";
	ctx = $("#jvmCPUChart").get(0).getContext("2d");
	new Chart(ctx).Line(cpuData, {datasetFill:true,pointDot:false});
	legend($('#jvmCPUChartLegend')[0], cpuData);
	
	var threadData = cloneTemplate(singleDataTemplate);
	populateSingleChartData(threadCount, threadData);
	threadData.datasets[0].title = "Thread Count(c)";
	ctx = $("#jvmThreadChart").get(0).getContext("2d");
	new Chart(ctx).Line(threadData, $.extend({datasetFill:true,pointDot:false}, getScaleSettingStartFromZero(threadData.datasets[0].data, 10, 2)));
	legend($('#jvmThreadChartLegend')[0], threadData);
}
Array.prototype.max = function() {
  return Math.max.apply(null, this);
};
var getScaleSettingStartFromZero = function(dataArray, scaleSteps, precision){
	var additionalSetting = {'scaleStartValue': 0, 'scaleSteps':scaleSteps, 'scaleOverlay':false, 'scaleOverride':true};
	scaleStepWidth = (dataArray.max() - 0)/scaleSteps;
	if(scaleStepWidth == 0){
		scaleStepWidth = 1;
	}else{
		if(precision > 0){
			scaleStepWidth = scaleStepWidth.toPrecision(precision);
		}
	}
	additionalSetting['scaleStepWidth'] = scaleStepWidth;
	return additionalSetting;
}

function initTimeRange(){
	var formatDateTime = function(d){
		return d.getFullYear()+"-"+toTwoDig(d.getMonth() + 1)+"-"+toTwoDig(d.getDate())+"T"+toTwoDig(d.getHours())+":"+toTwoDig(d.getMinutes());
	}

	var toTwoDig = function(v){
		if(v < 10){
			return "0"+v;
		}
		return v;
	}

	var now = new Date();
	var oneHourEarlier = new Date();
	oneHourEarlier.setTime(now.getTime() - 3600*1000);
	$('#timeRangeStart').val(formatDateTime(oneHourEarlier));
	$('#timeRangeEnd').val(formatDateTime(now));
	$('#changeTimeRangeBtn').bind('click', function(){
		loadPerfData();
	});
}

function getTimeRangeQueryString(){
	return "startTime=" + $('#timeRangeStart').val() + "&endTime=" + $('#timeRangeEnd').val();
}

function loadPerfData(){
	$.ajax({
		url:"query?" + getTimeRangeQueryString(),
		type:"POST",
		dataType: "json",
		success: function(data, status, jqXHR){
			drawRequestStatChart(data.requestStat);
			if(!data.responseTime || data.responseTime.length == 0){
				drawResponseTimeChart([{"label":"", "value":[0,0,0,0]}]);
			}else{
				drawResponseTimeChart(data.responseTime);
			}
			
			if(!data.transactions || data.transactions.length == 0){
				drawAllTransactionsTable([{}]);
			}else{
				drawAllTransactionsTable(data.transactions);
			}
			
			drawJVMCharts(data.heap, data.gcRate, data.cpuTime, data.threadCount);
		}
	});
}

$(document).ready(function(){
	initTimeRange();
	loadPerfData();
	$('#overviewPlaceholder').width($('#overviewDiv').width()*0.35 - 400);
});