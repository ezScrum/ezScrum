Ext.ns('ezScrum');

/* SaveProjectResult */
var CreateProjectResult = Ext.data.Record.create( [ 'Result' , 'IP','DBName']);

var createResultReader = new Ext.data.XmlReader( {
	record : 'CreateProjectResult'
}, CreateProjectResult);

var createResultStore = new Ext.data.Store( {
	fields : [ {
		name : 'Result'
	} ,
	{
		name:'IP'
	},
	{
		name:'DBName'
	}],
	reader : createResultReader
});

/*-----------------------------------------------------------
 *	顯示DataBase選單
 -------------------------------------------------------------*/
var sqlList = [ [ 'Default(Local Database)' ], [ 'MySQL' ] ];
// var sqlList = [['MySQL']];
var sqlDataType = new Ext.data.ArrayStore( {
	// store configs
	autoDestory : true,
	storeId : 'sqlDataType',
	// reader configs
	idIndex : 0,
	fields : [ 'SQLName' ],
	data : sqlList
});

var sqlCombo = new Ext.form.ComboBox( {
	store : sqlDataType,
	displayField : 'SQLName',
	typeAhead : true,
	mode : 'local',
	triggerAction : 'all',
	emptyText : 'Select a DataBase',
	selectOnFocus : true,
	allowBlank : false,
	name : 'SQLType'
});

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
				maxLength : 128
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
			}, sqlCombo, {

				fieldLabel : 'Server Url',
				id : 'ServerUrl',
				name : 'ServerUrl',
				allowBlank : false,
				disabled : true
			}, {
				name : 'ServicePath',
				originalValue : '/mantis/mc/mantisconnect.php',
				allowBlank : false,
                hidden:true
			}, {
				fieldLabel : 'Account of DB of ITS',
				name : 'DBAccount',
                id : 'DBAccount',
				allowBlank : false
			}, {
				fieldLabel : 'Password of DB of ITS',
				inputType : 'password',
				name : 'DBPassword',
                id:'DBPassword',
				allowBlank : false
			} , {
                fieldLabel : 'Database Name',
                name : 'DBName',
                id : 'DBName',
                originalValue:'ezScrum',
                //readOnly   : true,
                allowBlank : false
            }],
			buttons : [ {
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

		/*-----------------------------------------------------------
		 *  加入選擇不一樣的SQL類型時，出現不同的設定表格 
		-------------------------------------------------------------*/

		sqlCombo.on('select', function(sqlcombo, record, index) {
            var cArray = new Array();
            cArray.push(this.getComponent('ServerUrl'));
            cArray.push(this.getComponent('DBAccount'));
			cArray.push(this.getComponent('DBPassword'));
            cArray.push(this.getComponent('DBName'));

			var selectValue = record.get('SQLName');
			if (selectValue == 'Default(Local Database)') {
				for(var i=0;i<cArray.length;i++)
                {
                    cArray[i].disable();
                }
			} else {
				for(var i=0;i<cArray.length;i++)
                {
                    cArray[i].enable();
                }
			}
		}, this);

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateProjectForm.superclass.initComponent.apply(this,
				arguments);

		this.addEvents('CreateSuccess', 'CreateFailure', 'InitDatabase','CreateDatabase');
	},
	onRender : function() {
		ezScrum.CreateProjectForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function() {
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg : "Please wait..."
		});
		myMask.show();
		var form = this.getForm();
		var obj = this;
		Ext.Ajax.request( {
			url : this.url,
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
			this.fireEvent('CreateSuccess', this, response);
		else if (createResultStore.getAt(0).data['Result'] == "Failure")
			this.fireEvent('CreateFailure', this, response);
		else if (createResultStore.getAt(0).data['Result'] == "InitDatabase") {
			response.ip = createResultStore.getAt(0).data['IP'];
			response.dbName = createResultStore.getAt(0).data['DBName'];
			this.fireEvent('InitDatabase', this.getForm(), response);
		}
        else if(createResultStore.getAt(0).data['Result'] == "CreateDatabase")
        {
            response.ip = createResultStore.getAt(0).data['IP'];
            response.dbName = createResultStore.getAt(0).data['DBName'];
            this.fireEvent('CreateDatabase', this.getForm(), response);
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

ezScrum.CreateProjectWidget = Ext.extend(Ext.Window, {
	title : 'Create New Project',
	id : 'te',
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
		ezScrum.CreateProjectWidget.superclass.initComponent.apply(this,
				arguments);

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
        this.items.get(0).on('CreateDatabase',function(obj,response){
            this.fireEvent('CreateDatabase',this,obj,response)  
        },this);
	},
	showWidget : function(sprint) {
		this.items.get(0).reset();
		this.show();
	}
});
