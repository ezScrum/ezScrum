var ezScrumReportPage = new Ext.Panel({
	id 			: 'ezScrum_Report_Page',
	layout 		: 'anchor',
	autoScroll	: true,
    items: [
	    ezScrumReportTabForm
    ],
    listeners: {
    	/*
    	 * when switch to page "Scrum Report" in ezScrum
    	 * the 'show' event is occur on this panel, not report TabPanel
    	 */
    	show: function(){
    		if( this.items.get(0).rendered){
    			this.items.get(0).reloadActiveTab();
    		}
    	}
    }
});