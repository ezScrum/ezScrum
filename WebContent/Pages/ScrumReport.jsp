<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">

Ext.onReady(function() {
	
	var tabs = new Ext.TabPanel({
		id: 'tabs',
	    region: 'center',
	    activeTab: 0,
	    items: [{
	    	title: 'Remaining Work Report',
	    	html:'<iframe src=showRemainingReport.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
		},{
			title: 'Schedule Report',
			html:'<iframe src=showScheduleReport.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
		},{
			title: 'Reopen Report',
			disabled:true
		}]
	});
	
	// 多使用一個Panel裝TabPanel，如此才可以設置以Fit的方式剛好填滿頁面，而不超出頁面
	var tabPanel = new Ext.Panel({
		layout : 'border',
		viewConfig: {
	        forceFit: true
	    },
		items : [tabs]
	});
	
	var content = new Ext.Panel({
		renderTo: 'content',
		layout : 'fit',
		title : 'Scrum Report List',
		height: 650,
		items : [tabPanel]
	});
})
</script>	

<div id = "content">
</div>
<div id="SideShowItem" style="display:none;">showScrumReport</div>