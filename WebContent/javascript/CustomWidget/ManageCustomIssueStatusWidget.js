var statusStore = new Ext.data.Store({
	fields:[{name:'Id', type:'int'},{name:'Name'}],
	reader: statusReader
});

ezScrum.AddStatusForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 50,
	defaults: {
        width: 150,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			url : 'ajaxAddIssueTypeStatus.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'newStatusName',
		            allowBlank: false,	
					maxLength: 128		
		        }
		    ],
		    buttons: [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit
	    	}, {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddStatusForm.superclass.initComponent.apply(this, arguments);
		this.addEvents('AddSuccess', 'AddFailure');
	},
	onRender:function() {
		ezScrum.AddStatusForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function() {
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var obj = this;
		var form = this.getForm();
		
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onAddSuccess(response);},
			failure:function(response){obj.onAddFailure(response);},
			params: {typeName: StatusWin.typeName, statusName: form.findField('newStatusName').getValue()}
		});
	},
	onAddSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		var q = Ext.DomQuery,
		doc = response.responseXML;
		var rs = statusReader.readRecords(response.responseXML);
		if(rs.success && rs.records.length > 0){
			var record = rs.records[0];
			this.fireEvent('AddSuccess', this, response, record);
		}
		else{
			this.fireEvent('AddFailure', this, response, q.selectValue('Message', doc, 'Unknow Error'));
		}
		
	},
	onAddFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('AddFailure', this, response, 'Unknow Error!!');
	},
	reset:function()
	{
		this.getForm().reset();
	}

});

Ext.reg('addStatusForm', ezScrum.AddStatusForm);

ezScrum.AddStatusWidget = Ext.extend(Ext.Window, {
	title:'Add Status',
	width:280,
	modal:false,
	resizable:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'addStatusForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddStatusWidget.superclass.initComponent.apply(this, arguments);
		
		this.addStatusForm = this.items.get(0); 
		this.addStatusForm.on('AddSuccess', function(obj, response, record){ this.fireEvent('AddSuccess', this, obj, response, record); }, this);
		this.addStatusForm.on('AddFailure', function(obj, response, message){ this.fireEvent('AddFailure', this, obj, response, message); }, this);
	},
	addStatus:function(){
		this.addStatusForm.reset();
		this.show();
	}
});


//grid 
var grid = new Ext.grid.GridPanel({
	store: statusStore,
	width: 600,
	region:'center',
	margins: '0 5 5 5',
	autoExpandColumn: 'Name',
    sm: new Ext.grid.RowSelectionModel({
   		singleSelect:true
    }),
	tbar: [{
		icon: 'images/add3.png',
		text: 'Add Status',
		handler: function(){
			if(!this.addStatusWidget)
			{
				this.addStatusWidget = new ezScrum.AddStatusWidget();
				this.addStatusWidget.on('AddSuccess', function(win, obj, response, record){
					win.hide(); 
					statusStore.insert(statusStore.getCount()-1, record);
					Ext.example.msg('Add Status', 'Add status success.');
				});
				this.addStatusWidget.on('AddFailure', function(win, obj, response, message){alert(message);});
			}
			this.addStatusWidget.addStatus();
		}
	},
	{
		ref: '../removeBtn',
		icon: 'images/delete.png',
		text: 'Remove Status',
		disabled: true,
		handler: function(){
			// Delete status
			var record = grid.getSelectionModel().getSelected();
			Ext.MessageBox.confirm('Confirm', 'Are you sure you want to do that?', function(btn){
				if(btn == 'yes'){
					Ext.Ajax.request({
						url:'ajaxDeleteIssueTypeStatus.do',
						params:{typeName: StatusWin.typeName, statusName: record.data['Name']},
						success:function(response){
							grid.onDeleteStatusSuccess(record);
						}
					});
				}
			});
		}
	}],
	columns: [
		{id:'Name', header:'Status', dataIndex:'Name', sortable:false}
	],
	onDeleteStatusSuccess: function(record){
		statusStore.remove(record);
		Ext.example.msg('Delete Status', 'Delete status success.');
	},
	listeners:{
		rowdblclick : function(grid, rowIndex, e)
		{
			if(!this.editTagWidget)
			{
				this.editTagWidget = new ezScrum.EditTagWidget();
				this.editTagWidget.on('EditSuccess', function(win, obj, response, record){
					win.hide(); 
					var index = statusStore.indexOfId(record.data['Id']);
					statusStore.removeAt(index);
			 		statusStore.insert(index,record);
				});
				this.editTagWidget.on('EditFailure', function(win, obj, response, message){alert(message);});
			}
			var record = grid.getStore().getAt(rowIndex);
			this.editTagWidget.editTag(record.data['Id'], record.data['Name']);
		} 
	}});
  	
	grid.getSelectionModel().on('selectionchange', function(sm){
		grid.removeBtn.setDisabled(sm.getCount() < 1);});


	var StatusWin = new Ext.Window({
		title: 'Status Manage',
		typeName: '',
		width:600,
		modal:false,
		height:400,
		layout: 'border',
		layoutConfig: {
		    columns: 1
		},
		width:600,
		height: 600,
		items: [grid],
		closeAction:'hide',
		showWidget: function(typeName) {
			var obj = this;
			this.typeName = typeName;
			Ext.Ajax.request({
				url:'ajaxGetIssueStatus.do',
				success:function(response){
					StatusWin.show();
					statusStore.loadData(response.responseXML);
				},
				failure:function(response){
					Ext.example.msg('Load Status', 'Load Status failure.');
				},
				params:{typeName: typeName}
			});
		}
	});
