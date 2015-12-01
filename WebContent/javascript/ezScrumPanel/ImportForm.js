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
				id:"files",
				width : '49.5%',
				xtype : 'fileuploadfield',
				fieldLabel : 'Choose file'
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
		//get golden answer file content!!
		var reader = new FileReader();
		reader.readAsText($("input[type='file']")[0].files[0]);
		//file content is in e.target.result
		reader.onloadend = function(e) {
		    $.ajax({
				url: "",
				method: "post",
				data:e.target.result
			}).done(function(msg){
				console.log(msg);
			}).fail(function(err){
				console.error(err);
			});
		};
	}
});

Ext.reg('ImportForm', ImportFormLayout);