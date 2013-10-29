<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
	<script type="text/javascript" src="javascript/ext-base.js"></script>
	<script type="text/javascript" src="javascript/ext-all-debug.js"></script>
	<script type="text/javascript" src="javascript/CompositeField.js"></script>
	<script type="text/javascript" src="javascript/FieldSet.js"></script>

	<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>	
	<script type="text/javascript" src="javascript/CustomWidget/Common.js"></script>
		
	<link rel="stylesheet" type="text/css" href="report.css" />

<!-- JavaScript -->
<script type="text/javascript">

function clickIssue(issueID){
	var projectName = "${issue.projectName}";
	parent.getTabComponent().addTab(projectName, issueID);
}

Ext.onReady(function() {
	//the info of issue
	var issue = "${issue}";
	var summary = "${summary}";
	var notes = "${Notes}";
	var description = "${description}";
	var howToDemo = "${howToDemo}";
	
	Ext.QuickTips.init();	
    var form = new Ext.form.FormPanel({
        frame:true,
        bodyStyle: 'padding: 5 5 0 0',
		autoHeight: true,
        renderTo: Ext.get("content2"),
        defaultType: 'textfield',
        items: [
			{
            	xtype: 'fieldset',
                title: 'Basic Info',
                collapsible: true,
                items: [
             		/* first row : issue id and last update*/
                	{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue id */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield',	value: 'Issue ID: ' },
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.issueID}&nbsp;' },
			    			/* last update */	
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Last Update: ' },
			    			{width: 200, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.lastUpdate}&nbsp;' }
			    		]
                	},
             		/* second row : issue name  */
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue name */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Issue Name: ' },
			    			{width: 480, cls: 'ReportFrameTitle',xtype: 'displayfield', value: summary + '&nbsp;' }
			    		]
					},	
					/* third row : issue category  and status*/
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue category */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Category: ' },
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.category}&nbsp;' }
			    		]
					},
					/* fourth row : issue status  */
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue status */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Status: '},
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.status}&nbsp;' }
			    		]
					},	
					/* fiveth row : reporter and handler*/
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue reporter */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Reporter: ' },
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.reporter}&nbsp;' },
		    				/* handler */	
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Handler: ' },
			    			{width: 200, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.assignto}&nbsp;' }
			    		]
					},	
					/* sixth row : Partner*/
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue partners */
		    				{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Partners: ' },
		    				{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.partners}&nbsp;' }
			    		]
					},
					/* seven row : Value*/
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue Value */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Value: ' },
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.value}&nbsp;' },
			    		]
					},
									
					/* eight row : importance and estimate*/
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue importance */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Imp.: ' },
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.importance}&nbsp;' },
		    				/* Est. */	
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Est.: ' },
			    			{width: 200, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.estimated}&nbsp;' }
			    		]
					},
					/* night row : Release ID and sprintID*/
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue release ID */
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Release ID: ' },
			    			{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.releaseID}&nbsp;' },
		    				/* Est. */	
			    			{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Sprint ID: ' },
			    			{width: 200, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '${issue.sprintID}&nbsp;' }
			    		]
					},			
					/* ten row : description */
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue description */
			   				{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Description: ' },
			   				{width: 480, cls: 'ReportFrameTitle', xtype: 'displayfield', value: description + '&nbsp;' }
					    ]
					},				
					/* eleven row : Notes */
					{
			    		xtype: 'compositefield',
			    		fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
						/* issue Notes */
		   					{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Notes: ' },
			   				{width: 480, height: 60, cls: 'ReportFrameTitle', xtype: 'displayfield', value: notes + '&nbsp;' }
						]
					},
					/* 12 row : How To Demo */
					{
		    			xtype: 'compositefield',
		    			fieldLabel: ' ',
			    		labelSeparator: '',
						items: [
							/* issue How To Demo */
		   					{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'How to demo: ' },
			   				{width: 480, height: 60, cls: 'ReportFrameTitle', xtype: 'displayfield', value: howToDemo + '&nbsp;' }
			    		]
					}
				]
			},
			{
				/* Relationships */
            	xtype: 'fieldset',
                title: 'Relationships',
                id: 'RelationshipsSet',
                collapsible: true
			},
			{
				/* Attached Files */
            	xtype: 'fieldset',
                title: 'Attached Files',
                id: 'AttachedFilesSet',
                collapsible: true
			},
			{	
				/* Historry */
            	xtype: 'fieldset',
                title: 'History',
                id: 'HistorySet',
                collapsible: true
			}
			
        ],
        //add the info of relationships
		addRelatonships: function(index, record){
			//data
			var issueId = record.data['IssueID'];
			var relationID = record.data['RelationID'];
			var relationType = record.data['RelationType'];
			var relationIssueName = record.data['RelationissueName'];
			//component
			var relationset = Ext.getCmp('RelationshipsSet');
			//add feild (relationType , relation issueID)
			var insertField = new Ext.form.CompositeField({
				fieldLabel: ' ',
    			labelSeparator: '',
				items: [
					/* issue category */
    				{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: relationType + '&nbsp;' },
    				{width: 430, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '<a href="javascript:clickIssue('+relationID+')">'+relationID+'</a>  ' + relationIssueName }
    			]
			});
			//insert
			relationset.insert(index, insertField);
			//refresh the layout            
            relationset.doLayout();
		},
        //add the info of relationships
		addFiles: function(index, record){
			//data
			var fileId = record.data['FileID'];
			var filename = record.data['Filename'];
			var filetype = record.data['Filetype'];
			var projectName = "${issue.projectName}";
			//component
			var fileset = Ext.getCmp('AttachedFilesSet');
			//add file (fileID , fileName)
			var insertField = new Ext.form.CompositeField({
				fieldLabel: ' ',
    			labelSeparator: '',
				items: [
					/* issue category */
    				{width: 120, cls: 'TaskReportHead', xtype: 'displayfield', value: 'File&nbsp;' },
    				{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: '<a href="fileDownload.do?projectName='+projectName+'&fileID='+fileId+'&fileName='+filename+'&fileType='+filetype+'" target="_blank">'+filename+'</a>' }
    			]
			});
			//insert
			fileset.insert(index, insertField);
			//refresh the layout            
            fileset.doLayout();
		},
       	//add the info of histories
		addHistory: function(index, record){
			//data
			var date = record.data['Date'];
			var field = record.data['Field'];
			var description = record.data['Description'];

			//component
			var historyset = Ext.getCmp('HistorySet');
			//add file (fileID , fileName)
			var insertField = new Ext.form.CompositeField({
				fieldLabel: ' ',
    			labelSeparator: '',
				items: [
					/* add history  */
    				{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: date + '&nbsp;' },
    				{width: 150, cls: 'ReportFrameTitle', xtype: 'displayfield', value: field + '&nbsp;' },
    				{width: 300, cls: 'ReportFrameTitle', xtype: 'displayfield', value: description + '&nbsp;' }
    			]
			});
			//insert
			historyset.insert(index, insertField);
			//refresh the layout            
            historyset.doLayout();
		},
       	//add the header of history
		addHistoryHeader: function(){
			//component
			var historyset = Ext.getCmp('HistorySet');
			//add head
			var insertField = new Ext.form.CompositeField({
				fieldLabel: ' ',
    			labelSeparator: '',
				items: [
					/* history title */
    				{width: 150, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Date Modified&nbsp;' },
    				{width: 150, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Field&nbsp;' },
    				{width: 300, cls: 'TaskReportHead', xtype: 'displayfield', value: 'Change&nbsp;' }
    			]
			});
			//insert
			historyset.insert(0, insertField);
			//refresh the layout            
            historyset.doLayout();
		},
		initialdData:function(){
			var issueID = ${issue.issueID};
			var projectName = "${issue.projectName}";
			/* load info of issue */
			Ext.Ajax.request({
				url:'ajaxGetIssueInfo.do',
				success:function(response){
					var rs = relationshipReader.readRecords(response.responseXML);
					if(rs.success){
						var recordCount = rs.totalRecords;
						for(index=0; index<recordCount; index++){
							var record = rs.records[index];
							form.addRelatonships(index, record);
						}
					}
					else{
						failure();
					}
				
					var rs2 = attachFileReader.readRecords(response.responseXML);
					if(rs2.success){
						var recordCount = rs2.totalRecords;
						for(index=0; index<recordCount; index++){
							var record = rs2.records[index];
							form.addFiles(index, record);
						}
					}
					else{
						failure();
					}
										
					var rs3 = historyReader.readRecords(response.responseXML);
					if(rs3.success){
						// add header
						form.addHistoryHeader();
						var recordCount = rs3.totalRecords;
						// add history
						for(index=0; index<recordCount; index++){
							var record = rs3.records[index];
							form.addHistory(index+1, record);
						}
					}
					else{
						failure();
					}
					
				},
				failure:function(response){
					Ext.example.msg('Load Relationship', 'Load Relationship failure.');
				},
				params:{issueID: issueID, projectName: projectName}
			});
			
		}
			
    });
    
    //initial data
    form.initialdData();
});
</script>	

<div id = "content2">
</div>