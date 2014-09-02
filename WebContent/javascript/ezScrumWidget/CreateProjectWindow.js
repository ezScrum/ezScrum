Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

/* SaveProjectResult */
var CreateProjectResult = Ext.data.Record.create( [ 'Result' , 'IP', 'ID']);

var createResultReader = new Ext.data.XmlReader( {
	record : 'CreateProjectResult'
}, CreateProjectResult);

var createResultStore = new Ext.data.Store( {
	fields : [ {
		name : 'Result'
	} ,{
		name:'IP'
	} ,{
		name:'ID'
	}],
	reader : createResultReader
});

/**
 * 檢查專案名稱不能以空白結尾
 * 並從專案列表的ProjectStore取出id來比對，檢查專案ID是否已經存在
 */
function checkProjectIDValidate(field) {
	// check project name end with space
	var value = field.getValue();
	if (value.charAt(value.length - 1) == ' ') {
		field.textValid = 'The project name can not end with space.';
		return;
	}
	
	// 若專案列表為空
	if (ProjectStore.getCount() == 0) {
		field.clearInvalid();
		field.textValid = true;
		return;
	}
	
	// check existed project name
	for ( var i = 0; i < ProjectStore.getCount(); i++) {
		if (value == ProjectStore.getAt(i).id) {
			field.textValid = 'The project name is already existed.';
			break;
		} else {
			field.clearInvalid();
			field.textValid = true;
		}
	}
}

ezScrum.CreateProjectForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle : 'padding:15px',
	border : false,
	defaultType : 'textfield',
	labelAlign : 'right',
	labelWidth : 150,
	defaults : {
		width : 450,
		msgTarget : 'side'
	},
	monitorValid : true,
	initComponent : function() {
		var config = {
			url : 'AjaxCreateProject.do',
			items : [ {
				name : 'from',
				originalValue : 'createProject',
				hidden : true
			}, {
				fieldLabel : 'Project Name',
				name : 'Name',
				allowBlank : false,
				maxLength : 128,
				regex : /^[\w-_()~ ]*$/, // support a-z,A-Z,0-9,-,_,(,),~還有空格
				regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.',
				enableKeyEvents : true,
				textValid : true,
				validator : function() {
					return this.textValid;
				},
				listeners : { // 增加keyup event
					keyup : function() {
						checkProjectIDValidate(this);
					}
				}
			}, {
				fieldLabel : 'Project Display Name',
				name : 'DisplayName',
				allowBlank : false,
				maxLength : 128
			}, {
				fieldLabel : 'Comment',
				xtype : 'textarea',
				name : 'Comment',
				height : 50
			}, {
				fieldLabel : 'Project Manager',
				name : 'ProjectManager'
			}, {
				fieldLabel : 'Attach File Max Size (Default: 2MB)',
				name : 'AttachFileSize',
				vtype : 'Number'
			}, {
				xtype: 'RequireFieldLabel'
			}],
			buttons : [{
				formBind : true,
				text : 'Submit',
				scope : this,
				handler : this.submit,
				disabled : true
			}, {
				text : 'Cancel',
				scope : this,
				handler : function() {
					this.ownerCt.hide();
				}
			} ]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateProjectForm.superclass.initComponent.apply(this, arguments);

		this.addEvents('CreateSuccess', 'CreateFailure', 'InitDatabase', 'CreateDatabase');
	},
	
	onRender : function() {
		ezScrum.CreateProjectForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	
	submit : function() {
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg : "Validating..."
		});
		myMask.show();
		var form = this.getForm();
		var obj = this;
		
		Ext.Ajax.request( {
			url : 'AjaxCreateProject.do',
			success : function(response) {
				obj.onSuccess(response);
			},
			failure : function(response) {
				obj.onFailure(response);
			},
			params : form.getValues()
		});
	},
	
	onSuccess : function(response) {
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg : "Please wait..."
		});

		createResultStore.loadData(response.responseXML);

		if (createResultStore.getAt(0).data['Result'] == "Success")
			this.fireEvent('CreateSuccess', this, createResultStore.getAt(0).data['ID']);
		else if (createResultStore.getAt(0).data['Result'] == "Failure")
			this.fireEvent('CreateFailure', this, response);
		else if (createResultStore.getAt(0).data['Result'] == "InitDatabase") {
			response.ip = createResultStore.getAt(0).data['IP'];
			response.dbName = createResultStore.getAt(0).data['DBName'];
			this.fireEvent('InitDatabase', this.getForm(), response);
		}
        else if(createResultStore.getAt(0).data['Result'] == "CreateDatabase") {
            response.ip = createResultStore.getAt(0).data['IP'];
            response.dbName = createResultStore.getAt(0).data['DBName'];
            this.fireEvent('CreateDatabase', this.getForm(), response);
        }
        else if(createResultStore.getAt(0).data['Result'] == "Connect_Error") {
        	this.fireEvent('CreateFailure', this, response);
        }
		myMask.hide();
		
	},
	onFailure : function(response) {
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg : "Please wait..."
		});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);
	},
	reset : function() {
		this.getForm().reset();
	}
});

Ext.reg('CreateProjectForm', ezScrum.CreateProjectForm);

ezScrum.window.CreateProjectWindow = Ext.extend(ezScrum.layout.Window, {
	id : 'CreateProjectWindow',
	title : 'Create New Project',
	width : 700,
	modal : true,
	constrain : true,
	closeAction : 'hide',
	initComponent : function() {
		var config = {
			layout : 'form',
			items : [ {
				xtype : 'CreateProjectForm'
			} ]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.CreateProjectWindow.superclass.initComponent.apply(this, arguments);

		this.addEvents('CreateSuccess', 'CreateFailure', 'InitDatabase');

		this.items.get(0).on('CreateSuccess', function(obj, response) {
			this.fireEvent('CreateSuccess', this, obj, response);
		}, this);
		this.items.get(0).on('CreateFailure', function(obj, response) {
			this.fireEvent('CreateFailure', this, obj, response);
		}, this);
		this.items.get(0).on('InitDatabase', function(obj, response) {
			this.fireEvent('InitDatabase', this, obj, response);
		}, this),
        this.items.get(0).on('CreateDatabase',function(obj,response) {
            this.fireEvent('CreateDatabase', this, obj,response) 
        },this);
	}, 
	showWidget : function(sprint) {
		this.items.get(0).reset();
		this.show();
	}
});