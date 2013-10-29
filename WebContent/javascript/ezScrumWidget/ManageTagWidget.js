var Manage_IssueTagStore = new Ext.data.Store({
	fields:[{name:'Id', type:'int'},{name:'Name'}],
	reader:	tagReader
});

ezScrum.AddTagForm = Ext.extend(Ext.form.FormPanel, {
	url			: 'AjaxAddNewTag.do',
	bodyStyle	: 'padding:5px',
	border		: false,
	defaultType	: 'textfield',
	labelAlign	: 'right',
	labelWidth	: 50,
	monitorValid: true,
	defaults	: {
        msgTarget: 'side'
    },
	initComponent:function() {
		var config = {
			items: [{
			            fieldLabel	: 'Name',
			            name		: 'newTagName',
			            allowBlank	: false,	
						maxLength	: 100,
						width		: '85%'
	        		},{
	        			xtype       : 'RequireFieldLabel'
	        }],
		    buttons: [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit
	    	}, {
	        	text: 'Close',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddTagForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('AddSuccess', 'AddFailure');
	},
	onRender:function() {
		ezScrum.AddTagForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function() {
		var obj = this;
		var form = this.getForm();
		
		var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
		loadmask.show();
		Ext.Ajax.request({
			url		:obj.url,
			params:form.getValues(), 
			success	: function(response) { obj.onAddSuccess(response); },
			failure:function(response){ obj.onAddFailure(response); }
		});
	},
	onAddSuccess:function(response) {
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.hide();

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var q = Ext.DomQuery, doc = response.responseXML;
			var rs = tagReader.readRecords(response.responseXML);
			if(rs.success && rs.records.length > 0) {
				var record = rs.records[0];
				this.fireEvent('AddSuccess', this, response, record);
			} else {
				this.fireEvent('AddFailure', this, response, q.selectValue('Message', doc, 'Unknow Error'));
			}
		}		
	},
	onAddFailure:function(response) {
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.hide();
		
		this.fireEvent('Add Tag', this, response, 'Sorry, the connection is failure.');
	},
	reset:function() {
		this.getForm().reset();
	}
});

Ext.reg('AddTagForm', ezScrum.AddTagForm);

ezScrum.AddTagWidget = Ext.extend(ezScrum.layout.Window, {
	title	: 'Add Tag',
	width	: 300,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'AddTagForm'}]
        }
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddTagWidget.superclass.initComponent.apply(this, arguments);
		
		this.addTagForm = this.items.get(0); 
		this.addTagForm.on('AddSuccess', function(obj, response, record){ this.fireEvent('AddSuccess', this, obj, response, record); }, this);
		this.addTagForm.on('AddFailure', function(obj, response, message){ this.fireEvent('AddFailure', this, obj, response, message); }, this);
	},
	addTag:function(){
		this.addTagForm.reset();
		this.show();
	}
});

ezScrum.EditTagForm = Ext.extend(Ext.form.FormPanel, {
	url			: 'AjaxUpdateTag.do',
	bodyStyle	: 'padding:0px',
	border 		: false,
	defaultType	: 'textfield',
	labelAlign	: 'right',
	labelWidth	: 50,
	monitorValid: true,
	defaults: {
        msgTarget: 'side'
    },
    viewConfig: {
        forceFit: true
    },
	initComponent:function() {
		var config = {
			items: [{
	            fieldLabel: 'Name',
	            name: 'tagName',
	            allowBlank: false,	
				maxLength: 256		
	        }, {
	            name: 'tagId',
	            hidden: true		
	        }],
		    buttons: [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit
	    	}, {
	        	text: 'Close',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditTagForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditStoryForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function() {
		var obj = this;
		var form = this.getForm();
		
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
		Ext.Ajax.request({
			url	: obj.url,
			params:form.getValues(),
			success : function(response){ obj.onEditSuccess(response); },
			failure : function(response){ obj.onEditFailure(response); }
		});
	},
	onEditSuccess:function(response) {
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.hide();

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var q = Ext.DomQuery, doc = response.responseXML;
			var rs = tagReader.readRecords(response.responseXML);
			if(rs.success && rs.records.length > 0) {
				var record = rs.records[0];
				this.fireEvent('EditSuccess', this, response, record);
			} else {
				this.fireEvent('EditFailure', this, response, q.selectValue('Message', doc, 'Unknow Error'));
			}
		}		
	},
	onEditFailure:function(response) {
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.hide();
		
		this.fireEvent('EditFailure', this, response, 'Sorry, the connection is failure.');
	},
	setTag:function(tagId, tagName) {
		this.getForm().setValues({tagId:tagId, tagName: tagName});
	}
});
Ext.reg('EditTagForm', ezScrum.EditTagForm);

ezScrum.EditTagWidget = Ext.extend(ezScrum.layout.Window, {
	title	: 'Edit Tag',
	width	: 380,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{ xtype:'EditTagForm' }]
        }
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditTagWidget.superclass.initComponent.apply(this, arguments);
		
		this.editTagForm = this.items.get(0); 
		this.editTagForm.on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.editTagForm.on('EditFailure', function(obj, response, message){ this.fireEvent('EditFailure', this, obj, response, message); }, this);
	},
	editTag:function(tagId, tagName){
		this.editTagForm.setTag(tagId, tagName);
		this.show();
	}
});

var Manage_Tag_GridPanel = new Ext.grid.GridPanel({
	store	: Manage_IssueTagStore,
	layout	: 'fit',
	region	: 'center',
	margins	: '5 5 5 5',
	autoExpandColumn: 'Name',
	viewConfig: {
        forceFit: true
    },
	listeners: {
		render: function() {
			this.loadDataModel();
		},
		rowdblclick : function(grid, rowIndex, e) {
			if(!this.editTagWidget) {
				this.editTagWidget = new ezScrum.EditTagWidget();
				this.editTagWidget.on('EditSuccess', function(win, obj, response, record){
					win.hide(); 
					var index = Manage_IssueTagStore.indexOfId(record.data['Id']);
					Manage_IssueTagStore.removeAt(index);
					Manage_IssueTagStore.insert(index,record);
				});
				this.editTagWidget.on('EditFailure', function(win, obj, response, message){alert(message);});
			}
			var record = grid.getStore().getAt(rowIndex);
			this.editTagWidget.editTag(record.data['Id'], record.data['Name']);
		}
	},
	loadDataModel: function() {
    	var obj = this;
    	var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
		loadmask.show();
    	Ext.Ajax.request({
    		url	: 'AjaxGetTagList.do',
    		success:function(response) {
    			Manage_IssueTagStore.loadData(response.responseXML);
    			var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    			loadmask.hide();
    		}
    	});
    },
	tbar: [{
		icon: 'images/add3.png',
		text: 'Add Tag',
		handler: function(){
			if( ! this.addTagWidget) {
				this.addTagWidget = new ezScrum.AddTagWidget();
				this.addTagWidget.on('AddSuccess', function(win, obj, response, record){win.hide(); Manage_IssueTagStore.add(record);});
				this.addTagWidget.on('AddFailure', function(win, obj, response, message){alert(message);});
			}
			
			this.addTagWidget.addTag();
		}
	}, {
		ref: '../removeBtn',
		icon: 'images/delete.png',
		text: 'Remove Tag',
		disabled: true,
		handler: function() {
			Manage_Tag_GridPanel.confirm_RemoveTag(); // 警告視窗：確認是否移除tag
		}
	}],
	columns: [
		{id:'Name', header:'Tag', dataIndex:'Name', sortable:true}
	],
	onDeleteTagSuccess:function(tagId){
		var record = Manage_IssueTagStore.getById(tagId);
		Manage_IssueTagStore.remove(record);
	},
	onDeleteTagFailure:function(tagId){	
	},
	confirm_RemoveTag: function() {
    	var warningText = 'This action will affect number of stories.<br/><br/>Are you sure you want to continue ?<br/>';
		Ext.MessageBox.confirm('Warning ! ', warningText ,function(btn){
    		if(btn == 'yes'){
    			var s = Manage_Tag_GridPanel.getSelectionModel().getSelections();
    			for(var i = 0, r; r = s[i]; i++){
    				Ext.Ajax.request({
    					url:'AjaxDeleteTag.do',
    					params:{tagId:r.data['Id']},
    					success:function(response){
    						var rs = tagReader.readRecords(response.responseXML);
    						var id = rs.records[0].data['Id'];
    						Manage_Tag_GridPanel.onDeleteTagSuccess(id);
    						
//    						LoadData_PB(null);
    					}
    				});
    			}
    			LoadData_PB(null);
    			
    		}else if(btn == 'no'){
    			
    		}
    	});
    }
});
  	
Manage_Tag_GridPanel.getSelectionModel().on('selectionchange', function(sm){
Manage_Tag_GridPanel.removeBtn.setDisabled(sm.getCount() < 1);});

var Manage_Tag_Window = new ezScrum.layout.Window({
	title	: 'Tag Manage',
	height	: 500,
	width	: 500,
	layout	: 'fit',
	items	: [
		Manage_Tag_GridPanel
	],
	buttons	: [{
		text: 'Close',
		scope: this,
		handler : function() {
			Manage_Tag_Window.hide();
		}
	}]
});