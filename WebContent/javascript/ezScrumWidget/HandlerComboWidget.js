Ext.ns('ezScrum');

ezScrum.HandlerComboWidget = Ext.extend(Ext.form.ComboBox, {
	editable		: false,
	triggerAction	: 'all',
	url				: 'AjaxGetHandlerList.do',
	forceSelection	: true,
	mode			: 'local',
	store			: HandlerComboStore,
	valueField		: 'Name',
	displayField	: 'Name',
	listeners : {
		'expand' : function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [ combo.el ]);
		},
		'collapse' : function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [ combo.el ]);
		},
		'render': function() {
			this.loadDataModel();
		}
	},
	loadDataModel: function() {
		var obj = this;
		obj.selectedIndex = 0 ;
		obj.reset();
		Ext.Ajax.request({
			url: obj.url,
			success:function(response){
				obj.store.loadData(response.responseXML);		// get handler info
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	setNewUrl: function(new_url) {
		this.url = new_url;
		this.loadDataModel();
	}
});

Ext.reg('HandlerComboBox', ezScrum.HandlerComboWidget);