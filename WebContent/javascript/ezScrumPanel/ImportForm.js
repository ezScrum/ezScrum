ImportFormLayout = Ext.extend(Ext.form.FormPanel, {
	id : 'Import_Form',
	border : false,
	frame : true,
	title : 'Import Page',
	bodyStyle : 'padding:0px;margin:0px',
	labelAlign : 'left',
	labelWidth : 80,
	buttonAlign : 'center',
	monitorValid : true,
	loadmask : null,
	initComponent : function() {
		var config = {
			url : 'import.do',
			modify_url : 'import.do',
			items : [ {
				width:'49.5%',
				xtype : 'fileuploadfield',
				fieldLabel : 'Choose file',
			} ],
			buttons : [ {
				formBind : true,
				scope : this,
				text : 'Import',
				disabled : true,
				handler : this.import
			} ]

		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ImportFormLayout.superclass.initComponent.apply(this, arguments);
	},
	import : function() {
		console.log($("input[type='file']")[0].value);
	}
});

Ext.reg('ImportForm', ImportFormLayout);