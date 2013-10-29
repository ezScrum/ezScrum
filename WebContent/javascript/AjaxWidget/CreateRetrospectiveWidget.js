Ext.ns('ezScrum');

/* Retrospective Type ComboBox 預設值為Good */
var typeComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: new Ext.data.ArrayStore({
        id: 0,
        fields: [
            {name: 'Id'},
       		{name: 'Name'}
        ],
        data: [['Good', 'Good'], ['Improvement', 'Improvement']]
    }),
    name: 'Type',
    valueField: 'Id',
    displayField: 'Name',
    fieldLabel: 'Type',
    originalValue: 'Good'
});

var sprintComboStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
        {name: 'Id', type: 'int'},
   		{name: 'Name'}
    ],
    reader:sprintForComboReader
});

/* Sprint ComboBox */
var sprintComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: sprintComboStoreForCreate,
    name: 'SprintComboBox',
    valueField: 'Id',
    displayField: 'Name',
    fieldLabel: 'Sprint',
    id: 'SprintComboBoxForCreate'
});

/* 取得 Sprint 資料 */
Ext.Ajax.request({
	url:'getAddNewRetrospectiveInfo.do',
	success:function(response){
		sprintComboStoreForCreate.loadData(response.responseXML);
	}
});

/* Create Story Form */
ezScrum.CreateRetrospectiveForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid:true,
	initComponent:function() {
		var config = {
			url : 'ajaxAddNewRetrospective.do',
			items: [
				{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        },
		        sprintComboForCreate
		        ,
		        typeComboForCreate
		        ,{
		            fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
		        }
		    ],
		    buttons: 
		    [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit,
	    		disabled:true
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateRetrospectiveForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateRetrospectiveForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();

		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:form.getValues()
		});
	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = retReader.readRecords(response.responseXML);
			
			if(rs.success)
			{
				var record = rs.records[0];
				if(record)
				{
					this.fireEvent('CreateSuccess', this, response, record);
				}
			}
			else
				this.fireEvent('CreateFailure', this, response);
		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('createRetrospectiveForm', ezScrum.CreateRetrospectiveForm);

ezScrum.AddNewRetrospectiveWidget = Ext.extend(Ext.Window, {
	title:'Add New Retrospective',
	id:'te',
	width:700,
	modal: true,
	constrain  : true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createRetrospectiveForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewRetrospectiveWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record){ this.fireEvent('CreateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(sprintId){
		/* 將 Sprint ComboBox 預設值設定為頁面所選取的 Sprint */
		var comboInForm = this.items.get(0).findById('SprintComboBoxForCreate');
		comboInForm.originalValue = sprintId;
		
		this.items.get(0).reset();
		this.show();
	}
});