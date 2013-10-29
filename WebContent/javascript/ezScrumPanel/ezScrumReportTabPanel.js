var ezScrumReportTabForm = {
	xtype: 'tabpanel',
	autoScroll:	true,
	activeTab: 0,
	frame: true,
	items: [{
			title: 'Remaining Work Report',
			id   : 'remainingWorkReport',
	    	url  : 'showRemainingReport.do?PID=' + getURLParameter("PID"),
	    	html : '<iframe id="remainingWorkReport" name="remainingWorkReport" src=showRemainingReport.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
    	},{
    		title: 'Schedule Report',
    		id   : 'scheduleReport',
    		url  : 'showScheduleReport.do?PID=' + getURLParameter("PID"),
    		html : '<iframe id="scheduleReport" name="scheduleReport" src=showScheduleReport.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
    	}
//    	,{	
//			title: 'CPI/SPI Report',	
//			ref: 'CPI_SPI_Report',	
//			xtype : 'CPI_SPI_Panel'
//		}
    ],
	/*
	 * when switch page to "Scrum Report", 
	 * we can indirectly reload the active tab by fire ezScrumReportPage to reload
	 */ 
	reloadActiveTab: function(){ 
		var activeTab = this.getActiveTab();
		reloadTab( activeTab );
	},
	listeners: {
		// reload when change tab
		tabchange: function(tabPanel, tab){
			//if( tab != this.items.get(2)){ // except CPI/SPI
				reloadTab( tab );
			//}
		}
	}
};

function reloadTab(tab) {
	var replaceUrl = tab.url;
	var tabId = tab.id;
	window.frames[ tabId ].location.replace( replaceUrl );
};
