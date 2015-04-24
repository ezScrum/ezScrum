<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript">

	var Project = Ext.data.Record.create([
	   'Name', 'DisplayName', 'AttachFileSize', 'Comment', 'ProjectManager'
	]);
	
	var projectReader = new Ext.data.XmlReader({
	   record: 'ProjectInfo',
	   idPath : 'Name'
	}, Project);
	
	var projectStore = new Ext.data.Store({
    	fields:[
			{name : 'Name'},
			{name : 'DisplayName'},
			{name : 'AttachFileSize'},
			{name : 'Comment'},
			{name : 'ProjectManager'}
		],
		reader : projectReader
	});
	
	
	var EditResult = Ext.data.Record.create([
	   'Result'
	]);
	
	var EditResultReader = new Ext.data.XmlReader({
	   record: 'EditProjectResult'
	}, EditResult);
	
	var EditResultStore = new Ext.data.Store({
	   	fields:[
			{name : 'Result'}
		],
		reader : EditResultReader
	});

	Ext.onReady(function() {
		
		var projectInfoForm = new Ext.FormPanel({
	        url:'AjaxCreateProject.do',
	        border : true,
	        frame: false,
	        title: 'Project Reference',
	        bodyStyle:'padding:15px',
	        labelAlign : 'right',
			labelWidth : 150,
	        defaults: {width: 300},
	        defaultType: 'textfield',
	        monitorValid:true,
	        items: [{
                fieldLabel: 'Project Name',
                name: 'Name'
            },{
                fieldLabel: 'Project Display Name',
                name: 'DisplayName',
                allowBlank:false
            },{
                fieldLabel: 'Comment',
                name: 'Comment',
                xtype: 'textarea',
                height:50
            }, {
                fieldLabel: 'Project Manager',
                name: 'ProjectManager'
            },{
                fieldLabel: 'Attach File Max Size (Default: 2MB)',
                name: 'AttachFileSize',
                vtype:'Number'
            }]
	    });
	    
	    var submit = projectInfoForm.addButton({
	    	formBind:true,
	    	disabled:true,
	        text: 'Submit',
	        handler: function(){
				var form = projectInfoForm.getForm();
				Ext.Ajax.request({
					url:projectInfoForm.url,
					success:function(response){
						EditResultStore.loadData(response.responseXML);
						
						var record = EditResultStore.getAt(0);
						if (record.data['Result'] == "Success")
							document.location.href = '<html:rewrite action="/viewProjectSummary" />';
						else
							alert('EditFailure');
					},
					failure:function(response){
						alert('EditFailure');
					},
					params: form.getValues()
				});
	        }
	    });
	    
	    var cancel = projectInfoForm.addButton({
	        text: 'Cancel',
	        handler: function(){
	            document.location.href = '<html:rewrite action="/viewProjectSummary" />';
	        }
	    });
	    
	
	    projectInfoForm.render('content');
	    
	    Ext.Ajax.request({
			url:'showProjectInfo.do',
			success: function(response){
				projectStore.loadData(response.responseXML);
				if (projectStore.getTotalCount() == 1)
				{
					var record = projectStore.getAt(0);
					projectInfoForm.getForm().setValues({
						Name : record.data['Name'],
						DisplayName : record.data['DisplayName'],
						Comment : record.data['Comment'],
						ProjectManager : record.data['ProjectManager'],
						AttachFileSize : record.data['AttachFileSize']});
					projectInfoForm.getForm().findField('Name').getEl().dom.readOnly = true;					
				}
			},
			failure:function(response){
				alert('Failure');
			}
		});
	})

</script>

<div id = "content"></div>

<div id="SideShowItem" style="display:none;">showProjectInfoForm</div>