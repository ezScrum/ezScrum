<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
	<title>ezScrum, Issue detail information</title>
	<link rel="shortcut icon" href="images/scrum_16.png"/>
</head>

<!-- extjs -->
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>
<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />

<script type="text/javascript" src="javascript/ux/RowExpander.js"></script>


<script type="text/javascript">
	// namespace setting
	Ext.ns('ezScrum');
	Ext.ns('ezScrum.review');
	Ext.ns('ezScrum.window');
</script>


<!-- other support -->
<script type="text/javascript" src="javascript/CodePress/Ext.ux.codepress.js"></script>


<!-- ezScrum -->
<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumLayoutSupport.js"></script>

<script type="text/javascript" src="javascript/ezScrumCodeReview/IssueLinkSVNWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumCodeReview/CodeReviewSourceCodePanel.js"></script>

<script type="text/javascript" src="javascript/ezScrumCodeReview/IssueCodeReviewFileSourcePanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumCodeReview/IssueCodeReviewFileSourceWindow.js"></script>

<script type="text/javascript" src="javascript/ezScrumCodeReview/IssueCommitLogDataModel.js"></script>
<script type="text/javascript" src="javascript/ezScrumCodeReview/IssueCommitLogPanel.js"></script>

<script type="text/javascript">
	function getTabComponent() {
		return Ext.getCmp('IssueInformationTab');
	}
	
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		var m_issueID = "";
		var m_projectID = "";
		var url = new String(window.location);
		var index=url.indexOf('?');  
        if(index!=-1){  
            url=url.substring(index+1);
            var urlJSON=Ext.urlDecode(url);
            m_issueID=urlJSON.issueID;
            m_projectID=urlJSON.projectName;
        }
        
		//the tab
	    var IssueInformationTabPanel = new Ext.TabPanel({
	        id: 'IssueInformationTab',
	        renderTo: Ext.get("content"),
	        name:'IssueInformationTab',
	        autoHeight: true,
	        activeTab: 0,
	        frame:true,
	        items:[
				{
					id : 'Tab' + m_issueID,
					html : '<iframe src="viewIssueInfo.do?projectName='
							+ m_projectID
							+ '&issueID='
							+ m_issueID
							+ '" width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>',
					title : 'Issue ' + m_issueID
				}/*,
				{
					id : 'Issue_Commit_Logs' + m_issueID,
					issueID: m_issueID,
					title : 'Commit Logs',
					layout: 'form',
					xtype : 'IssueCommitLogPanel',
					listeners: {
						'render': function() {
							this.loadData(m_issueID);
						}
					}
				}*/
			],
			addTab: function(projectName, issueID) {
				var tab = this.getItem('Tab' + issueID);
				if (tab) {
					tab.show();
				} else {
					this.add({
						id : 'Tab' + issueID,
						html : '<iframe src="viewIssueInfo.do?projectName='
								+ projectName
								+ '&issueID='
								+ issueID
								+ '" width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>',
						title : 'Issue ' + issueID,
						closable : true
					}).show();
				}
			}
		});
	});
</script>

<div id="content"></div>

</html>
