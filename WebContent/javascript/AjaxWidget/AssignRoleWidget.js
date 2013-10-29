Ext.ns('ezScrum');

var msg = "Please wait...";

// Assign Role Information
var AssignRole = Ext.data.Record.create([
 	  'Resource', 'Operation'
]);

var assignRoleReader = new Ext.data.XmlReader({
	record: 'Assigned'
}, AssignRole);

var assignRoleStore = new Ext.data.Store({
	fields:[
   	{name : 'Resource'},
	{name : 'Operation'}	
	],
	reader : assignRoleReader
});

// Unassign Role Information
var UnassignRole = Ext.data.Record.create([
 	  'Resource'
]);

var unassignRoleReader = new Ext.data.XmlReader({
	record: 'Unassigned'
}, UnassignRole);

var unassignRoleStore = new Ext.data.Store({
	fields:[
   	{name : 'Resource'}
	],
	reader : unassignRoleReader
});
    
var scrumRoleStore = new Ext.data.ArrayStore({
	fields: ['Operation'],
	data: [['ProductOwner'],['ScrumMaster'],['ScrumTeam'],['Stakeholder'],['Guest']]
});


var unassignRoleCombo = new Ext.form.ComboBox({
	fieldLabel: 'Unassigned Project',
    name: 'unassignRole',
    editable:false,
	triggerAction:'all',
	forceSelection: true,
	mode:'local',
	store: unassignRoleStore,
    displayField: 'Resource',
    valueField: 'Resource',
    listeners: {
		// hide cursor
	    'expand': function(combo) {
	        var blurField = function(el) {
	            el.blur();
	        }
	        blurField.defer(10,this,[combo.el]);
	    },
	    'collapse': function(combo) {
	        var blurField = function(el) {
	            el.blur();
	        }
	        blurField.defer(10,this,[combo.el]);
	   	},
	   	'select': function(combo) {
	        // Check system Role
	        if (combo.getValue() == "system")
	        {
	        	roleCombo.setDisabled(false);
	        	roleCombo.disabled = true;
	        	roleCombo.originalValue = "admin";
	        	roleCombo.reset();

        	}
        	else
        	{
        		roleCombo.setDisabled(false);
        		roleCombo.originalValue = "";
        		roleCombo.reset();
        	}
        	
        	// Check add user button disable
	        if ((combo.getValue() != "") && (roleCombo.getValue() != ""))
	        	Ext.getCmp("addRoleBtn").setDisabled(false);
	        else
	        	Ext.getCmp("addRoleBtn").setDisabled(true);
        	
	        	
	   	}
	}
});


var roleCombo = new Ext.form.ComboBox({
	fieldLabel: 'Role',
    name: 'Role',
    xtype:'combo',
    editable:false,
    disabled: 'true',
	triggerAction:'all',
	forceSelection: true,
	mode:'local',
	store: scrumRoleStore,
    displayField: 'Operation',
    valueField: 'Operation',    
    listeners: {
 		// hide cursor
	    'expand': function(combo) {
	        var blurField = function(el) {
	            el.blur();
	        }
	        blurField.defer(10,this,[combo.el]);	        
	    },
	    'collapse': function(combo) {
	        var blurField = function(el) {
	            el.blur();
	        }
	        blurField.defer(10,this,[combo.el]);
    	},
    	'select': function(combo) {
	   		// Check add user button disable
	        if ((unassignRoleCombo.getValue()!="") && (combo.getValue()!=""))
	        	Ext.getCmp("addRoleBtn").setDisabled(false);
	        else
	        	Ext.getCmp("addRoleBtn").setDisabled(true);
	   	}
	}
});

var colModel = new Ext.grid.ColumnModel([
    {header: "Project", width: 150, dataIndex: 'Resource', id: 'Resource'},
    {header: "Role", width: 150, dataIndex: 'Operation'}
])

/* Assign Role Form */
ezScrum.AssignRoleForm = Ext.extend(Ext.form.FormPanel, {
	// Default Account ID
	id : 'assignRoleForm',
	accountID : '-1',
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 150,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			// Ajax load Unplanned Item url
			loadUrl : 'getAssignedProject.do',
			
			items: [
				{
            		xtype:'fieldset',
           			title: 'User Information',
            		autoHeight:true,
            		defaults: {width: 300},
            		defaultType: 'textfield',
            		items :[{
            			fieldLabel: 'User ID',
		            	name: 'accountID',
		            	id: 'accountID',
		            	readOnly: true
		        	},
		        	{
		            	fieldLabel: 'User Name',
		            	name: 'Name',
		            	readOnly: true
		        	}]
        		}
				,
		        {
            		xtype:'fieldset',
           			title: 'Unassigned  Projects',
            		autoHeight:true,
            		defaults: {width: 300},
            		defaultType: 'textfield',
            		items :[
            			unassignRoleCombo,           			
                    	roleCombo
                	],
                	buttons: 
				    [{
				    	text: 'Add Role',
				    	id: 'addRoleBtn',
				    	disabled: 'true',
			    		handler: function(){
			    			if ((unassignRoleCombo.getValue()!="") && (roleCombo.getValue()!=""))
			    			{
			    				var cid = Ext.getCmp("accountID").value;
			    				var selectProject = unassignRoleCombo.getValue();
			    				var accessLevel = roleCombo.getValue();
			    							    				
			    				showLoadMask(msg);
			    				
								Ext.Ajax.request({
									url: 'addUser.do',
									params: { 
										id: cid,
									    resource: selectProject, 
										operation: accessLevel
									},
									success:function(response){Ext.getCmp("assignRoleForm").onUpdateSuccess(response);},
									failure:function(response){Ext.getCmp("assignRoleForm").onUpdateFailure(response);}
								});		
		    				}
			    		}
			    	}]
        		},
        		{
            		xtype:'fieldset',
           			title: 'Assigned Projects',
            		autoHeight:true,
            		defaults: {width: 300},
            		items :[{
            			xtype: 'grid',
            			id: 'AssignedGrid',
		                ds: assignRoleStore,
		                cm: colModel,
		                sm: new Ext.grid.RowSelectionModel({
		                    singleSelect: true,
		                    listeners: {
								'selectionChange' : function(sm){  
									if (sm.getSelected() != null)
		                				Ext.getCmp("removeRoleBtn").setDisabled(false);
								}  
		                    }
		                }),
		                autoExpandColumn: 'Resource',
		                height: 200,
		                width: 460,
		                title:'Assigned Projects',
		                border: true
		        	}],
                	buttons: 
				    [{
				    	text: 'Remove Role',
				    	id: 'removeRoleBtn',
				    	disabled: 'true',
			    		handler: function(){
			    			var cid = Ext.getCmp("accountID").value;
			    			var resource = Ext.getCmp("AssignedGrid").getSelectionModel().getSelected().data['Resource'];
			    			var operation = Ext.getCmp("AssignedGrid").getSelectionModel().getSelected().data['Operation'];
			    			
			    			showLoadMask(msg);
			    			
			    			Ext.Ajax.request({
								url: 'removeUser.do',
								params: { 
									id: cid,
								    resource: resource, 
									operation: operation
								},
								success:function(response){Ext.getCmp("assignRoleForm").onUpdateSuccess(response);},
								failure:function(response){Ext.getCmp("assignRoleForm").onUpdateFailure(response);}
							});		
			    		}
			    	}]
        		}
        	],
        	buttons : 
        		[{
                    text    : 'Cancel',
                    scope   : this,
                    handler : function() {this.ownerCt.hide();  }
                 }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AssignRoleForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'UpdateSuccess', 'UpdateFailure');
	},
	onRender:function() {
		ezScrum.AssignRoleForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Load Role Item success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var account = accountReader.readRecords(response.responseXML);
		var assignRole = assignRoleReader.readRecords(response.responseXML);
		var unassignRole = unassignRoleReader.readRecords(response.responseXML);
		
		
		if(account.success && assignRole.success && unassignRole.success)
		{
			unassignRoleStore.loadData(response.responseXML);
			assignRoleStore.loadData(response.responseXML);
					
			var accountRecord = account.records[0];
			
			// Load account data
			if(accountRecord)
			{
				this.getForm().setValues({accountID : accountRecord.data['ID'], Name : accountRecord.data['Name']});
				this.fireEvent('LoadSuccess', this, response, accountRecord);
			}
			
			// Reset Combobox
			roleCombo.originalValue = "";
       		roleCombo.reset();
       		unassignRoleCombo.reset();
		}
		
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response);
	},
	onUpdateSuccess:function(response) 
	{
		
		var rs = accountReader.readRecords(response.responseXML);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				Ext.getCmp("assignRoleWidget").loadAssignRole(record.data['ID']);	
				hideLoadMask(msg);
				this.fireEvent('UpdateSuccess', this, response, record);
			}
		}
		else
		{
			hideLoadMask(msg);
			this.fireEvent('UpdateFailure', this, response);
		}

	},
	onUpdateFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('UpdateFailure', this, response);
	},
	loadStore:function()
	{
	
		var obj = this;

		var myMask = new Ext.LoadMask(obj.getEl(), {msg:"Please wait..."});
		myMask.show();
		
		Ext.Ajax.request({
			url:this.loadUrl,
			success:function(response){obj.onLoadSuccess(response);},
			failure:function(response){obj.onLoadFailure(response);},
			params : {accountID : this.accountID}
		});
		
	},
	reset:function()
	{
		this.getForm().reset();
	}
});
Ext.reg('assignRoleForm', ezScrum.AssignRoleForm);

ezScrum.AssignRoleWidget = Ext.extend(Ext.Window, {
	title:'Assign Role',
	id: 'assignRoleWidget',
	width:550,
	modal : true,
	constrain : true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'assignRoleForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AssignRoleWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'UpdateSuccess', 'UpdateFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response){ this.fireEvent('LoadFailure', this, obj, response); }, this);
		this.items.get(0).on('UpdateSuccess', function(obj, response, record){ this.fireEvent('UpdateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('UpdateFailure', function(obj, response){ this.fireEvent('UpdateFailure', this, obj, response); }, this);
	},
	loadAssignRole:function(accountID){
		this.items.get(0).accountID = accountID;
		this.items.get(0).reset();
		this.show();
		this.items.get(0).loadStore();
	}
});