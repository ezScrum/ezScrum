	//Load build infomation
	function loadBuildModule(){
		new Ajax.Request('getBuildInfo.do',
		{
			onSuccess: function(transport){
				var reportList = transport.responseXML.documentElement.getElementsByTagName("Report");      		
				var max_size = 5;
				
				//將報表列表顯示於build info欄位
				for( var i = 0; i < reportList.length && i < max_size ; i++ ){
					var report = reportList[i];
					addBuildInfo(report);
				}

				//若是比要觀看的report多, 則放一個more讓使用者點擊
				if( reportList.length > max_size ){
					addBuildMore();
				}
			},
			onFailure: function(){
				window.alert("與Server連線發生問題, 請重新再試.");
			}
		});
	}

	//Show more link in BuildInfo Block
	function addBuildMore(){
		var buildInfo = $('Build_Info');

		var a = new Element('a',{ 'href': 'showReportHistory.do'}).update('more...');
		var div = new Element('div',{ 'class': 'SummaryFieldText', 'align': 'right'}).update(a);

		buildInfo.insert(div);
	}
	
	//Show the report info to BuildInfo Block
	function addBuildInfo(report){
		var buildInfo = $('Build_Info');
		var id = report.getAttribute('id');
		var count = report.getAttribute('count');
		var date = report.getAttribute('date');
		var version = report.getAttribute('version');
		var value = date + ' ' + '(Build.' + count + ' version:' + version + ')';
		
		var herf = new Element('a',{ 'href': 'showIntegrationReport.do?integrationID=' + id }).update(value);
		
		var li = new Element('li',{ 'class':'SummaryFieldText' }).update(herf);
		buildInfo.insert(li);		
	}
	
	function loadScheduleModule(){
		//No implement
	}
	