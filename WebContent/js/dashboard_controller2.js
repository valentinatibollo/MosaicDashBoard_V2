//
//$(function(){
//	renderContainer();
//
//});
//
//$( window ).resize(function() {
//	renderContainer();    		
//});

function renderContainerCP(){
	$('#chart_container_cp').width(Math.floor($('#dashboard_container_cp').innerWidth() -50));

	$('#chart_container_cp').height($('#dashboard_container_cp').height());

//	//setloader position
//	$('.loader_cp').css('left',($('#dashboard_container_cp').innerWidth()/2)-$('.loader_cp').innerWidth());
//
//	hideLoaderCP();
}


//google.load("visualization", "1", {packages:["corechart", "timeline"]});
//google.setOnLoadCallback(drawCharts);

function drawChartsCP(sessionId) {
//	hideLoaderCP();
	hist_chart_data_json = $.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: false,
		data: {step: "1",chart_type: "time_to_complication", session_id: sessionId}
	}).responseText;


	var outerJson = jQuery.parseJSON(hist_chart_data_json);
	var general_complication_json = outerJson.generic_complication;
	var ami_complication_json = outerJson.ami_complication;
	var ang_complication_json = outerJson.ang_complication;
	var cihd_complication_json = outerJson.cihd_complication;
	var occ_complication_json = outerJson.occ_complication;
	var paod_complication_json = outerJson.paod_complication;
	var str_complication_json = outerJson.str_complication;
	var df_complication_json = outerJson.df_complication;
	var fld_complication_json = outerJson.fld_complication;
	var neu_complication_json = outerJson.neu_complication;
	var neph_complication_json = outerJson.neph_complication;
	var ret_complication_json = outerJson.ret_complication;
	var hist_chart_data = new google.visualization.DataTable(general_complication_json);
	var hist_ami_chart_data = new google.visualization.DataTable(ami_complication_json);
	var hist_ang_chart_data = new google.visualization.DataTable(ang_complication_json);
	var hist_cihd_chart_data = new google.visualization.DataTable(cihd_complication_json);
	var hist_occ_chart_data = new google.visualization.DataTable(occ_complication_json);
	var hist_paod_chart_data = new google.visualization.DataTable(paod_complication_json);
	var hist_str_chart_data = new google.visualization.DataTable(str_complication_json);
	var hist_df_chart_data = new google.visualization.DataTable(df_complication_json);
	var hist_fld_chart_data = new google.visualization.DataTable(fld_complication_json);
	var hist_neu_chart_data = new google.visualization.DataTable(neu_complication_json);
	var hist_neph_chart_data = new google.visualization.DataTable(neph_complication_json);
	var hist_ret_chart_data = new google.visualization.DataTable(ret_complication_json);
	
	var options_hist = {
			isStacked: false,
			vAxis: { title: "Patients Number", textStyle:{fontSize: '14',fontName: 'MyriadPro' }, titleTextStyle:{fontSize: '14',fontName: 'MyriadPro' }},
			hAxis: {title: "Years from first visit", textStyle:{fontSize: '14',fontName: 'MyriadPro' }},	
			legend: { position: 'none' },
			colors: ['#015E84'],
			explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
			tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } }
	};	

	var hist_duration_chart = new google.visualization.ColumnChart(document.getElementById('generic_time_to_complication_chart'));
	var hist_ami_chart = new google.visualization.ColumnChart(document.getElementById('ami_chart'));
	var hist_ang_chart = new google.visualization.ColumnChart(document.getElementById('ang_chart'));
	var hist_cihd_chart = new google.visualization.ColumnChart(document.getElementById('cihd_chart'));
	var hist_occ_chart = new google.visualization.ColumnChart(document.getElementById('occ_chart'));
	var hist_paod_chart = new google.visualization.ColumnChart(document.getElementById('paod_chart'));
	var hist_str_chart = new google.visualization.ColumnChart(document.getElementById('str_chart'));
	var hist_df_chart = new google.visualization.ColumnChart(document.getElementById('df_chart'));
	var hist_fld_chart = new google.visualization.ColumnChart(document.getElementById('fld_chart'));
	var hist_neu_chart = new google.visualization.ColumnChart(document.getElementById('neu_chart'));
	var hist_neph_chart = new google.visualization.ColumnChart(document.getElementById('neph_chart'));
	var hist_ret_chart = new google.visualization.ColumnChart(document.getElementById('ret_chart'));
	hist_duration_chart.draw(hist_chart_data, options_hist);
	hist_ami_chart.draw(hist_ami_chart_data, options_hist);
	hist_ang_chart.draw(hist_ang_chart_data, options_hist);
	hist_cihd_chart.draw(hist_cihd_chart_data, options_hist);
	hist_occ_chart.draw(hist_occ_chart_data, options_hist);
	hist_paod_chart.draw(hist_paod_chart_data, options_hist);
	hist_str_chart.draw(hist_str_chart_data, options_hist);
	hist_df_chart.draw(hist_df_chart_data, options_hist);
	hist_fld_chart.draw(hist_fld_chart_data, options_hist);
	hist_neu_chart.draw(hist_neu_chart_data, options_hist);
	hist_neph_chart.draw(hist_neph_chart_data, options_hist);
	hist_ret_chart.draw(hist_ret_chart_data, options_hist);

}


//function showLoaderCP() {
//	$('#loader_cp').show();
//}
//
//
//
//function hideLoaderCP() {
//	$('#loader_cp').hide();
//}





