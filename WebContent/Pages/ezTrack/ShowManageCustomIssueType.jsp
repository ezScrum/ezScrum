<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
	<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
	<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
	
	
	<script type="text/javascript" src="javascript/CustomWidget/Common.js"></script>
	<script type="text/javascript" src="javascript/CustomWidget/CreateCustomIssueTypeWidget.js"></script>
	<script type="text/javascript" src="javascript/CustomWidget/ManageCustomIssueStatusWidget.js"></script>
	
    <link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">
	
	Ext.ns('ezScrum');
	
	var typeStore = new Ext.data.Store({
		fields: [{name:'TypeId', type:'int'}, {name:'TypeName'}, {name:'IsPublic'}],
		reader: customIssueCategoryReader,
		url: 'AjaxGetCustomIssueType.do',
		storeId: 'TypeId',
		autoLoad: true
	});

	/* the issue type column  */
    var createColModel = function () {
        var columns = [
			{dataIndex: 'TypeName', header: 'Name', width: 600},
			{dataIndex: 'IsPublic', header: 'Public', width: 600}
		];
        return new Ext.grid.ColumnModel({
            columns: columns,
            defaults: {
                sortable: true
            }
        });
    };
	
	
	/* on ready */
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		var createIssueTypeWidget = new ezScrum.CreateIssueTypeWidget({
			listeners:{
				CreateSuccess:function(win, form, response){			
			 		this.hide();
			 		typeStore.load(response);
			 		Ext.example.msg('Create issue type', 'Create issuetype Success.');
				},
				CreateFailure:function(win, form, response){
					this.hide();
					Ext.example.msg('Create issue type', 'Create issuetype Failure.');
				}
			}
		});
		
		/* manageIssueTypeWidget */
		var manageIssueTypeWidget = new Ext.grid.GridPanel({
			colModel: createColModel(),
			height: 600,
			id : 'gridPanel',
			enableColumnHide : false,
			region : 'center',
			store: typeStore,
		    sm: new Ext.grid.RowSelectionModel({
		    	singleSelect:true
		    })
		});

		/* model selection change */	
		manageIssueTypeWidget.getSelectionModel().on({
			selectionchange:{buffer:10, fn:function(sm) {
				//var record = sm.getSelected();
				//alert(record.data['TypeId']+" --- "+record.data['TypeName']);
			}
		}});
		
		/* Main Widget */
		var masterWidget = new Ext.Panel({
			height: 600,
			title: 'Manage Issue Type',
			items:[manageIssueTypeWidget],
			renderTo: Ext.get("content"),
			region:'center',
			margins: '0 5 5 5',

			tbar: [{
				icon: 'images/add3.png',
				text: 'Add Issue Type',
				handler: function(){
					createIssueTypeWidget.showWidget();
				}
			}, {
				icon: 'images/delete.png',
				text: 'Delete Issue Type',
				/* 處理delete issue type */
				handler: function(){
					/* 欲刪除的typeID */
					var typeID = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['TypeId'];
					Ext.MessageBox.confirm('Confirm', 'Are you sure you want to do that?', function(btn){
						if(btn == 'yes'){
							Ext.Ajax.request({
								url : 'ajaxDeleteIssueType.do',
								params : {typeID: typeID},
								success : function(response){
									var index = typeStore.find("TypeId", typeID);
									if(index!=-1){
									 	var record = typeStore.getAt(index);
								    	typeStore.remove(record);
										Ext.example.msg('Delete Issue Type', 'Delete Issue Type Success.');
									}
								},
								failure : function(response){
									Ext.example.msg('Delete Issue Type', 'Delete Issue Type Failure.');
								}
							});
						}
					});
				}
			}, {
				icon: 'images/magic-wand.png',
				text: 'Manage Status',
				handler: function(){
					var typeName = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['TypeName'];
					StatusWin.showWidget(typeName);
				}
			}]
		});
		
		StatusWin.on({
			hide:function(){
			}
		});
	});
</script>

<input type="hidden" value="" id="typeID" name="typeID">
<div id = "content">
</div>
<div id="SideShowItem" style="display:none;">showManageIssueType</div>