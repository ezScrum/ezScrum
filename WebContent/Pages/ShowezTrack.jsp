<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript">
	/* Grid View */
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		//the tab
	    var tabs = new Ext.TabPanel({
	        renderTo: Ext.get("content"),
	        height: 630,
	        width: 1500,
	        activeTab: 0,
	        frame:true,
	        items:[{
	            	html : '<iframe src=showScrumIssue.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>',
					title: 'Scrum Issue'
				}, {
	            	html : '<iframe src=showCustomIssue.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>',
					title: 'Custom Issue'
				 }]
    	});
	});
</script>

<div id = "content"></div>

<div id="SideShowItem" style="display:none;">showezTrack</div>