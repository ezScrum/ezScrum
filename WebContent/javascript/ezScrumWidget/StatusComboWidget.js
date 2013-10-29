Ext.ns('ezScrum')

ezScrum.StatusComboWidget = Ext.extend(Ext.form.ComboBox, {
	editable: false,
	triggerAction: 'all',
	forceSelection: true,
	mode: 'local',
	store: new Ext.data.ArrayStore({
        fields: ['Status'],
        data: [['new'],['assigned'],['closed']]
    }),
	valueField: 'Status',
    displayField: 'Status',
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
		}
	}
});

Ext.reg('StatusComboBox', ezScrum.StatusComboWidget);