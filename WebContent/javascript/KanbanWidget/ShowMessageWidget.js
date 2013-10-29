Ext.ns('ezScrum');

/* Show Message Widget */
ezScrum.ShowMessageWidget = Ext.extend(Ext.Window, {
	title:'Show Message',
	height:140,
	width:450,
	modal:false,
	text:'<p style="font-size:12pt">請先選擇 Item</p><br/>',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			items:{
				id:'label',
				xtype:'label',
				html:this.text
			},
			buttons:[
				{text:'OK',scope:this, handler:this.onOK}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ShowMessageWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('ShowOK');
	},
	showMessage:function(){
		this.get('label').setText(this.text, false);
		this.show();
	},
	showCustomMessage:function(message){
		this.get('label').setText('<p style="font-size:12pt">' + message + '</p>', false);
		this.show();
	},
	// OK action 關閉視窗
	onOK : function(){
		this.hide();
	}
});